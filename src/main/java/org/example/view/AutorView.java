package org.example.view;

import org.example.model.Autor;

import java.util.List;

/**
 * Vista de consola para el modulo de autores.
 */
public class AutorView {

    private final BibliotecaView base;

    /** Crea la vista de autores enlazada a la vista base. */
    public AutorView(BibliotecaView base) {
        this.base = base;
    }

    /** Lee el ID del autor. */
    public int leerIdAutor() {
        return base.leerEnteroConMensaje("Ingrese ID del autor:");
    }

    /** Lee el nombre del autor. */
    public String leerNombreAutor() {
        return base.leerTexto("Ingrese nombre del autor:");
    }

    /** Lee la nacionalidad del autor. */
    public String leerNacionalidad() {
        return base.leerTexto("Ingrese nacionalidad:");
    }

    /** Lee el anio de nacimiento del autor. */
    public int leerAnioNacimiento() {
        return base.leerEnteroConMensaje("Ingrese anio de nacimiento:");
    }

    /** Muestra confirmacion de registro exitoso. */
    public void mostrarRegistroExitoso() {
        base.mostrarMensaje("Autor registrado correctamente.");
    }

    /** Informa que no hay autores registrados. */
    public void mostrarNoHayAutores() {
        base.mostrarMensaje("No hay autores registrados.");
    }

    /** Muestra un mensaje de error. */
    public void mostrarError(String mensaje) {
        base.mostrarMensaje("Error: " + mensaje);
    }

    /** Muestra el listado de autores. */
    public void mostrarListado(List<Autor> autores) {
        base.mostrarMensaje("\n==== LISTA DE AUTORES ====");
        for (Autor autor : autores) {
            base.mostrarMensaje(autor.toString());
        }
    }

    /** Lee opcion del menu del modulo. */
    public int leerOpcionModulo() {
        return base.leerEntero();
    }

    /** Muestra un mensaje en consola. */
    public void mostrarMensaje(String mensaje) {
        base.mostrarMensaje(mensaje);
    }
}
