package org.example.repository;

import org.example.model.Prestamo;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del repositorio de prestamos en memoria.
 *
 * <p>
 * Almacena los prestamos en una lista interna de {@link ArrayList}.
 * Los datos persisten solo durante la ejecucion del programa.
 * Esta clase es util para pruebas o cuando no se cuenta con base de datos.
 * </p>
 *
 * <p><b>Patron aplicado:</b> Repository Pattern. Implementa {@link IPrestamoRepository}.</p>
 *
 * <p><b>Nota:</b> Para produccion, usar {@code PrestamoRepositoryMySQL}.</p>
 *
 * @author Josue
 * @version 1.0
 * @see IPrestamoRepository
 * @see org.example.repository.PrestamoRepositoryMySQL
 */
public class PrestamoRepositoryMemoria implements IPrestamoRepository {

    /** Lista interna que actua como almacenamiento temporal en memoria. */
    private final List<Prestamo> prestamos = new ArrayList<>();

    /**
     * Agrega un prestamo al almacenamiento en memoria.
     *
     * @param prestamo Prestamo a guardar. No puede ser null.
     */
    @Override
    public void guardar(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    /**
     * Retorna una copia de todos los prestamos almacenados.
     * Se retorna una nueva lista para proteger el estado interno.
     *
     * @return Lista con todos los prestamos.
     */
    @Override
    public List<Prestamo> obtenerTodos() {
        return new ArrayList<>(prestamos);
    }

    /**
     * Busca un prestamo por su ID numerico.
     *
     * @param id ID del prestamo a buscar.
     * @return El prestamo encontrado, o {@code null} si no existe.
     */
    @Override
    public Prestamo buscarPorId(int id) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getId() == id) {
                return prestamo;
            }
        }
        return null;
    }

    /**
     * Reemplaza un prestamo existente por el prestamo actualizado.
     * La busqueda se realiza por ID.
     *
     * @param prestamoActualizado Prestamo con los nuevos datos.
     * @throws RuntimeException Si no existe un prestamo con ese ID en la lista.
     */
    @Override
    public void actualizar(Prestamo prestamoActualizado) {
        for (int i = 0; i < prestamos.size(); i++) {
            if (prestamos.get(i).getId() == prestamoActualizado.getId()) {
                prestamos.set(i, prestamoActualizado);
                return;
            }
        }
        throw new RuntimeException("No existe el prestamo a actualizar en memoria.");
    }

    /**
     * Elimina el prestamo con el ID especificado.
     *
     * @param id ID del prestamo a eliminar.
     * @return {@code true} si fue eliminado, {@code false} si no existia.
     */
    @Override
    public boolean eliminarPorId(int id) {
        return prestamos.removeIf(prestamo -> prestamo.getId() == id);
    }
}
