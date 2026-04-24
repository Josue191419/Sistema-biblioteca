package org.example.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad que representa un prestamo de libro realizado por un usuario en la biblioteca.
 *
 * <p>
 * Un prestamo vincula un {@link Libro} con un {@link Usuario}, registrando la fecha
 * del prestamo, la fecha de devolucion, la biblioteca donde se realizo
 * y el bibliotecario que lo gestionó.
 * </p>
 *
 * <p><b>Conceptos aplicados:</b></p>
 * <ul>
 *   <li><b>Encapsulamiento:</b> Todos los atributos son privados con getters y setters.</li>
 *   <li><b>Asociacion:</b> Vincula un Libro con un Usuario.</li>
 *   <li><b>Sobrecarga de constructores:</b> Constructor con IDs (para MySQL) y con objetos completos.</li>
 *   <li><b>Polimorfismo:</b> Sobrescritura de toString(), equals() y hashCode().</li>
 * </ul>
 *
 * @author Josue
 * @version 2.0
 */
public class Prestamo {

    /** Identificador unico numerico del prestamo. */
    private int id;

    /** ID del libro prestado (clave foranea hacia la tabla libro). */
    private int libroId;

    /** ID del usuario que realiza el prestamo (clave foranea hacia la tabla usuario). */
    private int usuarioId;

    /** Fecha en la que se realizo el prestamo. */
    private LocalDate fechaPrestamo;

    /** Fecha en la que se debe devolver el libro. Por defecto igual a fechaPrestamo. */
    private LocalDate fechaDevolucion;

    /** Nombre de la biblioteca donde se realizo el prestamo. */
    private String bibliotecaNombre;

    /** Identificacion del bibliotecario que gestionó el prestamo. */
    private String bibliotecarioIdentificacion;

    /** Referencia opcional al objeto Libro completo (no guardado en BD directamente). */
    private Libro libro;

    /** Referencia opcional al objeto Usuario completo (no guardado en BD directamente). */
    private Usuario usuario;

    /**
     * Constructor compatible con repositorios MySQL que trabajan con IDs numericos.
     * La fecha de devolucion se inicializa igual a la fecha de prestamo.
     *
     * @param id            Identificador del prestamo.
     * @param libroId       ID del libro prestado.
     * @param usuarioId     ID del usuario.
     * @param fechaPrestamo Fecha en que se realiza el prestamo.
     */
    public Prestamo(int id, int libroId, int usuarioId, LocalDate fechaPrestamo) {
        this.id = id;
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaPrestamo;
        this.bibliotecaNombre = "";
        this.bibliotecarioIdentificacion = "";
    }

    /**
     * Constructor completo que recibe objetos Libro y Usuario directamente.
     * Extrae automaticamente los IDs de los objetos para la persistencia.
     *
     * @param id      Identificador del prestamo.
     * @param libro   Objeto Libro prestado.
     * @param usuario Objeto Usuario que realiza el prestamo.
     */
    public Prestamo(int id, Libro libro, Usuario usuario) {
        this.id = id;
        this.libro = libro;
        this.usuario = usuario;
        this.libroId = (libro != null) ? libro.getId() : 0;
        this.usuarioId = (usuario != null) ? usuario.getId() : 0;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucion = this.fechaPrestamo;
        this.bibliotecaNombre = "";
        this.bibliotecarioIdentificacion = "";
    }

    // ===============================
    // Getters y Setters
    // ===============================

    /** @return Identificador unico del prestamo. */
    public int getId() { return id; }

    /** @param id Nuevo identificador. */
    public void setId(int id) { this.id = id; }

    /** @return ID del libro asociado al prestamo. */
    public int getLibroId() { return libroId; }

    /** @param libroId Nuevo ID de libro. */
    public void setLibroId(int libroId) { this.libroId = libroId; }

    /** @return ID del usuario que realiza el prestamo. */
    public int getUsuarioId() { return usuarioId; }

    /** @param usuarioId Nuevo ID de usuario. */
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    /** @return Fecha en que se realizo el prestamo. */
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }

    /** @param fechaPrestamo Nueva fecha de prestamo. */
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    /** @return Fecha limite de devolucion del libro. */
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }

    /** @param fechaDevolucion Nueva fecha de devolucion. */
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    /** @return Nombre de la biblioteca donde se registro el prestamo. */
    public String getBibliotecaNombre() { return bibliotecaNombre; }

    /** @param bibliotecaNombre Nombre de la biblioteca. Null se convierte a cadena vacia. */
    public void setBibliotecaNombre(String bibliotecaNombre) {
        this.bibliotecaNombre = bibliotecaNombre != null ? bibliotecaNombre : "";
    }

    /** @return Identificacion del bibliotecario que gestionó el prestamo. */
    public String getBibliotecarioIdentificacion() { return bibliotecarioIdentificacion; }

    /** @param bibliotecarioIdentificacion Identificacion del bibliotecario. Null se convierte a cadena vacia. */
    public void setBibliotecarioIdentificacion(String bibliotecarioIdentificacion) {
        this.bibliotecarioIdentificacion = bibliotecarioIdentificacion != null ? bibliotecarioIdentificacion : "";
    }

    /** @return Objeto Libro asociado, o null si solo se cargaron IDs. */
    public Libro getLibro() { return libro; }

    /**
     * Asigna el objeto Libro y actualiza el libroId automaticamente.
     *
     * @param libro Objeto Libro a asociar.
     */
    public void setLibro(Libro libro) {
        this.libro = libro;
        if (libro != null) this.libroId = libro.getId();
    }

    /** @return Objeto Usuario asociado, o null si solo se cargaron IDs. */
    public Usuario getUsuario() { return usuario; }

    /**
     * Asigna el objeto Usuario y actualiza el usuarioId automaticamente.
     *
     * @param usuario Objeto Usuario a asociar.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) this.usuarioId = usuario.getId();
    }

    // ===============================
    // Metodos de comportamiento
    // ===============================

    /**
     * Genera un resumen detallado del prestamo mostrando libro, usuario,
     * biblioteca, bibliotecario y fechas.
     *
     * @return Cadena con toda la informacion del prestamo.
     */
    public String resumenPrestamo() {
        String tituloLibro = (libro != null) ? libro.getTitulo() : String.valueOf(libroId);
        String nombreUsuario = (usuario != null) ? usuario.getNombre() : String.valueOf(usuarioId);
        String bibliotecaTexto = (bibliotecaNombre != null && !bibliotecaNombre.isBlank()) ? bibliotecaNombre : "N/A";
        String bibliotecarioTexto = (bibliotecarioIdentificacion != null && !bibliotecarioIdentificacion.isBlank()) ? bibliotecarioIdentificacion : "N/A";
        return "Prestamo{" +
                "id=" + id +
                ", Libro='" + tituloLibro + '\'' +
                ", Usuario='" + nombreUsuario + '\'' +
                ", Biblioteca='" + bibliotecaTexto + '\'' +
                ", Bibliotecario='" + bibliotecarioTexto + '\'' +
                ", FechaPrestamo=" + fechaPrestamo +
                ", FechaDevolucion=" + fechaDevolucion +
                '}';
    }

    // ===============================
    // Polimorfismo
    // ===============================

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return resumenPrestamo();
    }

    /**
     * Dos prestamos son iguales si tienen el mismo ID.
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Prestamo)) return false;
        Prestamo other = (Prestamo) obj;
        return this.id == other.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
