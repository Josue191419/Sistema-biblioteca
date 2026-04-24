package org.example.repository;

import org.example.model.Bibliotecario;

import java.util.List;

/**
 * Contrato de persistencia para {@link Bibliotecario}.
 */
public interface IBibliotecarioRepository {
    /** Guarda un bibliotecario. */
    void guardar(Bibliotecario bibliotecario);
    /** Busca por identificacion unica. */
    Bibliotecario buscarPorIdentificacion(String identificacion);
    /** Busca por ID numerico cuando la implementacion lo soporte. */
    Bibliotecario buscarPorId(int id);
    /** Retorna todos los bibliotecarios. */
    List<Bibliotecario> obtenerTodos();
    /** Asigna la misma biblioteca a todos los bibliotecarios. */
    void asignarBibliotecaATodos(String nombreBiblioteca);
    /** Actualiza un bibliotecario existente. */
    void actualizar(Bibliotecario bibliotecario);
    /** Elimina por ID numerico cuando la implementacion lo soporte. */
    boolean eliminarPorId(int id);
    /** Elimina por identificacion unica. */
    boolean eliminarPorIdentificacion(String identificacion);
}
