package org.example.controller;

import org.example.model.Usuario;
import org.example.service.IBibliotecaService;
import org.example.view.BibliotecaView;
import org.example.view.UsuarioView;

import java.util.List;

/**
 * Controlador del modulo de usuarios.
 */
public class UsuarioController {
    private final UsuarioView view;
    private final IBibliotecaService service;

    /** Crea el controlador de usuarios con su vista especializada. */
    public UsuarioController(BibliotecaView view, IBibliotecaService service) {
        this.view = view.getUsuarioView();
        this.service = service;
    }

    /** Inicia el menu del modulo de usuarios. */
    public void iniciarModulo() {
        int opcion;
        do {
            mostrarMenuModulo();
            opcion = view.leerOpcionModulo();
            switch (opcion) {
                case 1 -> registrarUsuario();
                case 2 -> editarUsuario();
                case 3 -> eliminarUsuario();
                case 4 -> mostrarUsuarios();
                case 0 -> view.mostrarMensaje("Volviendo al menu principal.");
                default -> view.mostrarError("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    /** Muestra el menu de acciones del modulo de usuarios. */
    private void mostrarMenuModulo() {
        System.out.println("\n===== MODULO USUARIOS =====");
        System.out.println("1. Registrar Usuario");
        System.out.println("2. Editar Usuario");
        System.out.println("3. Eliminar Usuario");
        System.out.println("4. Mostrar Usuarios");
        System.out.println("0. Volver");
    }

    /** Solicita datos y registra un usuario. */
    public void registrarUsuario() {
        int id = view.leerIdUsuario();
        if (existeUsuario(id)) {
            view.mostrarError("Ya existe un usuario con ese ID.");
            return;
        }

        String nombre = view.leerNombre();
        String identificacion = view.leerIdentificacion();
        String tipo = view.leerTipo();

        service.registrarUsuario(id, nombre, identificacion, tipo);
        view.mostrarRegistroExitoso();
    }

    /** Solicita datos y edita un usuario existente. */
    public void editarUsuario() {
        int id = view.leerIdUsuarioEditar();
        if (!existeUsuario(id)) {
            view.mostrarError("No existe un usuario con ese ID.");
            return;
        }

        String nombre = view.leerNombreNuevo();
        String identificacion = view.leerIdentificacionNueva();
        String tipo = view.leerTipoNuevo();

        service.editarUsuario(id, nombre, identificacion, tipo);
        view.mostrarEdicionExitosa();
    }

    /** Elimina un usuario por ID. */
    public void eliminarUsuario() {
        int id = view.leerIdUsuarioEliminar();
        if (!existeUsuario(id)) {
            view.mostrarError("No existe un usuario con ese ID.");
            return;
        }

        service.eliminarUsuario(id);
        view.mostrarEliminacionExitosa();
    }

    /** Muestra el listado de usuarios registrados. */
    public void mostrarUsuarios() {
        List<Usuario> lista = service.getUsuarios();
        if (lista.isEmpty()) {
            view.mostrarNoHayUsuarios();
            return;
        }

        view.mostrarListado(lista);
    }

    /** Verifica si existe un usuario con el ID indicado. */
    private boolean existeUsuario(int id) {
        return service.getUsuarios().stream().anyMatch(usuario -> usuario.getId() == id);
    }
}
