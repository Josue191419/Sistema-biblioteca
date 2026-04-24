package org.example.model;

/**
 * Entidad que representa a un bibliotecario dentro del sistema de gestion bibliotecaria.
 *
 * <p><b>Conceptos aplicados:</b></p>
 * <ul>
 *   <li><b>Encapsulamiento:</b> Atributos privados con getters y setters.</li>
 *   <li><b>Asociacion bidireccional:</b> Un bibliotecario puede estar asignado a una Biblioteca.
 *       Al asignarlo, se agrega automaticamente a la lista de bibliotecarios de esa biblioteca.</li>
 *   <li><b>Polimorfismo:</b> Sobrescritura de toString().</li>
 * </ul>
 *
 * @author Josue
 * @version 1.0
 */
public class Bibliotecario {

    /** Nombre completo del bibliotecario. */
    private String nombre;

    /** Identificacion unica del bibliotecario (cedula, carnet, etc.). */
    private String identificacion;

    /** Turno de trabajo asignado (Manana, Tarde, Noche, etc.). */
    private String turno;

    /** Referencia a la biblioteca a la que esta asignado. Puede ser null. */
    private Biblioteca bibliotecaAsignada;

    /**
     * Constructor completo del bibliotecario.
     *
     * @param nombre         Nombre completo.
     * @param identificacion Identificacion unica.
     * @param turno          Turno de trabajo.
     */
    public Bibliotecario(String nombre, String identificacion, String turno) {
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.turno = turno;
    }

    // ===============================
    // Getters y Setters
    // ===============================

    /** @return Nombre del bibliotecario. */
    public String getNombre() { return nombre; }

    /** @param nombre Nuevo nombre. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return Identificacion unica del bibliotecario. */
    public String getIdentificacion() { return identificacion; }

    /** @param identificacion Nueva identificacion. */
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    /** @return Turno de trabajo del bibliotecario. */
    public String getTurno() { return turno; }

    /** @param turno Nuevo turno. */
    public void setTurno(String turno) { this.turno = turno; }

    /** @return Biblioteca asignada, o null si no tiene ninguna. */
    public Biblioteca getBibliotecaAsignada() { return bibliotecaAsignada; }

    /**
     * Asigna una biblioteca al bibliotecario y mantiene la asociacion bidireccional.
     * Si la biblioteca no contiene a este bibliotecario, lo agrega automaticamente.
     *
     * @param bibliotecaAsignada Biblioteca a asignar.
     */
    public void setBibliotecaAsignada(Biblioteca bibliotecaAsignada) {
        if (bibliotecaAsignada != null) {
            this.bibliotecaAsignada = bibliotecaAsignada;
            if (!bibliotecaAsignada.getBibliotecarios().contains(this)) {
                bibliotecaAsignada.agregarBibliotecario(this);
            }
        }
    }

    // ===============================
    // Polimorfismo
    // ===============================

    /**
     * Muestra los datos del bibliotecario incluyendo la biblioteca asignada.
     * Si no tiene biblioteca asignada, muestra "Sin asignar".
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String nombreBiblioteca = (bibliotecaAsignada != null)
                ? bibliotecaAsignada.getNombre()
                : "Sin asignar";

        return "Bibliotecario: " + nombre +
                " | ID: " + identificacion +
                " | Turno: " + turno +
                " | Biblioteca: " + nombreBiblioteca;
    }
}
