package org.example.model;

import java.util.Objects;

/**
 * Entidad que representa un autor dentro del sistema de gestion bibliotecaria.
 *
 * <p><b>Conceptos aplicados:</b></p>
 * <ul>
 *   <li><b>Encapsulamiento:</b> Atributos privados con acceso mediante getters y setters.</li>
 *   <li><b>Sobrecarga de constructores:</b> Constructor con y sin anio de nacimiento.</li>
 *   <li><b>Polimorfismo:</b> Sobrescritura de toString(), equals() y hashCode().</li>
 * </ul>
 *
 * <p>
 * Se guarda solo el anio de nacimiento (no la fecha completa) para simplificar
 * la representacion en base de datos y en consola.
 * </p>
 *
 * @author Josue
 * @version 1.0
 */
public class Autor {

    /** Identificador unico numerico del autor. */
    private int id;

    /** Nombre completo del autor. */
    private String nombre;

    /** Nacionalidad del autor. */
    private String nacionalidad;

    /** Anio de nacimiento del autor. 0 si no fue definido. */
    private int anioNacimiento;

    /**
     * Constructor completo con todos los campos del autor.
     *
     * @param id              Identificador unico.
     * @param nombre          Nombre completo del autor.
     * @param nacionalidad    Nacionalidad del autor.
     * @param anioNacimiento  Anio de nacimiento del autor.
     */
    public Autor(int id, String nombre, String nacionalidad, int anioNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.anioNacimiento = anioNacimiento;
    }

    /**
     * Constructor simplificado sin anio de nacimiento.
     * El anio queda en 0 indicando que no fue definido.
     *
     * @param id           Identificador unico.
     * @param nombre       Nombre completo del autor.
     * @param nacionalidad Nacionalidad del autor.
     */
    public Autor(int id, String nombre, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    // ===============================
    // Getters y Setters
    // ===============================

    /** @return Identificador unico del autor. */
    public int getId() { return id; }

    /** @param id Nuevo identificador. */
    public void setId(int id) { this.id = id; }

    /** @return Nombre completo del autor. */
    public String getNombre() { return nombre; }

    /** @param nombre Nuevo nombre. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return Nacionalidad del autor, o null si no fue definida. */
    public String getNacionalidad() { return nacionalidad; }

    /** @param nacionalidad Nueva nacionalidad. */
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    /** @return Anio de nacimiento, o 0 si no fue definido. */
    public int getAnioNacimiento() { return anioNacimiento; }

    /** @param anioNacimiento Nuevo anio de nacimiento. */
    public void setAnioNacimiento(int anioNacimiento) { this.anioNacimiento = anioNacimiento; }

    // ===============================
    // Polimorfismo
    // ===============================

    /**
     * Muestra el autor con nombre, nacionalidad y anio de nacimiento.
     * Si el anio es 0, muestra "N/A".
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String anio = (anioNacimiento > 0) ? String.valueOf(anioNacimiento) : "N/A";
        return "Autor: " + nombre +
                " | Nacionalidad: " + (nacionalidad != null ? nacionalidad : "N/A") +
                " | Año nacimiento: " + anio;
    }

    /**
     * Dos autores son iguales si tienen el mismo ID.
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Autor)) return false;
        Autor other = (Autor) obj;
        return this.id == other.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
