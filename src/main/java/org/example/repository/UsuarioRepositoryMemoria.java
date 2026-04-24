package org.example.repository;

import org.example.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryMemoria implements IUsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();

    @Override
    public void guardar(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }

    @Override
    public Usuario buscarPorId(int id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public void actualizar(Usuario usuarioActualizado) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() == usuarioActualizado.getId()) {
                usuarios.set(i, usuarioActualizado);
                return;
            }
        }
        throw new RuntimeException("No existe el usuario a actualizar en memoria.");
    }

    @Override
    public boolean eliminarPorId(int id) {
        return usuarios.removeIf(usuario -> usuario.getId() == id);
    }
}
