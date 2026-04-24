package org.example.controller;

import org.example.model.Autor;
import org.example.model.Libro;
import org.example.service.IBibliotecaService;
import org.example.service.OpenLibraryService;
import org.example.view.BibliotecaView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para buscar libros en Open Library API y registrarlos en la BD.
 */
public class BuscadorController {
    private final BibliotecaView view;
    private final IBibliotecaService service;
    private final OpenLibraryService openLibraryService;
    private List<Libro> resultadosBusqueda;

    /** Crea el controlador de busqueda y su servicio Open Library. */
    public BuscadorController(BibliotecaView view, IBibliotecaService service) {
        this.view = view;
        this.service = service;
        this.openLibraryService = new OpenLibraryService();
        this.resultadosBusqueda = new ArrayList<>();
    }

    /**
     * Busca libros en Open Library y ofrece registrar uno en la BD.
     */
    public void buscarLibros() {
        String texto = view.leerTexto("Ingrese el nombre del libro a buscar:");

        if (texto.isEmpty()) {
            view.mostrarMensaje("Error: Por favor ingrese un termino de busqueda.");
            return;
        }

        view.mostrarMensaje("\nBuscando en Open Library...\n");
        resultadosBusqueda = openLibraryService.buscarLibros(texto);

        if (resultadosBusqueda.isEmpty()) {
            view.mostrarMensaje("No se encontraron libros para: " + texto);
            return;
        }

        mostrarResultados();
        ofrecerRegistro();
    }

    /** Muestra en consola los resultados recuperados desde la API. */
    private void mostrarResultados() {
        view.mostrarMensaje("\n========== RESULTADOS DE BUSQUEDA ==========");
        view.mostrarMensaje("Se encontraron " + resultadosBusqueda.size() + " libros:\n");

        for (int i = 0; i < resultadosBusqueda.size(); i++) {
            Libro libro = resultadosBusqueda.get(i);
            String anio = libro.getFechaPublicacion() != null
                    ? String.valueOf(libro.getFechaPublicacion().getYear())
                    : "N/A";
            view.mostrarMensaje((i + 1) + ". Titulo: " + libro.getTitulo());
            view.mostrarMensaje("   Autor: " + libro.getAutor());
            String anioAutor = libro.getAutorAnioNacimiento() != null
                    ? String.valueOf(libro.getAutorAnioNacimiento())
                    : "N/A";
            String nacionalidadAutor = libro.getAutorNacionalidad() != null
                    ? libro.getAutorNacionalidad()
                    : "N/A";
            view.mostrarMensaje("   Autor - Nacionalidad: " + nacionalidadAutor + " | Anio nacimiento: " + anioAutor);
            view.mostrarMensaje("   Editorial: " + libro.getEditora());
            view.mostrarMensaje("   Anio: " + anio);
            view.mostrarMensaje("");
        }
    }

    /** Ofrece al usuario registrar uno de los resultados encontrados. */
    private void ofrecerRegistro() {
        int opcion = view.leerEnteroConMensaje("Desea registrar algun libro? (1-"
                + resultadosBusqueda.size() + ", 0 para no registrar):");

        if (opcion == 0) return;

        if (opcion < 1 || opcion > resultadosBusqueda.size()) {
            view.mostrarMensaje("Opcion invalida.");
            return;
        }

        Libro libroSeleccionado = resultadosBusqueda.get(opcion - 1);
        registrarLibroBD(libroSeleccionado);
    }

    /** Registra en base de datos un libro seleccionado desde la busqueda. */
    private void registrarLibroBD(Libro libro) {
        try {
            int nuevoId = generarIdLibro();
            String editora = libro.getEditora() != null ? libro.getEditora() : "Desconocida";
            LocalDate fecha = libro.getFechaPublicacion() != null
                    ? libro.getFechaPublicacion()
                    : LocalDate.of(2024, 1, 1);

            service.registrarLibro(nuevoId, libro.getTitulo(), libro.getAutor(), editora, fecha);
            actualizarAutorConDatosApi(libro);

            view.mostrarMensaje("\nLibro registrado correctamente en la base de datos!");
            view.mostrarMensaje("ID asignado: " + nuevoId);
            view.mostrarMensaje("Titulo: " + libro.getTitulo());
            view.mostrarMensaje("Autor: " + libro.getAutor());
            view.mostrarMensaje("Editorial: " + editora);

        } catch (IllegalArgumentException e) {
            view.mostrarMensaje("Error: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarMensaje("Error inesperado al registrar: " + e.getMessage());
        }
    }

    /** Genera un nuevo ID secuencial tomando como base los libros existentes. */
    private int generarIdLibro() {
        List<Libro> existentes = service.getLibros();
        if (existentes.isEmpty()) return 1;
        return existentes.stream().mapToInt(Libro::getId).max().orElse(0) + 1;
    }

    /**
     * Si la API trae datos del autor, enriquece el autor registrado automaticamente con el libro.
     */
    private void actualizarAutorConDatosApi(Libro libro) {
        if (libro == null || libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            return;
        }

        String nombreAutor = libro.getAutor().trim();
        Autor autorExistente = service.getAutores().stream()
                .filter(a -> a.getNombre() != null && a.getNombre().trim().equalsIgnoreCase(nombreAutor))
                .findFirst()
                .orElse(null);

        if (autorExistente == null) {
            return;
        }

        String nacionalidadActual = autorExistente.getNacionalidad();
        int anioActual = autorExistente.getAnioNacimiento();

        String nacionalidadApi = libro.getAutorNacionalidad();
        Integer anioApi = libro.getAutorAnioNacimiento();

        String nacionalidadFinal = nacionalidadActual;
        if (nacionalidadApi != null && !nacionalidadApi.trim().isEmpty()
                && (nacionalidadActual == null || nacionalidadActual.trim().isEmpty()
                || "Desconocida".equalsIgnoreCase(nacionalidadActual.trim()))) {
            nacionalidadFinal = nacionalidadApi.trim();
        }

        int anioFinal = anioActual;
        if (anioApi != null && anioApi > 0 && anioActual <= 0) {
            anioFinal = anioApi;
        }

        if (nacionalidadFinal == null || nacionalidadFinal.trim().isEmpty()) {
            nacionalidadFinal = "Desconocida";
        }

        boolean cambioNacionalidad = !nacionalidadFinal.equals(nacionalidadActual);
        boolean cambioAnio = anioFinal != anioActual;

        if (cambioNacionalidad || cambioAnio) {
            service.editarAutor(autorExistente.getId(), autorExistente.getNombre(), nacionalidadFinal, anioFinal);
        }
    }
}

