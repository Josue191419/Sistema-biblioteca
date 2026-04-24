package org.example.view;

import java.util.Scanner;

/**
 * Vista principal/fachada de consola.
 * Mantiene utilidades generales y expone sub-vistas por dominio.
 */
public class BibliotecaView {

    private final Scanner sc = new Scanner(System.in);
    private final LibroView libroView;
    private final UsuarioView usuarioView;
    private final AutorView autorView;
    private final BibliotecaEntidadView bibliotecaEntidadView;
    private final BibliotecarioView bibliotecarioView;

    /** Inicializa la vista principal y sus sub-vistas por modulo. */
    public BibliotecaView() {
        this.libroView = new LibroView(this);
        this.usuarioView = new UsuarioView(this);
        this.autorView = new AutorView(this);
        this.bibliotecaEntidadView = new BibliotecaEntidadView(this);
        this.bibliotecarioView = new BibliotecarioView(this);
    }

    /**
     * Muestra el menú principal por módulos.
     */
    public void mostrarMenu() {
        System.out.println("\n===== SISTEMA BIBLIOTECA =====");
        System.out.println("1. Modulo Libros");
        System.out.println("2. Modulo Usuarios");
        System.out.println("3. Modulo Prestamos");
        System.out.println("4. Modulo Autores");
        System.out.println("5. Modulo Biblioteca");
        System.out.println("6. Modulo Bibliotecarios");
        System.out.println("0. Salir");
    }

    /** Retorna la vista del modulo de libros. */
    public LibroView getLibroView() {
        return libroView;
    }

    /** Retorna la vista del modulo de usuarios. */
    public UsuarioView getUsuarioView() {
        return usuarioView;
    }

    /** Retorna la vista del modulo de autores. */
    public AutorView getAutorView() {
        return autorView;
    }

    /** Retorna la vista del modulo de biblioteca activa. */
    public BibliotecaEntidadView getBibliotecaEntidadView() {
        return bibliotecaEntidadView;
    }

    /** Retorna la vista del modulo de bibliotecarios. */
    public BibliotecarioView getBibliotecarioView() {
        return bibliotecarioView;
    }

    /**
     * Lee la opcion seleccionada por el usuario en el menu.
     * @return numero de opcion
     */
    public int leerEntero() {
        while (true) {
            try {
                System.out.print("Opcion: ");
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor ingrese un numero.");
            }
        }
    }

    /**
     * Lee un numero entero con un mensaje previo.
     * @param mensaje mensaje a mostrar al usuario
     * @return numero entero ingresado
     */
    public int leerEnteroConMensaje(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje + " ");
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor ingrese un numero entero.");
            }
        }
    }

    /**
     * Lee un texto ingresado por el usuario.
     * @param mensaje mensaje a mostrar al usuario
     * @return texto ingresado
     */
    public String leerTexto(String mensaje) {
        System.out.print(mensaje + " ");
        return sc.nextLine();
    }

    /**
     * Imprime un mensaje en consola.
     * @param mensaje mensaje a imprimir
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
