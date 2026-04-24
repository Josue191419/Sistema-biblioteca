package org.example.view;

import org.example.model.Usuario;

import java.util.List;

/**
 * Vista de consola para el modulo de usuarios.
 */
public class UsuarioView {

    private final BibliotecaView base;

    /** Crea la vista de usuarios enlazada a la vista base. */
    public UsuarioView(BibliotecaView base) {
        this.base = base;
    }

    /** Lee el ID para registrar un usuario. */
    public int leerIdUsuario() {
        return base.leerEnteroConMensaje("Ingrese ID del usuario:");
    }

    /** Lee el ID del usuario a editar. */
    public int leerIdUsuarioEditar() {
        return base.leerEnteroConMensaje("Ingrese ID del usuario a editar:");
    }

    /** Lee el ID del usuario a eliminar. */
    public int leerIdUsuarioEliminar() {
        return base.leerEnteroConMensaje("Ingrese ID del usuario a eliminar:");
    }

    /** Lee el nombre del usuario. */
    public String leerNombre() {
        return base.leerTexto("Ingrese nombre:");
    }

    /** Lee el nuevo nombre del usuario. */
    public String leerNombreNuevo() {
        return base.leerTexto("Ingrese nuevo nombre:");
    }

    /** Lee la identificacion del usuario. */
    public String leerIdentificacion() {
        return base.leerTexto("Ingrese identificacion:");
    }

    /** Lee la nueva identificacion del usuario. */
    public String leerIdentificacionNueva() {
        return base.leerTexto("Ingrese nueva identificacion:");
    }

    /** Lee el tipo del usuario. */
    public String leerTipo() {
        return base.leerTexto("Ingrese tipo (Estudiante/Profesor/Visitante):");
    }

    /** Lee el nuevo tipo del usuario. */
    public String leerTipoNuevo() {
        return base.leerTexto("Ingrese nuevo tipo (Estudiante/Profesor/Visitante):");
    }

    /** Muestra error en consola. */
    public void mostrarError(String mensaje) {
        base.mostrarMensaje("Error: " + mensaje);
    }

    /** Muestra confirmacion de registro exitoso. */
    public void mostrarRegistroExitoso() {
        base.mostrarMensaje("Usuario registrado correctamente.");
    }

    /** Muestra confirmacion de edicion exitosa. */
    public void mostrarEdicionExitosa() {
        base.mostrarMensaje("Usuario editado correctamente.");
    }

    /** Muestra confirmacion de eliminacion exitosa. */
    public void mostrarEliminacionExitosa() {
        base.mostrarMensaje("Usuario eliminado correctamente.");
    }

    /** Informa que no hay usuarios registrados. */
    public void mostrarNoHayUsuarios() {
        base.mostrarMensaje("No hay usuarios registrados.");
    }

    /** Muestra el listado de usuarios. */
    public void mostrarListado(List<Usuario> usuarios) {
        base.mostrarMensaje("\n==== LISTA DE USUARIOS ====");
        for (Usuario usuario : usuarios) {
            String identificacion = usuario.getIdentificacion() != null ? usuario.getIdentificacion() : "N/A";
            String tipo = usuario.getTipo() != null ? usuario.getTipo() : "N/A";
            base.mostrarMensaje("ID: " + usuario.getId()
                    + " | Nombre: " + usuario.getNombre()
                    + " | Identificacion: " + identificacion
                    + " | Tipo: " + tipo);
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
