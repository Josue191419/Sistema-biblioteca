package org.example.repository;

import org.example.model.Biblioteca;

/**
 * Contrato de persistencia para la biblioteca activa del sistema.
 */
public interface IBibliotecaEntidadRepository {
    /** Guarda o actualiza la biblioteca activa. */
    void guardarOActualizar(Biblioteca biblioteca);
    /** Obtiene la biblioteca activa. */
    Biblioteca obtenerActiva();
}

