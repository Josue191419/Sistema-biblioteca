package org.example.repository;

import org.example.model.Autor;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del repositorio de autores en memoria.
 *
 * <p>
 * Almacena los autores en una lista interna de {@link ArrayList}.
 * Los datos persisten solo durante la ejecucion del programa.
 * Util para pruebas o entornos sin base de datos.
 * </p>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link IAutorRepository}.</p>
 *
 * @author Josue
 * @version 1.0
 * @see IAutorRepository
 */
public class AutorRepositoryMemoria implements IAutorRepository {

    /** Lista interna que actua como almacenamiento temporal en memoria. */
    private final List<Autor> autores = new ArrayList<>();

    /**
     * Agrega un autor al almacenamiento en memoria.
     *
     * @param autor Autor a guardar.
     */
    @Override
    public void guardar(Autor autor) {
        autores.add(autor);
    }

    /**
     * Retorna una copia de todos los autores almacenados.
     *
     * @return Lista con todos los autores.
     */
    @Override
    public List<Autor> obtenerTodos() {
        return new ArrayList<>(autores);
    }

    /**
     * Busca un autor por su ID.
     *
     * @param id ID del autor a buscar.
     * @return El autor encontrado, o {@code null} si no existe.
     */
    @Override
    public Autor buscarPorId(int id) {
        for (Autor autor : autores) {
            if (autor.getId() == id) {
                return autor;
            }
        }
        return null;
    }

    /**
     * Busca un autor por nombre ignorando mayusculas/minusculas y espacios laterales.
     *
     * @param nombre Nombre del autor a buscar.
     * @return El autor encontrado, o {@code null} si no existe.
     */
    @Override
    public Autor buscarPorNombre(String nombre) {
        if (nombre == null) {
            return null;
        }

        String nombreNormalizado = nombre.trim();
        for (Autor autor : autores) {
            if (autor.getNombre() != null && autor.getNombre().trim().equalsIgnoreCase(nombreNormalizado)) {
                return autor;
            }
        }
        return null;
    }

    /**
     * Reemplaza un autor existente por el autor actualizado (busqueda por ID).
     *
     * @param autorActualizado Autor con los nuevos datos.
     * @throws RuntimeException Si no existe un autor con ese ID en memoria.
     */
    @Override
    public void actualizar(Autor autorActualizado) {
        for (int i = 0; i < autores.size(); i++) {
            if (autores.get(i).getId() == autorActualizado.getId()) {
                autores.set(i, autorActualizado);
                return;
            }
        }
        throw new RuntimeException("No existe el autor a actualizar en memoria.");
    }

    /**
     * Elimina el autor con el ID especificado.
     *
     * @param id ID del autor a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no existia.
     */
    @Override
    public boolean eliminarPorId(int id) {
        return autores.removeIf(autor -> autor.getId() == id);
    }
}
