package org.example.controller;

import org.example.model.Autor;
import org.example.service.IBibliotecaService;
import org.example.view.AutorView;
import org.example.view.BibliotecaView;

import java.util.List;

/**
 * Controlador del modulo de autores.
 */
public class AutorController {

    private final AutorView view;
    private final IBibliotecaService service;

    /** Crea el controlador de autores con su vista especializada. */
    public AutorController(BibliotecaView view, IBibliotecaService service) {
        this.view = view.getAutorView();
        this.service = service;
    }

    /** Inicia el menu del modulo de autores. */
    public void iniciarModulo() {
        int opcion;
        do {
            mostrarMenuModulo();
            opcion = view.leerOpcionModulo();
            switch (opcion) {
                case 1 -> registrarAutor();
                case 2 -> editarAutor();
                case 3 -> eliminarAutor();
                case 4 -> mostrarAutores();
                case 0 -> view.mostrarMensaje("Volviendo al menu principal.");
                default -> view.mostrarError("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    /** Muestra el menu de acciones del modulo de autores. */
    private void mostrarMenuModulo() {
        System.out.println("\n===== MODULO AUTORES =====");
        System.out.println("1. Registrar Autor");
        System.out.println("2. Editar Autor");
        System.out.println("3. Eliminar Autor");
        System.out.println("4. Mostrar Autores");
        System.out.println("0. Volver");
    }

    /** Solicita datos y registra un autor. */
    public void registrarAutor() {
        int id = view.leerIdAutor();
        if (existeAutor(id)) {
            view.mostrarError("Ya existe un autor con ese ID.");
            return;
        }
        String nombre = view.leerNombreAutor();
        String nacionalidad = view.leerNacionalidad();
        int anio = view.leerAnioNacimiento();

        try {
            service.registrarAutor(id, nombre, nacionalidad, anio);
            view.mostrarRegistroExitoso();
        } catch (Exception e) {
            view.mostrarError("al registrar autor: " + e.getMessage());
        }
    }

    /** Solicita datos y edita un autor existente. */
    public void editarAutor() {
        List<Autor> autores = service.getAutores();
        if (autores.isEmpty()) {
            view.mostrarError("No hay autores registrados.");
            return;
        }

        int id = view.leerIdAutor();
        if (!existeAutor(id)) {
            view.mostrarError("No existe un autor con ese ID.");
            return;
        }

        String nombre = view.leerNombreAutor();
        String nacionalidad = view.leerNacionalidad();
        int anio = view.leerAnioNacimiento();

        try {
            service.editarAutor(id, nombre, nacionalidad, anio);
            view.mostrarMensaje("Autor editado correctamente.");
        } catch (Exception e) {
            view.mostrarError("al editar autor: " + e.getMessage());
        }
    }

    /** Elimina un autor por ID. */
    public void eliminarAutor() {
        List<Autor> autores = service.getAutores();
        if (autores.isEmpty()) {
            view.mostrarError("No hay autores registrados.");
            return;
        }

        int id = view.leerIdAutor();
        if (!existeAutor(id)) {
            view.mostrarError("No existe un autor con ese ID.");
            return;
        }

        try {
            service.eliminarAutor(id);
            view.mostrarMensaje("Autor eliminado correctamente.");
        } catch (Exception e) {
            view.mostrarError("al eliminar autor: " + e.getMessage());
        }
    }

    /** Muestra todos los autores registrados. */
    public void mostrarAutores() {
        List<Autor> lista = service.getAutores();
        if (lista.isEmpty()) {
            view.mostrarNoHayAutores();
            return;
        }
        view.mostrarListado(lista);
    }

    /** Verifica si existe un autor con el ID indicado. */
    private boolean existeAutor(int id) {
        return service.getAutores().stream().anyMatch(a -> a.getId() == id);
    }
}
