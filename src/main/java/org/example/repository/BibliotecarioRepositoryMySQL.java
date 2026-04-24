package org.example.repository;

import org.example.misc.Conexion;
import org.example.model.Biblioteca;
import org.example.model.Bibliotecario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del repositorio de bibliotecarios para MySQL.
 *
 * <p>
 * Gestiona la persistencia de {@link Bibliotecario} en la tabla {@code bibliotecario}
 * de la base de datos. Usa un JOIN con la tabla {@code biblioteca} para obtener
 * los datos completos de la biblioteca asignada.
 * </p>
 *
 * <p><b>Columnas de la tabla bibliotecario:</b>
 * identificacion, nombre, turno, biblioteca_nombre</p>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link IBibliotecarioRepository}.</p>
 *
 * @author Josue
 * @version 1.0
 * @see IBibliotecarioRepository
 * @see Conexion
 */
public class BibliotecarioRepositoryMySQL implements IBibliotecarioRepository {

    /**
     * Guarda un nuevo bibliotecario en la base de datos.
     *
     * <p>
     * Si el bibliotecario no tiene biblioteca asignada, se guarda NULL
     * en la columna {@code biblioteca_nombre}.
     * </p>
     *
     * @param bibliotecario Bibliotecario a guardar.
     * @throws RuntimeException Si ocurre un error SQL al insertar.
     */
    @Override
    public void guardar(Bibliotecario bibliotecario) {
        String sql = "INSERT INTO bibliotecario (identificacion, nombre, turno, biblioteca_nombre) VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bibliotecario.getIdentificacion());
            statement.setString(2, bibliotecario.getNombre());
            statement.setString(3, bibliotecario.getTurno());
            if (bibliotecario.getBibliotecaAsignada() != null) {
                statement.setString(4, bibliotecario.getBibliotecaAsignada().getNombre());
            } else {
                statement.setNull(4, java.sql.Types.VARCHAR);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar bibliotecario en MySQL", e);
        }
    }

