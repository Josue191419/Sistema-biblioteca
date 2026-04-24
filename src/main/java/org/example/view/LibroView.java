package org.example.view;

import org.example.model.Libro;

import java.time.LocalDate;
import java.util.List;

/**
 * Vista de consola para el modulo de libros.
 */
public class LibroView {

    private final BibliotecaView base;

    /** Crea la vista de libros enlazada a la vista base. */
    public LibroView(BibliotecaView base) {
        this.base = base;
    }

    /** Lee el ID para registrar un libro. */
    public int leerIdLibro() {
        return base.leerEnteroConMensaje("Ingrese ID del libro:");
    }

    /** Lee el ID del libro a editar. */
    public int leerIdLibroEditar() {
        return base.leerEnteroConMensaje("Ingrese ID del libro a editar:");
    }

    /** Lee el ID del libro a eliminar. */
    public int leerIdLibroEliminar() {
        return base.leerEnteroConMensaje("Ingrese ID del libro a eliminar:");
    }

    /** Lee el titulo del libro. */
    public String leerTitulo() {
        return base.leerTexto("Ingrese titulo:");
    }

    /** Lee el nuevo titulo del libro. */
    public String leerTituloNuevo() {
        return base.leerTexto("Ingrese nuevo titulo:");
    }

    /** Lee el autor del libro. */
    public String leerAutor() {
        return base.leerTexto("Ingrese autor:");
    }

    /** Lee el nuevo autor del libro. */
    public String leerAutorNuevo() {
        return base.leerTexto("Ingrese nuevo autor:");
    }

    /** Lee la editora del libro. */
    public String leerEditora() {
        return base.leerTexto("Ingrese editora:");
    }

    /** Lee la nueva editora del libro. */
    public String leerEditoraNueva() {
        return base.leerTexto("Ingrese nueva editora:");
    }

    /** Lee la fecha de publicacion completa (dia, mes, anio). */
    public LocalDate leerFechaPublicacion() {
        return leerFechaConMensaje("publicacion");
    }

    /** Lee la nueva fecha de publicacion completa (dia, mes, anio). */
    public LocalDate leerFechaPublicacionNueva() {
        return leerFechaConMensaje("nueva publicacion");
    }

    /**
     * Pide dia, mes y anio por separado y construye un LocalDate validado.
     * Si el usuario ingresa un valor invalido, lo vuelve a pedir.
     */
    private LocalDate leerFechaConMensaje(String contexto) {
        while (true) {
            try {
                int anio = base.leerEnteroConMensaje("Ingrese anio de " + contexto + " (ej: 1997):");
                int mes  = base.leerEnteroConMensaje("Ingrese mes de " + contexto + " (1-12):");
                int dia  = base.leerEnteroConMensaje("Ingrese dia de " + contexto + " (1-31):");
                return LocalDate.of(anio, mes, dia);
            } catch (Exception e) {
                base.mostrarMensaje("Fecha invalida. Intente nuevamente.");
            }
        }
    }

    /** Muestra confirmacion de registro exitoso. */
    public void mostrarRegistroExitoso() {
        base.mostrarMensaje("Libro registrado correctamente.");
    }

    /** Muestra confirmacion de edicion exitosa. */
    public void mostrarEdicionExitosa() {
        base.mostrarMensaje("Libro editado correctamente.");
    }

    /** Muestra confirmacion de eliminacion exitosa. */
    public void mostrarEliminacionExitosa() {
        base.mostrarMensaje("Libro eliminado correctamente.");
    }

    /** Informa que no hay libros registrados. */
    public void mostrarNoHayLibros() {
        base.mostrarMensaje("No hay libros registrados.");
    }

    /** Muestra un mensaje de error. */
    public void mostrarError(String mensaje) {
        base.mostrarMensaje("Error: " + mensaje);
    }

    /** Muestra el listado de libros en formato resumido. */
    public void mostrarListado(List<Libro> libros) {
        base.mostrarMensaje("\n==== LISTA DE LIBROS ====");
        for (Libro libro : libros) {
            String editora = libro.getEditora() != null ? libro.getEditora() : "N/A";
            String fecha = libro.getFechaPublicacion() != null
                    ? libro.getFechaPublicacion().getDayOfMonth()
                      + "/" + libro.getFechaPublicacion().getMonthValue()
                      + "/" + libro.getFechaPublicacion().getYear()
                    : "N/A";
            String disponible = libro.isDisponible() ? "Si" : "No";
            base.mostrarMensaje("ID: " + libro.getId()
                    + " | Titulo: " + libro.getTitulo()
                    + " | Autor: " + libro.getAutor()
                    + " | Editora: " + editora
                    + " | Fecha: " + fecha
                    + " | Disponible: " + disponible);
        }
    }

    /** Lee la opcion del menu del modulo de libros. */
    public int leerOpcionModulo() {
        return base.leerEntero();
    }

    /** Muestra un mensaje directo en consola. */
    public void mostrarMensaje(String mensaje) {
        base.mostrarMensaje(mensaje);
    }
}
