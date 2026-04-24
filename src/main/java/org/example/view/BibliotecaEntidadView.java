package org.example.view;

import org.example.model.Biblioteca;

/**
 * Vista de consola para el modulo de biblioteca activa.
 */
public class BibliotecaEntidadView {

    private final BibliotecaView base;

    /** Crea la vista enlazada a la vista base. */
    public BibliotecaEntidadView(BibliotecaView base) {
        this.base = base;
    }

    /** Lee opcion del menu del modulo. */
    public int leerOpcionModulo() {
        return base.leerEntero();
    }

    /** Lee nombre de la biblioteca. */
    public String leerNombre() {
        return base.leerTexto("Ingrese nombre de la biblioteca:");
    }

    /** Lee direccion de la biblioteca. */
    public String leerDireccion() {
        return base.leerTexto("Ingrese direccion:");
    }

    /** Lee telefono de la biblioteca. */
    public String leerTelefono() {
        return base.leerTexto("Ingrese telefono:");
    }

    /** Lee capacidad maxima de la biblioteca. */
    public int leerCapacidad() {
        return base.leerEnteroConMensaje("Ingrese capacidad maxima de libros:");
    }

    /** Muestra confirmacion de registro o actualizacion. */
    public void mostrarRegistroExitoso() {
        base.mostrarMensaje("Biblioteca creada/actualizada correctamente.");
    }

    /** Informa que no hay biblioteca activa registrada. */
    public void mostrarNoRegistrada() {
        base.mostrarMensaje("No hay biblioteca registrada todavia.");
    }

    /** Muestra datos de la biblioteca activa. */
    public void mostrarDatos(Biblioteca biblioteca) {
        base.mostrarMensaje("\n==== DATOS DE BIBLIOTECA ====");
        base.mostrarMensaje("Nombre: " + biblioteca.getNombre());
        base.mostrarMensaje("Direccion: " + biblioteca.getDireccion());
        base.mostrarMensaje("Telefono: " + biblioteca.getTelefono());
        base.mostrarMensaje("Capacidad: " + biblioteca.getCapacidad());
        base.mostrarMensaje("Libros asociados: " + biblioteca.getCantidadLibros());
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

