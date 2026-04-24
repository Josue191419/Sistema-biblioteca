package org.example.repository;

import org.example.model.Prestamo;

import java.util.List;

/**
 * Contrato de persistencia para {@link Prestamo}.
 */
public interface IPrestamoRepository {
    /** Guarda un prestamo. */
    void guardar(Prestamo prestamo);

    /** Retorna todos los prestamos. */
    List<Prestamo> obtenerTodos();

    /** Busca un prestamo por ID. */
    Prestamo buscarPorId(int id);

    /** Actualiza un prestamo existente. */
    void actualizar(Prestamo prestamo);

    /** Elimina un prestamo por ID. */
    boolean eliminarPorId(int id);
}
