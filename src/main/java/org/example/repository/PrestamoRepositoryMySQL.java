package org.example.repository;

import org.example.misc.Conexion;
import org.example.model.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del repositorio de prestamos para MySQL.
 *
 * <p>
 * Gestiona la persistencia de {@link Prestamo} en la tabla {@code prestamo} de la base de datos.
 * Implementa un mecanismo de <b>fallback por columnas faltantes</b>: si la tabla no tiene
 * las columnas {@code biblioteca_nombre} o {@code bibliotecario_identificacion}, se usan
 * consultas SQL mas simples que solo requieren las columnas basicas.
 * </p>
 *
 * <p><b>Columnas de la tabla prestamo (completo):</b></p>
 * <ul>
 *   <li>id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion,
 *       biblioteca_nombre, bibliotecario_identificacion</li>
 * </ul>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link IPrestamoRepository}.</p>
 *
 * @author Josue
 * @version 2.0
 * @see IPrestamoRepository
 * @see Conexion
 */
public class PrestamoRepositoryMySQL implements IPrestamoRepository {

    /**
     * Guarda un nuevo prestamo en MySQL.
     *
     * <p>
     * Primero intenta insertar con todas las columnas (incluyendo biblioteca y bibliotecario).
     * Si la tabla no tiene esas columnas ({@code columnaFaltante()}), usa consultas simplificadas
     * con o sin fecha de devolucion segun la estructura de la tabla.
     * Si el ID del prestamo ya existe (SQLState 23000), lanza una excepcion descriptiva.
     * </p>
     *
     * @param prestamo Prestamo a guardar.
     * @throws RuntimeException Si ocurre un error al insertar o hay conflicto de clave primaria.
     */
    @Override
    public void guardar(Prestamo prestamo) {
        String sqlConTodo = "INSERT INTO prestamo (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, biblioteca_nombre, bibliotecario_identificacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlConDevolucion = "INSERT INTO prestamo (id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?, ?, ?)";
        String sqlBasico = "INSERT INTO prestamo (id, libro_id, usuario_id, fecha_prestamo) VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlConTodo)) {
                statement.setInt(1, prestamo.getId());
                statement.setInt(2, prestamo.getLibroId());
                statement.setInt(3, prestamo.getUsuarioId());
                statement.setDate(4, Date.valueOf(prestamo.getFechaPrestamo()));
                statement.setDate(5, Date.valueOf(prestamo.getFechaDevolucion()));
                statement.setString(6, prestamo.getBibliotecaNombre());
                statement.setString(7, prestamo.getBibliotecarioIdentificacion());
                statement.executeUpdate();
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    guardarConFallback(connection, prestamo, sqlConDevolucion, sqlBasico);
                } else if ("23000".equals(e.getSQLState())) {
                    throw new RuntimeException("No se pudo guardar el prestamo. Verifica si el ID ya existe o si libro/usuario no existen.", e);
                } else {
                    throw new RuntimeException("Error al guardar prestamo en MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar prestamo en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna todos los prestamos de la base de datos.
     *
     * <p>
     * Intenta recuperar todas las columnas incluyendo biblioteca y bibliotecario.
     * Si alguna columna no existe, usa consultas de fallback con menos columnas.
     * </p>
     *
     * @return Lista con todos los prestamos. Puede estar vacia si no hay registros.
     * @throws RuntimeException Si ocurre un error de conexion o consulta.
     */
    @Override
    public List<Prestamo> obtenerTodos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sqlConTodo = "SELECT id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, biblioteca_nombre, bibliotecario_identificacion FROM prestamo";
        String sqlConDevolucion = "SELECT id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion FROM prestamo";
        String sqlBasico = "SELECT id, libro_id, usuario_id, fecha_prestamo FROM prestamo";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlConTodo);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    prestamos.add(mapearPrestamoCompleto(resultSet));
                }
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    cargarConFallback(connection, prestamos, sqlConDevolucion, sqlBasico);
                } else {
                    throw new RuntimeException("Error al obtener prestamos de MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener prestamos de MySQL: " + e.getMessage(), e);
        }

        return prestamos;
    }

    /**
     * Busca un prestamo especifico por su ID en la base de datos.
     *
     * <p>
     * Igual que {@code obtenerTodos()}, aplica fallback de columnas si es necesario.
     * </p>
     *
     * @param id ID del prestamo a buscar.
     * @return El prestamo encontrado, o {@code null} si no existe.
     * @throws RuntimeException Si ocurre un error de consulta.
     */
    @Override
    public Prestamo buscarPorId(int id) {
        String sqlConTodo = "SELECT id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion, biblioteca_nombre, bibliotecario_identificacion FROM prestamo WHERE id = ?";
        String sqlConDevolucion = "SELECT id, libro_id, usuario_id, fecha_prestamo, fecha_devolucion FROM prestamo WHERE id = ?";
        String sqlBasico = "SELECT id, libro_id, usuario_id, fecha_prestamo FROM prestamo WHERE id = ?";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlConTodo)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapearPrestamoCompleto(resultSet);
                    }
                }
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    return buscarConFallback(connection, id, sqlConDevolucion, sqlBasico);
                } else {
                    throw new RuntimeException("Error al buscar prestamo por ID en MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar prestamo por ID en MySQL: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Actualiza los datos de un prestamo existente en MySQL.
     *
     * <p>
     * Aplica fallback de columnas si la tabla no tiene {@code biblioteca_nombre}
     * o {@code bibliotecario_identificacion}.
     * </p>
     *
     * @param prestamo Prestamo con los nuevos datos y el mismo ID original.
     * @throws RuntimeException Si ocurre un error al actualizar.
     */
    @Override
    public void actualizar(Prestamo prestamo) {
        String sqlConTodo = "UPDATE prestamo SET libro_id = ?, usuario_id = ?, fecha_prestamo = ?, fecha_devolucion = ?, biblioteca_nombre = ?, bibliotecario_identificacion = ? WHERE id = ?";
        String sqlConDevolucion = "UPDATE prestamo SET libro_id = ?, usuario_id = ?, fecha_prestamo = ?, fecha_devolucion = ? WHERE id = ?";
        String sqlBasico = "UPDATE prestamo SET libro_id = ?, usuario_id = ?, fecha_prestamo = ? WHERE id = ?";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlConTodo)) {
                statement.setInt(1, prestamo.getLibroId());
                statement.setInt(2, prestamo.getUsuarioId());
                statement.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                statement.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                statement.setString(5, prestamo.getBibliotecaNombre());
                statement.setString(6, prestamo.getBibliotecarioIdentificacion());
                statement.setInt(7, prestamo.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    actualizarConFallback(connection, prestamo, sqlConDevolucion, sqlBasico);
                } else {
                    throw new RuntimeException("Error al actualizar prestamo en MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar prestamo en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un prestamo por su ID.
     *
     * @param id ID del prestamo a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no existia.
     * @throws RuntimeException Si ocurre un error al eliminar.
     */
    @Override
    public boolean eliminarPorId(int id) {
        String sql = "DELETE FROM prestamo WHERE id = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar prestamo en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea una fila del ResultSet a un Prestamo completo (con todas las columnas).
     * Incluye biblioteca_nombre y bibliotecario_identificacion.
     *
     * @param resultSet ResultSet posicionado en la fila a mapear.
     * @return Objeto Prestamo poblado.
     * @throws SQLException Si ocurre un error al leer el ResultSet.
     */
    private Prestamo mapearPrestamoCompleto(ResultSet resultSet) throws SQLException {
        Prestamo prestamo = new Prestamo(
                resultSet.getInt("id"),
                resultSet.getInt("libro_id"),
                resultSet.getInt("usuario_id"),
                resultSet.getDate("fecha_prestamo").toLocalDate()
        );
        Date fechaDevolucion = resultSet.getDate("fecha_devolucion");
        if (fechaDevolucion != null) {
            prestamo.setFechaDevolucion(fechaDevolucion.toLocalDate());
        }
        prestamo.setBibliotecaNombre(resultSet.getString("biblioteca_nombre"));
        prestamo.setBibliotecarioIdentificacion(resultSet.getString("bibliotecario_identificacion"));
        return prestamo;
    }

    /**
     * Mapea una fila del ResultSet a un Prestamo sin las nuevas columnas extra.
     * Usado cuando la tabla no tiene biblioteca_nombre ni bibliotecario_identificacion.
     *
     * @param resultSet ResultSet posicionado en la fila a mapear.
     * @return Objeto Prestamo parcialmente poblado.
     * @throws SQLException Si ocurre un error al leer el ResultSet.
     */
    private Prestamo mapearPrestamoSinNuevasColumnas(ResultSet resultSet) throws SQLException {
        Prestamo prestamo = new Prestamo(
                resultSet.getInt("id"),
                resultSet.getInt("libro_id"),
                resultSet.getInt("usuario_id"),
                resultSet.getDate("fecha_prestamo").toLocalDate()
        );
        Date fechaDevolucion = resultSet.getDate("fecha_devolucion");
        if (fechaDevolucion != null) {
            prestamo.setFechaDevolucion(fechaDevolucion.toLocalDate());
        }
        return prestamo;
    }

    /**
     * Detecta si el error SQL indica que una columna no existe en la tabla.
     * Soporta mensajes de MySQL ("Unknown column") y otros motores ("doesn't exist").
     *
     * @param e Excepcion SQL producida.
     * @return {@code true} si el error es por columna faltante.
     */
    private boolean columnaFaltante(SQLException e) {
        return e.getMessage() != null && (e.getMessage().contains("Unknown column") || e.getMessage().contains("doesn't exist"));
    }

    /**
     * Intenta guardar el prestamo sin las columnas extra (biblioteca/bibliotecario).
     * Primero intenta con fecha_devolucion; si tambien falla, usa solo los campos basicos.
     *
     * @param connection      Conexion activa a reutilizar.
     * @param prestamo        Prestamo a guardar.
     * @param sqlConDevolucion SQL con fecha_devolucion pero sin columnas extra.
     * @param sqlBasico       SQL con solo id, libro_id, usuario_id, fecha_prestamo.
     * @throws SQLException Si ocurre un error que no es de columna faltante.
     */
    private void guardarConFallback(Connection connection, Prestamo prestamo, String sqlConDevolucion, String sqlBasico) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlConDevolucion)) {
            statement.setInt(1, prestamo.getId());
            statement.setInt(2, prestamo.getLibroId());
            statement.setInt(3, prestamo.getUsuarioId());
            statement.setDate(4, Date.valueOf(prestamo.getFechaPrestamo()));
            statement.setDate(5, Date.valueOf(prestamo.getFechaDevolucion()));
            statement.executeUpdate();
        } catch (SQLException e2) {
            if (columnaFaltante(e2)) {
                try (PreparedStatement statement = connection.prepareStatement(sqlBasico)) {
                    statement.setInt(1, prestamo.getId());
                    statement.setInt(2, prestamo.getLibroId());
                    statement.setInt(3, prestamo.getUsuarioId());
                    statement.setDate(4, Date.valueOf(prestamo.getFechaPrestamo()));
                    statement.executeUpdate();
                }
            } else {
                throw e2;
            }
        }
    }

    /**
     * Carga todos los prestamos sin las columnas extra.
     * Aplica dos niveles de fallback: con y sin fecha_devolucion.
     *
     * @param connection      Conexion activa a reutilizar.
     * @param prestamos       Lista donde se agregan los prestamos cargados.
     * @param sqlConDevolucion SQL con fecha_devolucion pero sin columnas extra.
     * @param sqlBasico       SQL basico.
     * @throws SQLException Si ocurre un error no relacionado con columnas faltantes.
     */
    private void cargarConFallback(Connection connection, List<Prestamo> prestamos, String sqlConDevolucion, String sqlBasico) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlConDevolucion);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                prestamos.add(mapearPrestamoSinNuevasColumnas(resultSet));
            }
        } catch (SQLException e2) {
            if (columnaFaltante(e2)) {
                try (PreparedStatement statement = connection.prepareStatement(sqlBasico);
                     ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        prestamos.add(new Prestamo(
                                resultSet.getInt("id"),
                                resultSet.getInt("libro_id"),
                                resultSet.getInt("usuario_id"),
                                resultSet.getDate("fecha_prestamo").toLocalDate()
                        ));
                    }
                }
            } else {
                throw e2;
            }
        }
    }

    /**
     * Busca un prestamo por ID sin usar las columnas extra.
     *
     * @param connection      Conexion activa a reutilizar.
     * @param id              ID del prestamo a buscar.
     * @param sqlConDevolucion SQL con fecha_devolucion pero sin columnas extra.
     * @param sqlBasico       SQL basico.
     * @return Prestamo encontrado o {@code null}.
     * @throws SQLException Si ocurre un error no relacionado con columnas faltantes.
     */
    private Prestamo buscarConFallback(Connection connection, int id, String sqlConDevolucion, String sqlBasico) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlConDevolucion)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearPrestamoSinNuevasColumnas(resultSet);
                }
            }
        } catch (SQLException e2) {
            if (columnaFaltante(e2)) {
                try (PreparedStatement statement = connection.prepareStatement(sqlBasico)) {
                    statement.setInt(1, id);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            return new Prestamo(
                                    resultSet.getInt("id"),
                                    resultSet.getInt("libro_id"),
                                    resultSet.getInt("usuario_id"),
                                    resultSet.getDate("fecha_prestamo").toLocalDate()
                            );
                        }
                    }
                }
            } else {
                throw e2;
            }
        }
        return null;
    }

    /**
     * Actualiza un prestamo sin usar las columnas extra.
     *
     * @param connection      Conexion activa a reutilizar.
     * @param prestamo        Prestamo con los nuevos datos.
     * @param sqlConDevolucion SQL con fecha_devolucion pero sin columnas extra.
     * @param sqlBasico       SQL basico.
     * @throws SQLException Si ocurre un error no relacionado con columnas faltantes.
     */
    private void actualizarConFallback(Connection connection, Prestamo prestamo, String sqlConDevolucion, String sqlBasico) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlConDevolucion)) {
            statement.setInt(1, prestamo.getLibroId());
            statement.setInt(2, prestamo.getUsuarioId());
            statement.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            statement.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            statement.setInt(5, prestamo.getId());
            statement.executeUpdate();
        } catch (SQLException e2) {
            if (columnaFaltante(e2)) {
                try (PreparedStatement statement = connection.prepareStatement(sqlBasico)) {
                    statement.setInt(1, prestamo.getLibroId());
                    statement.setInt(2, prestamo.getUsuarioId());
                    statement.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                    statement.setInt(4, prestamo.getId());
                    statement.executeUpdate();
                }
            } else {
                throw e2;
            }
        }
    }
}
