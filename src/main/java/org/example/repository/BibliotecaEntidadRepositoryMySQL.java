package org.example.repository;

import org.example.misc.Conexion;
import org.example.model.Biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementacion del repositorio de la entidad Biblioteca para MySQL.
 *
 * <p>
 * Gestiona la persistencia del objeto {@link Biblioteca} en la tabla {@code biblioteca}.
 * El sistema solo soporta una biblioteca activa a la vez: al guardar o actualizar,
 * se eliminan todos los registros previos y se inserta el nuevo dentro de una transaccion.
 * </p>
 *
 * <p><b>Columnas de la tabla biblioteca:</b> nombre, direccion, telefono, capacidad</p>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link IBibliotecaEntidadRepository}.</p>
 *
 * @author Josue
 * @version 1.0
 * @see IBibliotecaEntidadRepository
 * @see Conexion
 */
public class BibliotecaEntidadRepositoryMySQL implements IBibliotecaEntidadRepository {

    /**
     * Guarda o reemplaza la biblioteca activa en la base de datos.
     *
     * <p>
     * Ejecuta dentro de una transaccion:
     * <ol>
     *   <li>Elimina todos los registros de la tabla {@code biblioteca}.</li>
     *   <li>Inserta la nueva biblioteca.</li>
     *   <li>Confirma la transaccion (commit).</li>
     * </ol>
     * Si ocurre un error en cualquier paso, se realiza un rollback automatico.
     * </p>
     *
     * @param biblioteca Nueva biblioteca activa a guardar.
     * @throws RuntimeException Si ocurre un error SQL durante la transaccion.
     */
    @Override
    public void guardarOActualizar(Biblioteca biblioteca) {
        String deleteSql = "DELETE FROM biblioteca";
        String insertSql = "INSERT INTO biblioteca (nombre, direccion, telefono, capacidad) VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexion.obtenerConexion()) {
            connection.setAutoCommit(false);
            try (PreparedStatement deleteSt = connection.prepareStatement(deleteSql);
                 PreparedStatement insertSt = connection.prepareStatement(insertSql)) {

                deleteSt.executeUpdate();

                insertSt.setString(1, biblioteca.getNombre());
                insertSt.setString(2, biblioteca.getDireccion());
                insertSt.setString(3, biblioteca.getTelefono());
                insertSt.setInt(4, biblioteca.getCapacidad());
                insertSt.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar biblioteca en MySQL", e);
        }
    }

    /**
     * Obtiene la biblioteca activa almacenada en la base de datos.
     *
     * <p>
     * Usa {@code LIMIT 1} porque solo debe existir una biblioteca activa a la vez.
     * Si no existe ninguna, retorna {@code null}.
     * </p>
     *
     * @return La biblioteca activa, o {@code null} si no hay ninguna registrada.
     * @throws RuntimeException Si ocurre un error SQL al consultar.
     */
    @Override
    public Biblioteca obtenerActiva() {
        String sql = "SELECT nombre, direccion, telefono, capacidad FROM biblioteca LIMIT 1";

        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                return new Biblioteca(
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getInt("capacidad")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener biblioteca de MySQL", e);
        }
    }
}
