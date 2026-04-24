package org.example.repository;

import org.example.model.Usuario;

import java.util.List;

/**
 * Contrato de persistencia para {@link Usuario}.
 */
public interface IUsuarioRepository {
    /** Guarda un usuario. */
    void guardar(Usuario usuario);

    /** Retorna todos los usuarios. */
    List<Usuario> obtenerTodos();

    /** Busca un usuario por ID. */
    Usuario buscarPorId(int id);

    /** Actualiza un usuario existente. */
    void actualizar(Usuario usuario);

    /** Elimina un usuario por ID. */
    boolean eliminarPorId(int id);
}
