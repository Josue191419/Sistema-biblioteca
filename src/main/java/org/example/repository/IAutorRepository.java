package org.example.repository;

import org.example.model.Autor;
import java.util.List;

/**
 * Contrato de acceso a datos para {@link Autor}.
 */
public interface IAutorRepository {
    /** Guarda un autor nuevo. */
    void guardar(Autor autor);
    /** Retorna todos los autores almacenados. */
    List<Autor> obtenerTodos();
    /** Busca un autor por ID. */
    Autor buscarPorId(int id);
    /** Busca un autor por nombre (ignorando mayusculas/minusculas). */
    Autor buscarPorNombre(String nombre);
    /** Actualiza un autor existente. */
    void actualizar(Autor autor);
    /** Elimina un autor por ID. */
    boolean eliminarPorId(int id);
}
