package org.example.repository;

import org.example.misc.Conexion;
import org.example.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del repositorio de usuarios para MySQL.
 *
 * <p>
 * Gestiona la persistencia de {@link Usuario} en la tabla {@code usuario}
 * de la base de datos.
 * </p>
 *
 * <p><b>Columnas de la tabla usuario:</b> id, nombre, identificacion, tipo</p>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link IUsuarioRepository}.</p>
 *
 * @see IUsuarioRepository
 * @see Conexion
 */
public class UsuarioRepositoryMySQL implements IUsuarioRepository {

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario Usuario a guardar.
     * @throws RuntimeException Si ocurre un error SQL al insertar.
     */
    @Override
    public void guardar(Usuario usuario) {
        String sql = "INSERT INTO usuario (id, nombre, identificacion, tipo) VALUES (?, ?, ?, ?)";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuario.getId());
            statement.setString(2, usuario.getNombre());
            statement.setString(3, usuario.getIdentificacion());
            statement.setString(4, usuario.getTipo());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar usuario en MySQL", e);
        }
    }

    /**
     * Retorna todos los usuarios registrados en la base de datos.
     *
     * @return Lista de usuarios. Puede estar vacia si no hay registros.
     * @throws RuntimeException Si ocurre un error de consulta.
     */
    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, identificacion, tipo FROM usuario";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(new Usuario(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("identificacion"),
                        resultSet.getString("tipo")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener usuarios de MySQL", e);
        }

        return usuarios;
    }

    /**
     * Busca un usuario por su ID en la base de datos.
     *
     * @param id ID del usuario a buscar.
     * @return El usuario encontrado, o {@code null} si no existe.
     * @throws RuntimeException Si ocurre un error de consulta.
     */
    @Override
    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, nombre, identificacion, tipo FROM usuario WHERE id = ?";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Usuario(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getString("identificacion"),
                            resultSet.getString("tipo")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por ID en MySQL", e);
        }

        return null;
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * <p>
     * Si no hay filas afectadas, lanza excepcion indicando que el usuario no existe.
     * </p>
     *
     * @param usuario Usuario con los nuevos datos y el mismo ID original.
     * @throws RuntimeException Si el usuario no existe o hay error SQL.
     */
    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre = ?, identificacion = ?, tipo = ? WHERE id = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getIdentificacion());
            statement.setString(3, usuario.getTipo());
            statement.setInt(4, usuario.getId());

            int filas = statement.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No existe el usuario a actualizar en MySQL.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario en MySQL", e);
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no existia.
     * @throws RuntimeException Si ocurre un error al eliminar.
     */
    @Override
    public boolean eliminarPorId(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario en MySQL", e);
        }
    }
}
