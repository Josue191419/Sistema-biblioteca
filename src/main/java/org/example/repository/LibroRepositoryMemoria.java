package org.example.repository;

import org.example.model.Libro;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del repositorio de libros en memoria.
 *
 * <p>
 * Almacena los libros en una lista interna de {@link ArrayList}.
 * Los datos persisten solo durante la ejecucion del programa.
 * Util para pruebas o entornos sin base de datos.
 * </p>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link ILibroRepository}.</p>
 *
 * @author Josue
 * @version 1.0
 * @see ILibroRepository
 */
public class LibroRepositoryMemoria implements ILibroRepository {

    /** Lista interna que actua como almacenamiento temporal en memoria. */
    private final List<Libro> libros = new ArrayList<>();

    /**
     * Agrega un libro al almacenamiento en memoria.
     *
     * @param libro Libro a guardar.
     */
    @Override
    public void guardar(Libro libro) {
        libros.add(libro);
    }

    /**
     * Retorna una copia de todos los libros almacenados.
     *
     * @return Lista con todos los libros.
     */
    @Override
    public List<Libro> obtenerTodos() {
        return new ArrayList<>(libros);
    }

    /**
     * Busca un libro por su ID.
     *
     * @param id ID del libro a buscar.
     * @return El libro encontrado, o {@code null} si no existe.
     */
    @Override
    public Libro buscarPorId(int id) {
        for (Libro libro : libros) {
            if (libro.getId() == id) {
                return libro;
            }
        }
        return null;
    }

    /**
     * Reemplaza un libro existente por el libro actualizado (busqueda por ID).
     *
     * @param libroActualizado Libro con los nuevos datos.
     * @throws RuntimeException Si no existe un libro con ese ID en memoria.
     */
    @Override
    public void actualizar(Libro libroActualizado) {
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).getId() == libroActualizado.getId()) {
                libros.set(i, libroActualizado);
                return;
            }
        }
        throw new RuntimeException("No existe el libro a actualizar en memoria.");
    }

    /**
     * Elimina el libro con el ID especificado.
     *
     * @param id ID del libro a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no existia.
     */
    @Override
    public boolean eliminarPorId(int id) {
        return libros.removeIf(libro -> libro.getId() == id);
    }
}
