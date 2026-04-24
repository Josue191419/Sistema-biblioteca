package org.example.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa un libro dentro del sistema de gestion bibliotecaria.
 *
 * <p><b>Conceptos aplicados:</b></p>
 * <ul>
 *   <li><b>Encapsulamiento:</b> Atributos privados accesibles mediante getters y setters.</li>
 *   <li><b>Asociacion bidireccional:</b> Un libro puede pertenecer a una Biblioteca.</li>
 *   <li><b>Polimorfismo:</b> Sobrescritura de toString(), equals() y hashCode().</li>
 * </ul>
 *
 * @author Josue
 * @version 2.0
 */
public class Libro {

    /** Identificador unico numerico del libro. */
    private int id;

    /** Titulo del libro. */
    private String titulo;

    /** Nombre del autor del libro. */
    private String autor;

    /** Editorial que publico el libro. */
    private String editora;

    /** Fecha de publicacion del libro. Puede ser null si no se conoce. */
    private LocalDate fechaPublicacion;

    /** Indica si el libro esta disponible para prestamo (true) o prestado (false). */
    private boolean disponible;

    /** Referencia a la biblioteca a la que pertenece el libro. Puede ser null. */
    private Biblioteca biblioteca;

    /** Nacionalidad del autor obtenida desde API (opcional). */
    private String autorNacionalidad;

    /** Anio de nacimiento del autor obtenido desde API (opcional). */
    private Integer autorAnioNacimiento;

    /**
     * Constructor completo con todos los campos del libro.
     *
     * @param id              Identificador unico del libro.
     * @param titulo          Titulo del libro.
     * @param autor           Nombre del autor.
     * @param editora         Editorial del libro.
     * @param fechaPublicacion Fecha de publicacion.
     */
    public Libro(int id, String titulo, String autor, String editora, LocalDate fechaPublicacion) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.fechaPublicacion = fechaPublicacion;
        this.disponible = true;
    }

    /**
     * Constructor simplificado sin editora ni fecha de publicacion.
     * Usado principalmente por repositorios MySQL cuando la tabla no tiene esas columnas.
     *
     * @param id     Identificador unico del libro.
     * @param titulo Titulo del libro.
     * @param autor  Nombre del autor.
     */
    public Libro(int id, String titulo, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = true;
    }

    // ===============================
    // Getters y Setters
    // ===============================

    /** @return Identificador unico del libro. */
    public int getId() { return id; }

    /** @param id Nuevo identificador. */
    public void setId(int id) { this.id = id; }

    /** @return Titulo del libro. */
    public String getTitulo() { return titulo; }

    /** @param titulo Nuevo titulo. */
    public void setTitulo(String titulo) { this.titulo = titulo; }

    /** @return Nombre del autor. */
    public String getAutor() { return autor; }

    /** @param autor Nuevo autor. */
    public void setAutor(String autor) { this.autor = autor; }

    /** @return Editorial del libro o null si no fue definida. */
    public String getEditora() { return editora; }

    /** @param editora Nueva editorial. */
    public void setEditora(String editora) { this.editora = editora; }

    /** @return Fecha de publicacion o null si no fue definida. */
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }

    /** @param fechaPublicacion Nueva fecha de publicacion. */
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    /** @return true si el libro esta disponible para prestamo. */
    public boolean isDisponible() { return disponible; }

    /** @param disponible Estado de disponibilidad del libro. */
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    /** @return Biblioteca asociada al libro, o null si no tiene. */
    public Biblioteca getBiblioteca() { return biblioteca; }

    /** @return Nacionalidad del autor obtenida desde API, o null si no se conoce. */
    public String getAutorNacionalidad() { return autorNacionalidad; }

    /** @param autorNacionalidad Nacionalidad del autor proveniente de API. */
    public void setAutorNacionalidad(String autorNacionalidad) { this.autorNacionalidad = autorNacionalidad; }

    /** @return Anio de nacimiento del autor desde API, o null si no se conoce. */
    public Integer getAutorAnioNacimiento() { return autorAnioNacimiento; }

    /** @param autorAnioNacimiento Anio de nacimiento del autor proveniente de API. */
    public void setAutorAnioNacimiento(Integer autorAnioNacimiento) { this.autorAnioNacimiento = autorAnioNacimiento; }

    /**
     * Asigna el libro a una biblioteca y mantiene la asociacion bidireccional.
     * Si la biblioteca no contiene este libro, lo agrega automaticamente.
     *
     * @param biblioteca Biblioteca a la que se asigna el libro.
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        if (biblioteca != null) {
            this.biblioteca = biblioteca;
            if (!biblioteca.getLibros().contains(this)) {
                biblioteca.agregarLibro(this);
            }
        }
    }

    // ===============================
    // Metodos de comportamiento
    // ===============================

    /**
     * Calcula los anios transcurridos desde la publicacion del libro hasta hoy.
     *
     * @return Antiguedad en anios, o 0 si la fecha de publicacion no fue definida.
     */
    public int getAntiguedad() {
        if (fechaPublicacion == null) return 0;
        return LocalDate.now().getYear() - fechaPublicacion.getYear();
    }

    /**
     * Retorna un resumen breve del libro con titulo, autor y anio.
     *
     * @return Cadena en formato "Titulo - Autor (Anio)" o "Titulo - Autor" si no hay fecha.
     */
    public String getInfoLibro() {
        if (fechaPublicacion != null) {
            return titulo + " - " + autor + " (" + fechaPublicacion.getYear() + ")";
        }
        return titulo + " - " + autor;
    }

    // ===============================
    // Polimorfismo
    // ===============================

    /** {@inheritDoc} */
    @Override
    public String toString() {
        String nombreBiblioteca = (biblioteca != null) ? biblioteca.getNombre() : "Sin asignar";
        String anio = (fechaPublicacion != null) ? String.valueOf(fechaPublicacion.getYear()) : "N/A";
        return "Libro: " + titulo +
                " | Autor: " + autor +
                " | Editorial: " + (editora != null ? editora : "N/A") +
                " | Año: " + anio +
                " | Disponible: " + disponible +
                " | Biblioteca: " + nombreBiblioteca;
    }

    /**
     * Dos libros son iguales si tienen el mismo ID.
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Libro)) return false;
        Libro other = (Libro) obj;
        return this.id == other.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
