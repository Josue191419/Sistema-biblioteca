package org.example.repository;

import org.example.model.Libro;

import java.util.List;

/**
 * Contrato de persistencia para {@link Libro}.
 */
public interface ILibroRepository {
    /** Guarda un libro nuevo o existente segun implementacion. */
    void guardar(Libro libro);

    /** Obtiene todos los libros almacenados. */
    List<Libro> obtenerTodos();

    /** Busca un libro por ID. */
    Libro buscarPorId(int id);

    /** Actualiza un libro existente. */
    void actualizar(Libro libro);

    /** Elimina un libro por ID. */
    boolean eliminarPorId(int id);
}

