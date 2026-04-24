package org.example.repository;

import org.example.model.Biblioteca;
import org.example.model.Bibliotecario;

import java.util.ArrayList;
import java.util.List;

public class BibliotecarioRepositoryMemoria implements IBibliotecarioRepository {
    private final List<Bibliotecario> bibliotecarios = new ArrayList<>();

    @Override
    public void guardar(Bibliotecario bibliotecario) {
        bibliotecarios.add(bibliotecario);
    }

    @Override
    public Bibliotecario buscarPorIdentificacion(String identificacion) {
        for (Bibliotecario b : bibliotecarios) {
            if (b.getIdentificacion().equals(identificacion)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public Bibliotecario buscarPorId(int id) {
        // En memoria, usar identificacion como clave no es soportado con ID numérico
        return null;
    }

    @Override
    public List<Bibliotecario> obtenerTodos() {
        return new ArrayList<>(bibliotecarios);
    }

    @Override
    public void asignarBibliotecaATodos(String nombreBiblioteca) {
        for (Bibliotecario b : bibliotecarios) {
            b.setBibliotecaAsignada(new Biblioteca(nombreBiblioteca, "", "", 0));
        }
    }

    @Override
    public void actualizar(Bibliotecario bibliotecarioActualizado) {
        for (int i = 0; i < bibliotecarios.size(); i++) {
            if (bibliotecarios.get(i).getIdentificacion().equals(bibliotecarioActualizado.getIdentificacion())) {
                bibliotecarios.set(i, bibliotecarioActualizado);
                return;
            }
        }
        throw new RuntimeException("No existe el bibliotecario a actualizar en memoria.");
    }

    @Override
    public boolean eliminarPorId(int id) {
        // Para consistencia, usar identificacion
        return false;
    }

    @Override
    public boolean eliminarPorIdentificacion(String identificacion) {
        return bibliotecarios.removeIf(b -> b.getIdentificacion().equals(identificacion));
    }
}