    /**
     * Busca un bibliotecario por su identificacion unica.
     *
     * <p>
     * Si la columna {@code biblioteca_nombre} tiene valor, se crea un objeto
     * {@link Biblioteca} basico con ese nombre para asignar al bibliotecario.
     * </p>
     *
     * @param identificacion Identificacion del bibliotecario a buscar.
     * @return El bibliotecario encontrado, o {@code null} si no existe.
     * @throws RuntimeException Si ocurre un error de consulta.
     */
    @Override
    public Bibliotecario buscarPorIdentificacion(String identificacion) {
        String sql = "SELECT identificacion, nombre, turno, biblioteca_nombre FROM bibliotecario WHERE identificacion = ?";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, identificacion);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Bibliotecario b = new Bibliotecario(
                            rs.getString("nombre"),
                            rs.getString("identificacion"),
                            rs.getString("turno")
                    );
                    String nombreBiblioteca = rs.getString("biblioteca_nombre");
                    if (nombreBiblioteca != null && !nombreBiblioteca.isEmpty()) {
                        b.setBibliotecaAsignada(new Biblioteca(nombreBiblioteca, "", "", 0));
                    }
                    return b;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar bibliotecario por identificacion", e);
        }
    }

    /**
     * Retorna todos los bibliotecarios con datos completos de su biblioteca asignada.
     *
     * <p>
     * Realiza un LEFT JOIN con la tabla {@code biblioteca} para obtener la direccion,
     * telefono y capacidad de la biblioteca asignada (si existe).
     * </p>
     *
     * @return Lista de bibliotecarios. Puede estar vacia si no hay registros.
     * @throws RuntimeException Si ocurre un error de consulta.
     */
    @Override
    public List<Bibliotecario> obtenerTodos() {
        List<Bibliotecario> lista = new ArrayList<>();
        String sql = "SELECT b.identificacion, b.nombre, b.turno, " +
                "bi.nombre AS biblioteca_nombre, bi.direccion, bi.telefono, bi.capacidad " +
                "FROM bibliotecario b " +
                "LEFT JOIN biblioteca bi ON bi.nombre = b.biblioteca_nombre";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Bibliotecario b = new Bibliotecario(
                        rs.getString("nombre"),
                        rs.getString("identificacion"),
                        rs.getString("turno")
                );

                String nombreBiblioteca = rs.getString("biblioteca_nombre");
                if (nombreBiblioteca != null && !nombreBiblioteca.isEmpty()) {
                    b.setBibliotecaAsignada(new Biblioteca(
                            nombreBiblioteca,
                            rs.getString("direccion"),
                            rs.getString("telefono"),
                            rs.getInt("capacidad")
                    ));
                }
                lista.add(b);
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener bibliotecarios de MySQL", e);
        }
    }

    /**
     * Actualiza la URL de biblioteca asignada para todos los bibliotecarios registrados.
     *
     * <p>
     * Esto se ejecuta automaticamente al crear o actualizar la biblioteca activa,
     * para mantener la coherencia entre la tabla bibliotecario y la biblioteca activa.
     * </p>
     *
     * @param nombreBiblioteca Nombre de la biblioteca que se asignara a todos.
     * @throws RuntimeException Si ocurre un error al actualizar.
     */
    @Override
    public void asignarBibliotecaATodos(String nombreBiblioteca) {
        String sql = "UPDATE bibliotecario SET biblioteca_nombre = ?";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombreBiblioteca);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar biblioteca a bibliotecarios en MySQL", e);
        }
    }

    /**
     * Busca un bibliotecario por ID numerico.
     *
     * <p>
     * <b>Nota:</b> La tabla usa {@code identificacion} (String) como clave primaria.
     * Este metodo no esta implementado. Se recomienda usar
     * {@link #buscarPorIdentificacion(String)} en su lugar.
     * </p>
     *
     * @param id ID numerico (no usado).
     * @return Siempre retorna {@code null}.
     */
    @Override
    public Bibliotecario buscarPorId(int id) {
        // En este caso, el ID se puede buscar por identificacion que es más lógico
        // Si necesitas búsqueda por ID numérico, se requiere agregar columna ID a la BD
        return null;
    }

    /**
     * Actualiza el nombre, turno y biblioteca asignada de un bibliotecario existente.
     *
     * <p>
     * La busqueda se realiza por {@code identificacion} (clave primaria logica).
     * Si no hay filas afectadas, se lanza excepcion indicando que no existe el registro.
     * </p>
     *
     * @param bibliotecario Bibliotecario con los nuevos datos y la misma identificacion.
     * @throws RuntimeException Si el bibliotecario no existe o hay error SQL.
     */
    @Override
    public void actualizar(Bibliotecario bibliotecario) {
        String sql = "UPDATE bibliotecario SET nombre = ?, turno = ?, biblioteca_nombre = ? WHERE identificacion = ?";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bibliotecario.getNombre());
            statement.setString(2, bibliotecario.getTurno());
            if (bibliotecario.getBibliotecaAsignada() != null) {
                statement.setString(3, bibliotecario.getBibliotecaAsignada().getNombre());
            } else {
                statement.setNull(3, java.sql.Types.VARCHAR);
            }
            statement.setString(4, bibliotecario.getIdentificacion());
            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No existe el bibliotecario a actualizar en MySQL.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar bibliotecario en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un bibliotecario por ID numerico.
     *
     * <p>
     * <b>Nota:</b> No implementado porque la tabla usa {@code identificacion}
     * como clave primaria logica. Usar {@link #eliminarPorIdentificacion(String)}.
     * </p>
     *
     * @param id ID numerico (no usado).
     * @return Siempre retorna {@code false}.
     */
    @Override
    public boolean eliminarPorId(int id) {
        // Para consistencia, usar identificacion
        return false;
    }

    /**
     * Elimina un bibliotecario por su identificacion unica.
     *
     * @param identificacion Identificacion del bibliotecario a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no existia.
     * @throws RuntimeException Si ocurre un error al eliminar.
     */
    @Override
    public boolean eliminarPorIdentificacion(String identificacion) {
        String sql = "DELETE FROM bibliotecario WHERE identificacion = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identificacion);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar bibliotecario en MySQL: " + e.getMessage(), e);
        }
    }
}
