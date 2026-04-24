package org.example.repository;

import org.example.misc.Conexion;
import org.example.model.Libro;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion MySQL del repositorio de {@link Libro}.
 *
 * <p>Incluye estrategia de fallback para tablas antiguas que no tienen
 * columnas {@code editora} o {@code fecha_publicacion}.</p>
 */
public class LibroRepositoryMySQL implements ILibroRepository {

    /**
     * Guarda un libro. Si el ID ya existe, actualiza el registro.
     */
    @Override
    public void guardar(Libro libro) {
        Libro existente = buscarPorId(libro.getId());
        if (existente == null) {
            insertar(libro);
        } else {
            actualizarInterno(libro);
        }
    }

    /** Inserta un libro nuevo con SQL completo o basico segun estructura de tabla. */
    private void insertar(Libro libro) {
        String sqlCompleto = "INSERT INTO libro (id, titulo, autor, editora, fecha_publicacion, disponible) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlBasico = "INSERT INTO libro (id, titulo, autor, disponible) VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlCompleto)) {
                statement.setInt(1, libro.getId());
                statement.setString(2, libro.getTitulo());
                statement.setString(3, libro.getAutor());
                statement.setString(4, libro.getEditora());
                if (libro.getFechaPublicacion() != null) {
                    statement.setDate(5, Date.valueOf(libro.getFechaPublicacion()));
                } else {
                    statement.setNull(5, Types.DATE);
                }
                statement.setBoolean(6, libro.isDisponible());
                statement.executeUpdate();
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    try (PreparedStatement statement = connection.prepareStatement(sqlBasico)) {
                        statement.setInt(1, libro.getId());
                        statement.setString(2, libro.getTitulo());
                        statement.setString(3, libro.getAutor());
                        statement.setBoolean(4, libro.isDisponible());
                        statement.executeUpdate();
                    }
                } else if ("23000".equals(e.getSQLState())) {
                    throw new RuntimeException("El libro con ID " + libro.getId() + " ya existe.", e);
                } else {
                    throw new RuntimeException("Error al guardar libro en MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar libro en MySQL: " + e.getMessage(), e);
        }
    }

    /** Actualiza un libro existente con SQL completo o basico segun columnas disponibles. */
    private void actualizarInterno(Libro libro) {
        String sqlCompleto = "UPDATE libro SET titulo = ?, autor = ?, editora = ?, fecha_publicacion = ?, disponible = ? WHERE id = ?";
        String sqlBasico = "UPDATE libro SET titulo = ?, autor = ?, disponible = ? WHERE id = ?";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlCompleto)) {
                statement.setString(1, libro.getTitulo());
                statement.setString(2, libro.getAutor());
                statement.setString(3, libro.getEditora());
                if (libro.getFechaPublicacion() != null) {
                    statement.setDate(4, Date.valueOf(libro.getFechaPublicacion()));
                } else {
                    statement.setNull(4, Types.DATE);
                }
                statement.setBoolean(5, libro.isDisponible());
                statement.setInt(6, libro.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    try (PreparedStatement statement = connection.prepareStatement(sqlBasico)) {
                        statement.setString(1, libro.getTitulo());
                        statement.setString(2, libro.getAutor());
                        statement.setBoolean(3, libro.isDisponible());
                        statement.setInt(4, libro.getId());
                        statement.executeUpdate();
                    }
                } else {
                    throw new RuntimeException("Error al actualizar libro en MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar libro en MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna todos los libros desde MySQL.
     */
    @Override
    public List<Libro> obtenerTodos() {
        List<Libro> libros = new ArrayList<>();
        String sqlCompleto = "SELECT id, titulo, autor, editora, fecha_publicacion, disponible FROM libro";
        String sqlBasico = "SELECT id, titulo, autor, disponible FROM libro";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlCompleto);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Date fechaSql = resultSet.getDate("fecha_publicacion");
                    LocalDate fecha = fechaSql != null ? fechaSql.toLocalDate() : null;
                    Libro libro = new Libro(
                            resultSet.getInt("id"),
                            resultSet.getString("titulo"),
                            resultSet.getString("autor"),
                            resultSet.getString("editora"),
                            fecha
                    );
                    libro.setDisponible(resultSet.getBoolean("disponible"));
                    libros.add(libro);
                }
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    try (PreparedStatement statement = connection.prepareStatement(sqlBasico);
                         ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            Libro libro = new Libro(
                                    resultSet.getInt("id"),
                                    resultSet.getString("titulo"),
                                    resultSet.getString("autor")
                            );
                            libro.setDisponible(resultSet.getBoolean("disponible"));
                            libros.add(libro);
                        }
                    }
                } else {
                    throw new RuntimeException("Error al obtener libros de MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener libros de MySQL: " + e.getMessage(), e);
        }

        return libros;
    }

    /**
     * Busca un libro por su ID.
     */
    @Override
    public Libro buscarPorId(int id) {
        String sqlCompleto = "SELECT id, titulo, autor, editora, fecha_publicacion, disponible FROM libro WHERE id = ?";
        String sqlBasico = "SELECT id, titulo, autor, disponible FROM libro WHERE id = ?";

        try (Connection connection = Conexion.obtenerConexion()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlCompleto)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Date fechaSql = resultSet.getDate("fecha_publicacion");
                        LocalDate fecha = fechaSql != null ? fechaSql.toLocalDate() : null;
                        Libro libro = new Libro(
                                resultSet.getInt("id"),
                                resultSet.getString("titulo"),
                                resultSet.getString("autor"),
                                resultSet.getString("editora"),
                                fecha
                        );
                        libro.setDisponible(resultSet.getBoolean("disponible"));
                        return libro;
                    }
                }
            } catch (SQLException e) {
                if (columnaFaltante(e)) {
                    try (PreparedStatement statement = connection.prepareStatement(sqlBasico)) {
                        statement.setInt(1, id);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                Libro libro = new Libro(
                                        resultSet.getInt("id"),
                                        resultSet.getString("titulo"),
                                        resultSet.getString("autor")
                                );
                                libro.setDisponible(resultSet.getBoolean("disponible"));
                                return libro;
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("Error al buscar libro por ID en MySQL: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar libro por ID en MySQL: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Actualiza un libro existente.
     */
    @Override
    public void actualizar(Libro libro) {
        actualizarInterno(libro);
    }

    /**
     * Elimina un libro por ID.
     */
    @Override
    public boolean eliminarPorId(int id) {
        String sql = "DELETE FROM libro WHERE id = ?";
        try (Connection connection = Conexion.obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar libro en MySQL: " + e.getMessage(), e);
        }
    }

    /** Detecta errores SQL por columnas inexistentes para activar fallback. */
    private boolean columnaFaltante(SQLException e) {
        return e.getMessage() != null && (e.getMessage().contains("Unknown column") || e.getMessage().contains("doesn't exist"));
    }
}
