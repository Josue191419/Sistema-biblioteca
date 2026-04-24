package org.example.controller;

import org.example.service.IBibliotecaService;
import org.example.view.BibliotecaView;

/**
 * Controlador principal de la biblioteca.
 * Coordina la navegación del menú y delega cada caso de uso a controladores específicos.
 */
public class BibliotecaController {
    private final BibliotecaView view;
    private final LibroController libroController;
    private final UsuarioController usuarioController;
    private final PrestamoController prestamoController;
    private final AutorController autorController;
    private final BibliotecaEntidadController bibliotecaEntidadController;
    private final BibliotecarioController bibliotecarioController;

    /**
     * Construye el controlador principal e inicializa los subcontroladores.
     */
    public BibliotecaController(BibliotecaView view, IBibliotecaService service) {
        this.view = view;
        this.libroController = new LibroController(view, service);
        this.usuarioController = new UsuarioController(view, service);
        this.prestamoController = new PrestamoController(view, service);
        this.autorController = new AutorController(view, service);
        this.bibliotecaEntidadController = new BibliotecaEntidadController(view, service);
        this.bibliotecarioController = new BibliotecarioController(view, service);
    }

    /**
     * Inicia el bucle principal del sistema por consola.
     */
    public void iniciar() {
        int opcion;
        do {
            view.mostrarMenu();
            opcion = view.leerEntero();
            try {
                procesarOpcion(opcion);
            } catch (IllegalArgumentException e) {
                view.mostrarMensaje("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                view.mostrarMensaje("Error inesperado: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    /**
     * Enruta una opcion del menu principal al modulo correspondiente.
     */
    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> libroController.iniciarModulo();
            case 2 -> usuarioController.iniciarModulo();
            case 3 -> prestamoController.iniciarModulo();
            case 4 -> autorController.iniciarModulo();
            case 5 -> bibliotecaEntidadController.iniciarModulo();
            case 6 -> bibliotecarioController.iniciarModulo();
            case 0 -> view.mostrarMensaje("Saliendo del sistema...");
            default -> view.mostrarMensaje("Opcion invalida.");
        }
    }
}
