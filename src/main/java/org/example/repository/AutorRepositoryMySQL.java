package org.example.repository;

import org.example.misc.Conexion;
import org.example.model.Autor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del repositorio de autores para MySQL.
 *
 * <p>
 * Gestiona la persistencia de {@link Autor} en la tabla {@code autor} de la base de datos.
 * Si el anio de nacimiento es 0 (no definido), se inserta como NULL en la base de datos.
 * </p>
 *
 * <p><b>Columnas de la tabla autor:</b> id, nombre, nacionalidad, anio_nacimiento</p>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link IAutorRepository}.</p>
 *
 * @author Josue
 * @version 1.0
 * @see IAutorRepository
 * @see Conexion
 */
public class AutorRepositoryMySQL implements IAutorRepository {

    /**
     * Guarda un nuevo autor en la base de datos.
     *
     * <p>
     * Si el anio de nacimiento del autor es 0, se inserta NULL en la columna
     * {@code anio_nacimiento}. Si el ID ya existe (SQLState 23000), lanza excepcion.
     * </p>
     *
     * @param autor Autor a guardar.
     * @throws RuntimeException Si el ID ya existe o si ocurre un error SQL.
     */
    @Override
    public void guardar(Autor autor) {
        String sql = "INSERT INTO autor (id, nombre, nacionalidad, anio_nacimiento) VALUES (?, ?, ?, ?)";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, autor.getId());
            statement.setString(2, autor.getNombre());
            statement.setString(3, autor.getNacionalidad());
            if (autor.getAnioNacimiento() > 0) {
                statement.setInt(4, autor.getAnioNacimiento());
            } else {
                statement.setNull(4, java.sql.Types.INTEGER);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                throw new RuntimeException("El autor con ID " + autor.getId() + " ya existe.", e);
            }
            throw new RuntimeException("Error al guardar autor en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna todos los autores registrados en la base de datos.
     *
     * <p>
     * Si {@code anio_nacimiento} es NULL en la BD, {@code getInt()} retorna 0 por convencion.
     * </p>
     *
     * @return Lista de autores. Puede estar vacia si no hay registros.
     * @throws RuntimeException Si ocurre un error de consulta.
     */
    @Override
    public List<Autor> obtenerTodos() {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT id, nombre, nacionalidad, anio_nacimiento FROM autor";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Autor autor = new Autor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("nacionalidad"),
                        rs.getInt("anio_nacimiento")   // 0 si es NULL en BD
                );
                autores.add(autor);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener autores de MySQL: " + e.getMessage(), e);
        }
        return autores;
    }

    /**
     * Busca un autor por su ID en la base de datos.
     *
     * @param id ID del autor a buscar.
     * @return El autor encontrado, o {@code null} si no existe.
     * @throws RuntimeException Si ocurre un error de consulta.
     */
    @Override
    public Autor buscarPorId(int id) {
        String sql = "SELECT id, nombre, nacionalidad, anio_nacimiento FROM autor WHERE id = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Autor(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("nacionalidad"),
                            rs.getInt("anio_nacimiento")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar autor por ID en MySQL: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Busca un autor por nombre (ignorando mayusculas/minusculas y espacios laterales).
     *
     * @param nombre Nombre del autor a buscar.
     * @return El autor encontrado, o {@code null} si no existe.
     */
    @Override
    public Autor buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT id, nombre, nacionalidad, anio_nacimiento FROM autor "
                + "WHERE LOWER(TRIM(nombre)) = LOWER(TRIM(?)) LIMIT 1";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Autor(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("nacionalidad"),
                            rs.getInt("anio_nacimiento")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar autor por nombre en MySQL: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Actualiza los datos de un autor existente.
     *
     * <p>
     * Si el anio de nacimiento es 0, se guarda NULL en la base de datos.
     * Si no hay filas afectadas, se lanza una excepcion indicando que el autor no existe.
     * </p>
     *
     * @param autor Autor con los nuevos datos y el mismo ID original.
     * @throws RuntimeException Si el autor no existe o si ocurre un error SQL.
     */
    @Override
    public void actualizar(Autor autor) {
        String sql = "UPDATE autor SET nombre = ?, nacionalidad = ?, anio_nacimiento = ? WHERE id = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, autor.getNombre());
            statement.setString(2, autor.getNacionalidad());
            if (autor.getAnioNacimiento() > 0) {
                statement.setInt(3, autor.getAnioNacimiento());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            statement.setInt(4, autor.getId());
            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No existe el autor a actualizar en MySQL.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar autor en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un autor por su ID.
     *
     * @param id ID del autor a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no existia.
     * @throws RuntimeException Si ocurre un error al eliminar.
     */
    @Override
    public boolean eliminarPorId(int id) {
        String sql = "DELETE FROM autor WHERE id = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar autor en MySQL: " + e.getMessage(), e);
        }
    }
}
