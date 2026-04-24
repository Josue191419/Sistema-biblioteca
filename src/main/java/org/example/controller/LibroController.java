package org.example.controller;

import org.example.model.Libro;
import org.example.service.IBibliotecaService;
import org.example.view.BibliotecaView;
import org.example.view.LibroView;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador del modulo de libros.
 *
 * <p>Orquesta el flujo de menu y las operaciones CRUD de libros, ademas
 * de delegar busquedas externas al {@link BuscadorController}.</p>
 */
public class LibroController {
    private final LibroView view;
    private final IBibliotecaService service;
    private final BuscadorController buscadorController;

    /** Crea el controlador de libros con su vista especializada. */
    public LibroController(BibliotecaView view, IBibliotecaService service) {
        this.view = view.getLibroView();
        this.service = service;
        this.buscadorController = new BuscadorController(view, service);
    }

    /** Inicia el menu del modulo de libros. */
    public void iniciarModulo() {
        int opcion;
        do {
            mostrarMenuModulo();
            opcion = view.leerOpcionModulo();
            switch (opcion) {
                case 1 -> registrarLibro();
                case 2 -> editarLibro();
                case 3 -> eliminarLibro();
                case 4 -> mostrarLibros();
                case 5 -> buscadorController.buscarLibros();
                case 0 -> view.mostrarMensaje("Volviendo al menu principal.");
                default -> view.mostrarError("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    /** Muestra el menu de acciones del modulo de libros. */
    private void mostrarMenuModulo() {
        System.out.println("\n===== MODULO LIBROS =====");
        System.out.println("1. Registrar Libro");
        System.out.println("2. Editar Libro");
        System.out.println("3. Eliminar Libro");
        System.out.println("4. Mostrar Libros");
        System.out.println("5. Buscar Libros en Open Library");
        System.out.println("0. Volver");
    }

    /** Solicita datos y registra un libro nuevo. */
    public void registrarLibro() {
        int id = view.leerIdLibro();
        if (existeLibro(id)) {
            view.mostrarError("Ya existe un libro con ese ID.");
            return;
        }

        String titulo = view.leerTitulo();
        String autor = view.leerAutor();
        String editora = view.leerEditora();
        LocalDate fecha = view.leerFechaPublicacion();

        service.registrarLibro(id, titulo, autor, editora, fecha);
        view.mostrarRegistroExitoso();
    }

    /** Solicita datos y edita un libro existente. */
    public void editarLibro() {
        int id = view.leerIdLibroEditar();
        if (!existeLibro(id)) {
            view.mostrarError("No existe un libro con ese ID.");
            return;
        }

        String titulo = view.leerTituloNuevo();
        String autor = view.leerAutorNuevo();
        String editora = view.leerEditoraNueva();
        LocalDate fecha = view.leerFechaPublicacionNueva();

        service.editarLibro(id, titulo, autor, editora, fecha);
        view.mostrarEdicionExitosa();
    }

    /** Elimina un libro por ID. */
    public void eliminarLibro() {
        int id = view.leerIdLibroEliminar();
        if (!existeLibro(id)) {
            view.mostrarError("No existe un libro con ese ID.");
            return;
        }

        service.eliminarLibro(id);
        view.mostrarEliminacionExitosa();
    }

    /** Muestra el listado completo de libros. */
    public void mostrarLibros() {
        List<Libro> lista = service.getLibros();
        if (lista.isEmpty()) {
            view.mostrarNoHayLibros();
            return;
        }

        view.mostrarListado(lista);
    }

    /** Verifica si ya existe un libro con el ID indicado. */
    private boolean existeLibro(int id) {
        return service.getLibros().stream().anyMatch(libro -> libro.getId() == id);
    }
}
