package org.example.controller;

import org.example.model.Biblioteca;
import org.example.service.IBibliotecaService;
import org.example.view.BibliotecaEntidadView;
import org.example.view.BibliotecaView;

/**
 * Controlador del modulo de configuracion de biblioteca activa.
 */
public class BibliotecaEntidadController {

    private final BibliotecaEntidadView view;
    private final IBibliotecaService service;

    /** Crea el controlador con su vista especializada. */
    public BibliotecaEntidadController(BibliotecaView view, IBibliotecaService service) {
        this.view = view.getBibliotecaEntidadView();
        this.service = service;
    }

    /** Inicia el menu del modulo biblioteca. */
    public void iniciarModulo() {
        int opcion;
        do {
            mostrarMenuModulo();
            opcion = view.leerOpcionModulo();
            switch (opcion) {
                case 1 -> crearBiblioteca();
                case 2 -> mostrarBiblioteca();
                case 0 -> view.mostrarMensaje("Volviendo al menu principal.");
                default -> view.mostrarError("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    /** Muestra el menu de acciones del modulo biblioteca. */
    private void mostrarMenuModulo() {
        System.out.println("\n===== MODULO BIBLIOTECA =====");
        System.out.println("1. Crear/Actualizar Biblioteca");
        System.out.println("2. Ver Biblioteca Activa");
        System.out.println("0. Volver");
    }

    /** Solicita datos y crea/actualiza la biblioteca activa. */
    private void crearBiblioteca() {
        String nombre = view.leerNombre();
        String direccion = view.leerDireccion();
        String telefono = view.leerTelefono();
        int capacidad = view.leerCapacidad();

        service.crearBiblioteca(nombre, direccion, telefono, capacidad);
        view.mostrarRegistroExitoso();
    }

    /** Muestra la biblioteca activa actual. */
    private void mostrarBiblioteca() {
        Biblioteca biblioteca = service.getBiblioteca();
        if (biblioteca == null) {
            view.mostrarNoRegistrada();
            return;
        }
        view.mostrarDatos(biblioteca);
    }
}

