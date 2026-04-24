package org.example.view;

import org.example.model.Bibliotecario;

import java.util.List;

/**
 * Vista de consola para el modulo de bibliotecarios.
 */
public class BibliotecarioView {

    private final BibliotecaView base;

    /** Crea la vista de bibliotecarios enlazada a la vista base. */
    public BibliotecarioView(BibliotecaView base) {
        this.base = base;
    }

    /** Lee opcion del menu del modulo. */
    public int leerOpcionModulo() {
        return base.leerEntero();
    }

    /** Lee nombre del bibliotecario. */
    public String leerNombre() {
        return base.leerTexto("Ingrese nombre del bibliotecario:");
    }

    /** Lee identificacion del bibliotecario. */
    public String leerIdentificacion() {
        return base.leerTexto("Ingrese identificacion:");
    }

    /** Lee turno del bibliotecario. */
    public String leerTurno() {
        return base.leerTexto("Ingrese turno:");
    }

    /** Muestra confirmacion de registro exitoso. */
    public void mostrarRegistroExitoso() {
        base.mostrarMensaje("Bibliotecario registrado correctamente.");
    }

    /** Informa que no hay bibliotecarios registrados. */
    public void mostrarNoHayBibliotecarios() {
        base.mostrarMensaje("No hay bibliotecarios registrados.");
    }

    /** Muestra el listado de bibliotecarios. */
    public void mostrarListado(List<Bibliotecario> bibliotecarios) {
        base.mostrarMensaje("\n==== LISTA DE BIBLIOTECARIOS ====");
        for (Bibliotecario b : bibliotecarios) {
            base.mostrarMensaje(b.toString());
        }
    }

    /** Muestra un mensaje de error. */
    public void mostrarError(String mensaje) {
        base.mostrarMensaje("Error: " + mensaje);
    }

    /** Muestra un mensaje en consola. */
    public void mostrarMensaje(String mensaje) {
        base.mostrarMensaje(mensaje);
    }
}

