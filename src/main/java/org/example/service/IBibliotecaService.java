package org.example.service;

import org.example.model.Autor;
import org.example.model.Biblioteca;
import org.example.model.Bibliotecario;
import org.example.model.Libro;
import org.example.model.Prestamo;
import org.example.model.Usuario;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato del servicio principal de la biblioteca.
 *
 * <p>Define los casos de uso del sistema por modulo: libros, usuarios,
 * prestamos, autores, biblioteca y bibliotecarios.</p>
 */
public interface IBibliotecaService {

    // Libros
    /** Registra un libro con datos basicos. */
    void registrarLibro(int id, String titulo, String autor);
    /** Registra un libro con metadatos completos de publicacion. */
    void registrarLibro(int id, String titulo, String autor, String editora, LocalDate fechaPublicacion);
    /** Edita un libro existente identificado por su ID. */
    void editarLibro(int id, String titulo, String autor, String editora, LocalDate fechaPublicacion);
    /** Elimina un libro por ID. */
    void eliminarLibro(int id);
    /** Obtiene un libro segun su posicion dentro de la lista actual. */
    Libro obtenerLibroPorIndice(int index);
    /** Retorna el listado completo de libros. */
    List<Libro> getLibros();

    // Usuarios
    /** Registra un usuario con datos basicos. */
    void registrarUsuario(int id, String nombre);
    /** Registra un usuario con identificacion y tipo. */
    void registrarUsuario(int id, String nombre, String identificacion, String tipo);
    /** Edita un usuario existente por ID. */
    void editarUsuario(int id, String nombre, String identificacion, String tipo);
    /** Elimina un usuario por ID. */
    void eliminarUsuario(int id);
    /** Obtiene un usuario por posicion en la lista actual. */
    Usuario obtenerUsuarioPorIndice(int index);
    /** Retorna el listado completo de usuarios. */
    List<Usuario> getUsuarios();

    // Prestamos
    /** Registra un prestamo para un libro y usuario dados. */
    void registrarPrestamo(int idPrestamo, int idLibro, int idUsuario, String bibliotecaNombre, String bibliotecarioIdentificacion);
    /** Edita los datos de un prestamo existente. */
    void editarPrestamo(int id, int libroId, int usuarioId, LocalDate fechaPrestamo, LocalDate fechaDevolucion,
                        String bibliotecaNombre, String bibliotecarioIdentificacion);
    /** Elimina un prestamo por ID. */
    void eliminarPrestamo(int id);
    /** Retorna todos los prestamos registrados. */
    List<Prestamo> getPrestamos();

    // ================== AUTORES ==================
    /** Registra un autor con datos basicos. */
    void registrarAutor(int id, String nombre, String nacionalidad);
    /** Registra un autor incluyendo anio de nacimiento. */
    void registrarAutor(int id, String nombre, String nacionalidad, int anioNacimiento);
    /** Edita un autor existente por ID. */
    void editarAutor(int id, String nombre, String nacionalidad, int anioNacimiento);
    /** Elimina un autor por ID. */
    void eliminarAutor(int id);
    /** Obtiene un autor por posicion en la lista actual. */
    Autor obtenerAutorPorIndice(int index);
    /** Retorna el listado completo de autores. */
    List<Autor> getAutores();

    // Biblioteca
    /** Crea o actualiza la biblioteca activa del sistema. */
    void crearBiblioteca(String nombre, String direccion, String telefono, int capacidad);
    /** Retorna la biblioteca activa; puede ser nula si no existe. */
    Biblioteca getBiblioteca();

    // Bibliotecarios
    /** Registra un bibliotecario. */
    void registrarBibliotecario(String nombre, String identificacion, String turno);
    /** Edita un bibliotecario por identificacion. */
    void editarBibliotecario(String identificacion, String nombre, String turno);
    /** Elimina un bibliotecario por identificacion. */
    void eliminarBibliotecario(String identificacion);
    /** Retorna el listado completo de bibliotecarios. */
    List<Bibliotecario> getBibliotecarios();
}
