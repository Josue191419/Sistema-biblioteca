package org.example.controller;

import org.example.model.Bibliotecario;
import org.example.service.IBibliotecaService;
import org.example.view.BibliotecaView;
import org.example.view.BibliotecarioView;

import java.util.List;

/**
 * Controlador del modulo de bibliotecarios.
 */
public class BibliotecarioController {

    private final BibliotecarioView view;
    private final IBibliotecaService service;

    /** Crea el controlador con su vista especializada. */
    public BibliotecarioController(BibliotecaView view, IBibliotecaService service) {
        this.view = view.getBibliotecarioView();
        this.service = service;
    }

    /** Inicia el menu del modulo de bibliotecarios. */
    public void iniciarModulo() {
        int opcion;
        do {
            mostrarMenuModulo();
            opcion = view.leerOpcionModulo();
            switch (opcion) {
                case 1 -> registrarBibliotecario();
                case 2 -> editarBibliotecario();
                case 3 -> eliminarBibliotecario();
                case 4 -> mostrarBibliotecarios();
                case 0 -> view.mostrarMensaje("Volviendo al menu principal.");
                default -> view.mostrarError("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    /** Muestra el menu de acciones del modulo de bibliotecarios. */
    private void mostrarMenuModulo() {
        System.out.println("\n===== MODULO BIBLIOTECARIOS =====");
        System.out.println("1. Registrar Bibliotecario");
        System.out.println("2. Editar Bibliotecario");
        System.out.println("3. Eliminar Bibliotecario");
        System.out.println("4. Mostrar Bibliotecarios");
        System.out.println("0. Volver");
    }

    /** Solicita datos y registra un bibliotecario. */
    private void registrarBibliotecario() {
        String nombre = view.leerNombre();
        String identificacion = view.leerIdentificacion();
        String turno = view.leerTurno();

        try {
            service.registrarBibliotecario(nombre, identificacion, turno);
            view.mostrarRegistroExitoso();
        } catch (Exception e) {
            view.mostrarError("al registrar bibliotecario: " + e.getMessage());
        }
    }

    /** Solicita datos y edita un bibliotecario existente. */
    private void editarBibliotecario() {
        List<Bibliotecario> bibliotecarios = service.getBibliotecarios();
        if (bibliotecarios.isEmpty()) {
            view.mostrarError("No hay bibliotecarios registrados.");
            return;
        }

        String identificacion = view.leerIdentificacion();
        if (!existeBibliotecario(identificacion)) {
            view.mostrarError("No existe un bibliotecario con esa identificacion.");
            return;
        }

        String nombre = view.leerNombre();
        String turno = view.leerTurno();

        try {
            service.editarBibliotecario(identificacion, nombre, turno);
            view.mostrarMensaje("Bibliotecario editado correctamente.");
        } catch (Exception e) {
            view.mostrarError("al editar bibliotecario: " + e.getMessage());
        }
    }

    /** Elimina un bibliotecario por identificacion. */
    private void eliminarBibliotecario() {
        List<Bibliotecario> bibliotecarios = service.getBibliotecarios();
        if (bibliotecarios.isEmpty()) {
            view.mostrarError("No hay bibliotecarios registrados.");
            return;
        }

        String identificacion = view.leerIdentificacion();
        if (!existeBibliotecario(identificacion)) {
            view.mostrarError("No existe un bibliotecario con esa identificacion.");
            return;
        }

        try {
            service.eliminarBibliotecario(identificacion);
            view.mostrarMensaje("Bibliotecario eliminado correctamente.");
        } catch (Exception e) {
            view.mostrarError("al eliminar bibliotecario: " + e.getMessage());
        }
    }

    /** Muestra el listado de bibliotecarios. */
    private void mostrarBibliotecarios() {
        List<Bibliotecario> lista = service.getBibliotecarios();
        if (lista.isEmpty()) {
            view.mostrarNoHayBibliotecarios();
            return;
        }
        view.mostrarListado(lista);
    }

    /** Verifica si existe un bibliotecario con la identificacion indicada. */
    private boolean existeBibliotecario(String identificacion) {
        return service.getBibliotecarios().stream()
                .anyMatch(b -> b.getIdentificacion().equals(identificacion));
    }
}
