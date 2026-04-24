package org.example.model;

import java.util.Objects;

/**
 * Entidad que representa un usuario registrado en el sistema de gestion bibliotecaria.
 *
 * <p><b>Conceptos aplicados:</b></p>
 * <ul>
 *   <li><b>Encapsulamiento:</b> Atributos privados con acceso mediante getters y setters.</li>
 *   <li><b>Sobrecarga de constructores:</b> Constructor simplificado y completo segun necesidad.</li>
 *   <li><b>Polimorfismo:</b> Sobrescritura de toString(), equals() y hashCode().</li>
 * </ul>
 *
 * @author Josue
 * @version 2.0
 */
public class Usuario {

    /** Identificador unico numerico del usuario en la base de datos. */
    private int id;

    /** Nombre completo del usuario. */
    private String nombre;

    /** Numero de identificacion unico del usuario (cedula, carnet, etc.). */
    private String identificacion;

    /** Categoria del usuario: Estudiante, Profesor o Visitante. */
    private String tipo;

    /**
     * Constructor simplificado compatible con repositorios MySQL que solo usan id y nombre.
     *
     * @param id     Identificador numerico del usuario.
     * @param nombre Nombre completo del usuario.
     */
    public Usuario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    /**
     * Constructor completo con todos los campos del usuario.
     *
     * @param id             Identificador numerico del usuario.
     * @param nombre         Nombre completo del usuario.
     * @param identificacion Numero de identificacion unico.
     * @param tipo           Tipo de usuario (Estudiante/Profesor/Visitante).
     */
    public Usuario(int id, String nombre, String identificacion, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.tipo = tipo;
    }

    // ===============================
    // Getters y Setters
    // ===============================

    /** @return Identificador numerico del usuario. */
    public int getId() { return id; }

    /** @param id Nuevo identificador numerico. */
    public void setId(int id) { this.id = id; }

    /** @return Nombre completo del usuario. */
    public String getNombre() { return nombre; }

    /** @param nombre Nuevo nombre. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return Numero de identificacion unico, o null si no fue definido. */
    public String getIdentificacion() { return identificacion; }

    /** @param identificacion Nueva identificacion. */
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    /** @return Tipo del usuario (Estudiante/Profesor/Visitante), o null si no fue definido. */
    public String getTipo() { return tipo; }

    /** @param tipo Nuevo tipo de usuario. */
    public void setTipo(String tipo) { this.tipo = tipo; }

    // ===============================
    // Metodos de comportamiento
    // ===============================

    /**
     * Accion generica asociada al usuario dentro del sistema.
     * Imprime un mensaje indicando la accion principal del usuario.
     */
    public void accionPrincipal() {
        System.out.println(nombre + " está realizando su acción principal.");
    }

    // ===============================
    // Polimorfismo
    // ===============================

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                (tipo != null ? ", tipo='" + tipo + '\'' : "") +
                '}';
    }

    /**
     * Dos usuarios son iguales si tienen el mismo ID numerico.
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Usuario)) return false;
        Usuario other = (Usuario) obj;
        return this.id == other.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
