package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa la biblioteca como institucion dentro del sistema.
 *
 * <p>
 * Actua como contenedor principal que agrupa libros, usuarios, bibliotecarios y prestamos.
 * Solo existe una biblioteca activa a la vez en el sistema (gestionada por BibliotecaEntidadRepository).
 * </p>
 *
 * <p><b>Conceptos aplicados:</b></p>
 * <ul>
 *   <li><b>Encapsulamiento:</b> Atributos privados con getters y setters.</li>
 *   <li><b>Agregacion:</b> Contiene listas de Libro, Usuario, Bibliotecario y Prestamo.</li>
 *   <li><b>Polimorfismo:</b> Sobrescritura de toString(), equals() y hashCode().</li>
 * </ul>
 *
 * @author Josue
 * @version 1.0
 */
public class Biblioteca {

    /** Nombre de la biblioteca. Actua como identificador unico. */
    private String nombre;

    /** Direccion fisica de la biblioteca. */
    private String direccion;

    /** Numero de telefono de contacto. */
    private String telefono;

    /** Cantidad maxima de libros que puede contener la biblioteca. */
    private int capacidad;

    /** Lista de libros registrados en la biblioteca. */
    private List<Libro> libros;

    /** Lista de usuarios registrados en la biblioteca. */
    private List<Usuario> usuarios;

    /** Lista de bibliotecarios asignados a la biblioteca. */
    private List<Bibliotecario> bibliotecarios;

    /** Lista de prestamos realizados en la biblioteca. */
    private List<Prestamo> prestamos;

    /**
     * Constructor completo para crear una nueva biblioteca.
     *
     * @param nombre    Nombre de la biblioteca.
     * @param direccion Direccion fisica.
     * @param telefono  Telefono de contacto.
     * @param capacidad Capacidad maxima de libros.
     */
    public Biblioteca(String nombre, String direccion, String telefono, int capacidad) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.capacidad = capacidad;
        this.libros = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.bibliotecarios = new ArrayList<>();
        this.prestamos = new ArrayList<>();
    }

    // ===============================
    // Getters y Setters
    // ===============================

    /** @return Nombre de la biblioteca. */
    public String getNombre() { return nombre; }

    /** @param nombre Nuevo nombre. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return Direccion de la biblioteca. */
    public String getDireccion() { return direccion; }

    /** @param direccion Nueva direccion. */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** @return Telefono de contacto. */
    public String getTelefono() { return telefono; }

    /** @param telefono Nuevo telefono. */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /** @return Capacidad maxima de libros. */
    public int getCapacidad() { return capacidad; }

    /** @param capacidad Nueva capacidad. */
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    /** @return Lista de libros registrados. */
    public List<Libro> getLibros() { return libros; }

    /** @return Lista de usuarios registrados. */
    public List<Usuario> getUsuarios() { return usuarios; }

    /** @return Lista de bibliotecarios asignados. */
    public List<Bibliotecario> getBibliotecarios() { return bibliotecarios; }

    /** @return Lista de prestamos realizados. */
    public List<Prestamo> getPrestamos() { return prestamos; }

    // ===============================
    // Metodos de comportamiento
    // ===============================

    /**
     * Agrega un libro a la biblioteca si hay espacio disponible y no existe previamente.
     *
     * @param libro Libro a agregar.
     */
    public void agregarLibro(Libro libro) {
        if (libro != null && libros.size() < capacidad) {
            boolean existe = libros.stream().anyMatch(l -> l.getId() == libro.getId());
            if (!existe) {
                libros.add(libro);
            }
        }
    }

    /**
     * Registra un nuevo usuario en la biblioteca.
     *
     * @param usuario Usuario a registrar.
     */
    public void agregarUsuario(Usuario usuario) {
        if (usuario != null) usuarios.add(usuario);
    }

    /**
     * Agrega un bibliotecario a la lista de la biblioteca.
     *
     * @param bibliotecario Bibliotecario a agregar.
     */
    public void agregarBibliotecario(Bibliotecario bibliotecario) {
        if (bibliotecario != null) bibliotecarios.add(bibliotecario);
    }

    /**
     * Registra un prestamo en la biblioteca.
     *
     * @param prestamo Prestamo a registrar.
     */
    public void agregarPrestamo(Prestamo prestamo) {
        if (prestamo != null) prestamos.add(prestamo);
    }

    /**
     * Retorna la cantidad actual de libros registrados.
     *
     * @return Numero total de libros.
     */
    public int getCantidadLibros() { return libros.size(); }

    /**
     * Retorna un resumen de la biblioteca con nombre, direccion, libros y usuarios.
     *
     * @return Cadena descriptiva de la biblioteca.
     */
    public String getInfoBiblioteca() {
        return "Biblioteca: " + nombre + " - " + direccion +
                " | Libros: " + libros.size() +
                " | Usuarios: " + usuarios.size();
    }

    // ===============================
    // Polimorfismo
    // ===============================

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Biblioteca '" + nombre + "' ubicada en " + direccion +
                ". Libros registrados: " + libros.size() +
                ", Usuarios: " + usuarios.size() +
                ", Préstamos: " + prestamos.size();
    }

    /**
     * Dos bibliotecas son iguales si tienen el mismo nombre.
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Biblioteca)) return false;
        Biblioteca other = (Biblioteca) obj;
        return Objects.equals(nombre, other.nombre);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(nombre); }
}
