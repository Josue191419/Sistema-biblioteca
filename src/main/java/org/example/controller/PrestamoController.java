package org.example.controller;

import org.example.model.Biblioteca;
import org.example.model.Bibliotecario;
import org.example.model.Libro;
import org.example.model.Prestamo;
import org.example.model.Usuario;
import org.example.service.IBibliotecaService;
import org.example.view.BibliotecaView;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador del modulo de prestamos.
 *
 * <p>Gestiona registro, edicion, eliminacion y visualizacion de prestamos,
 * incluyendo seleccion asistida de libros, usuarios y bibliotecarios.</p>
 */
public class PrestamoController {
    private final BibliotecaView view;
    private final IBibliotecaService service;

    /** Crea el controlador de prestamos. */
    public PrestamoController(BibliotecaView view, IBibliotecaService service) {
        this.view = view;
        this.service = service;
    }

    /** Inicia el menu del modulo de prestamos. */
    public void iniciarModulo() {
        int opcion;
        do {
            mostrarMenuModulo();
            opcion = view.leerEntero();
            switch (opcion) {
                case 1 -> registrarPrestamo();
                case 2 -> editarPrestamo();
                case 3 -> eliminarPrestamo();
                case 4 -> mostrarPrestamos();
                case 0 -> view.mostrarMensaje("Volviendo al menu principal.");
                default -> view.mostrarMensaje("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    /** Muestra el menu de acciones del modulo prestamos. */
    private void mostrarMenuModulo() {
        System.out.println("\n===== MODULO PRESTAMOS =====");
        System.out.println("1. Registrar Prestamo");
        System.out.println("2. Editar Prestamo");
        System.out.println("3. Eliminar Prestamo");
        System.out.println("4. Mostrar Prestamos");
        System.out.println("0. Volver");
    }

    /** Registra un prestamo nuevo con seleccion guiada de datos relacionados. */
    public void registrarPrestamo() {
        List<Libro> libros = service.getLibros();
        List<Usuario> usuarios = service.getUsuarios();
        List<Bibliotecario> bibliotecarios = service.getBibliotecarios();

        if (libros.isEmpty() || usuarios.isEmpty()) {
            view.mostrarMensaje("Primero debe registrar libros y usuarios.");
            return;
        }
        if (bibliotecarios.isEmpty()) {
            view.mostrarMensaje("Primero debe registrar al menos un bibliotecario.");
            return;
        }

        int idPrestamo = view.leerEnteroConMensaje("Ingrese ID del prestamo: ");

        mostrarLibrosEnumerados(libros);
        int libroIndex = seleccionarElemento("libro", libros.size());

        mostrarUsuariosEnumerados(usuarios);
        int usuarioIndex = seleccionarElemento("usuario", usuarios.size());

        mostrarBibliotecariosEnumerados(bibliotecarios);
        int bibliotecarioIndex = seleccionarElemento("bibliotecario", bibliotecarios.size());

        Libro libro = libros.get(libroIndex);
        Usuario usuario = usuarios.get(usuarioIndex);
        Bibliotecario bibliotecario = bibliotecarios.get(bibliotecarioIndex);

        String bibliotecaNombre = obtenerNombreBibliotecaParaPrestamo();

        service.registrarPrestamo(
                idPrestamo,
                libro.getId(),
                usuario.getId(),
                bibliotecaNombre,
                bibliotecario.getIdentificacion()
        );
        view.mostrarMensaje("Prestamo registrado correctamente.");
    }

    /** Edita un prestamo existente y recalcula fechas. */
    public void editarPrestamo() {
        List<Prestamo> prestamos = service.getPrestamos();
        if (prestamos.isEmpty()) {
            view.mostrarMensaje("No hay prestamos registrados.");
            return;
        }

        int id = view.leerEnteroConMensaje("Ingrese ID del prestamo a editar: ");
        if (!existePrestamo(id)) {
            view.mostrarMensaje("No existe un prestamo con ese ID.");
            return;
        }

        List<Libro> libros = service.getLibros();
        List<Usuario> usuarios = service.getUsuarios();
        List<Bibliotecario> bibliotecarios = service.getBibliotecarios();

        if (libros.isEmpty() || usuarios.isEmpty()) {
            view.mostrarMensaje("Primero debe registrar libros y usuarios.");
            return;
        }
        if (bibliotecarios.isEmpty()) {
            view.mostrarMensaje("Primero debe registrar al menos un bibliotecario.");
            return;
        }

        mostrarLibrosEnumerados(libros);
        int libroIndex = seleccionarElemento("libro", libros.size());

        mostrarUsuariosEnumerados(usuarios);
        int usuarioIndex = seleccionarElemento("usuario", usuarios.size());

        mostrarBibliotecariosEnumerados(bibliotecarios);
        int bibliotecarioIndex = seleccionarElemento("bibliotecario", bibliotecarios.size());

        Libro libro = libros.get(libroIndex);
        Usuario usuario = usuarios.get(usuarioIndex);
        Bibliotecario bibliotecario = bibliotecarios.get(bibliotecarioIndex);

        int dias = view.leerEnteroConMensaje("Ingrese dias para devolucion (ej: 7): ");
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(dias);
        String bibliotecaNombre = obtenerNombreBibliotecaParaPrestamo();

        service.editarPrestamo(
                id,
                libro.getId(),
                usuario.getId(),
                fechaPrestamo,
                fechaDevolucion,
                bibliotecaNombre,
                bibliotecario.getIdentificacion()
        );
        view.mostrarMensaje("Prestamo editado correctamente.");
    }

    /** Elimina un prestamo por ID. */
    public void eliminarPrestamo() {
        List<Prestamo> prestamos = service.getPrestamos();
        if (prestamos.isEmpty()) {
            view.mostrarMensaje("No hay prestamos registrados.");
            return;
        }

        int id = view.leerEnteroConMensaje("Ingrese ID del prestamo a eliminar: ");
        if (!existePrestamo(id)) {
            view.mostrarMensaje("No existe un prestamo con ese ID.");
            return;
        }

        service.eliminarPrestamo(id);
        view.mostrarMensaje("Prestamo eliminado correctamente.");
    }

    /** Muestra todos los prestamos registrados. */
    public void mostrarPrestamos() {
        List<Prestamo> lista = service.getPrestamos();
        if (lista.isEmpty()) {
            view.mostrarMensaje("No hay prestamos registrados.");
            return;
        }

        view.mostrarMensaje("\n==== LISTA DE PRESTAMOS ====");
        for (Prestamo prestamo : lista) {
            view.mostrarMensaje(prestamo.resumenPrestamo());
        }
    }

    /** Muestra libros enumerados para seleccion por indice. */
    private void mostrarLibrosEnumerados(List<Libro> libros) {
        view.mostrarMensaje("\n==== LIBROS DISPONIBLES ====");
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            String estado = libro.isDisponible() ? "Disponible" : "Prestado";
            view.mostrarMensaje((i + 1) + ". " + libro.getTitulo() + " - " + libro.getAutor() + " [" + estado + "]");
        }
    }

    /** Muestra usuarios enumerados para seleccion por indice. */
    private void mostrarUsuariosEnumerados(List<Usuario> usuarios) {
        view.mostrarMensaje("\n==== USUARIOS DISPONIBLES ====");
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            String tipo = usuario.getTipo() != null ? usuario.getTipo() : "N/A";
            view.mostrarMensaje((i + 1) + ". " + usuario.getNombre() + " (" + tipo + ")");
        }
    }

    /** Muestra bibliotecarios enumerados para seleccion por indice. */
    private void mostrarBibliotecariosEnumerados(List<Bibliotecario> bibliotecarios) {
        view.mostrarMensaje("\n==== BIBLIOTECARIOS DISPONIBLES ====");
        for (int i = 0; i < bibliotecarios.size(); i++) {
            Bibliotecario bibliotecario = bibliotecarios.get(i);
            String turno = bibliotecario.getTurno() != null ? bibliotecario.getTurno() : "N/A";
            view.mostrarMensaje((i + 1) + ". " + bibliotecario.getNombre() + " (" + bibliotecario.getIdentificacion() + ", Turno: " + turno + ")");
        }
    }

    /** Obtiene el nombre de biblioteca para asociar al prestamo actual. */
    private String obtenerNombreBibliotecaParaPrestamo() {
        Biblioteca bibliotecaActiva = service.getBiblioteca();
        if (bibliotecaActiva != null && bibliotecaActiva.getNombre() != null && !bibliotecaActiva.getNombre().isBlank()) {
            return bibliotecaActiva.getNombre();
        }
        return view.leerTexto("No hay biblioteca activa. Ingrese nombre de la biblioteca para este prestamo:");
    }

    /** Solicita un indice valido en rango y retorna posicion base cero. */
    private int seleccionarElemento(String tipo, int cantidad) {
        int numero;
        do {
            numero = view.leerEnteroConMensaje("Seleccione el " + tipo + " (1 a " + cantidad + "): ");
            if (numero < 1 || numero > cantidad) {
                view.mostrarMensaje("Numero invalido, ingrese un valor entre 1 y " + cantidad);
            }
        } while (numero < 1 || numero > cantidad);
        return numero - 1;
    }

    /** Verifica si existe un prestamo con el ID indicado. */
    private boolean existePrestamo(int id) {
        return service.getPrestamos().stream().anyMatch(prestamo -> prestamo.getId() == id);
    }
}
