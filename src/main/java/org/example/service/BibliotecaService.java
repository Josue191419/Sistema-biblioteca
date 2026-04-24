package org.example.service;

import org.example.model.Autor;
import org.example.model.Biblioteca;
import org.example.model.Bibliotecario;
import org.example.model.Libro;
import org.example.model.Prestamo;
import org.example.model.Usuario;
import org.example.repository.IAutorRepository;
import org.example.repository.IBibliotecaEntidadRepository;
import org.example.repository.IBibliotecarioRepository;
import org.example.repository.ILibroRepository;
import org.example.repository.IPrestamoRepository;
import org.example.repository.IUsuarioRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementacion de la logica de negocio del sistema de gestion bibliotecaria.
 *
 * <p>
 * Esta clase centraliza todas las reglas de negocio del sistema: validaciones,
 * verificaciones de existencia, restricciones de eliminacion y orquestacion
 * entre repositorios. Implementa la interfaz {@link IBibliotecaService}.
 * </p>
 *
 * <p><b>Modulos que gestiona:</b></p>
 * <ul>
 *   <li>Libros: registrar, editar, eliminar, listar.</li>
 *   <li>Usuarios: registrar, editar, eliminar, listar.</li>
 *   <li>Prestamos: registrar, editar, eliminar, listar.</li>
 *   <li>Autores: registrar, editar, eliminar, listar.</li>
 *   <li>Biblioteca: crear o actualizar la biblioteca activa.</li>
 *   <li>Bibliotecarios: registrar, editar, eliminar, listar.</li>
 * </ul>
 *
 * <p><b>Patron aplicado:</b> Inyeccion de dependencias via constructor.</p>
 *
 * @author Josue
 * @version 1.0
 */
public class BibliotecaService implements IBibliotecaService {

    /** Repositorio para operaciones CRUD de libros. */
    private final ILibroRepository libroRepository;

    /** Repositorio para operaciones CRUD de usuarios. */
    private final IUsuarioRepository usuarioRepository;

    /** Repositorio para operaciones CRUD de prestamos. */
    private final IPrestamoRepository prestamoRepository;

    /** Repositorio para operaciones CRUD de autores. */
    private final IAutorRepository autorRepository;

    /** Repositorio para gestionar la biblioteca activa. */
    private final IBibliotecaEntidadRepository bibliotecaEntidadRepository;

    /** Repositorio para operaciones CRUD de bibliotecarios. */
    private final IBibliotecarioRepository bibliotecarioRepository;

    /**
     * Constructor que inyecta todos los repositorios necesarios.
     *
     * @param libroRepository             Repositorio de libros.
     * @param usuarioRepository           Repositorio de usuarios.
     * @param prestamoRepository          Repositorio de prestamos.
     * @param autorRepository             Repositorio de autores.
     * @param bibliotecaEntidadRepository Repositorio de la biblioteca.
     * @param bibliotecarioRepository     Repositorio de bibliotecarios.
     */
    public BibliotecaService(ILibroRepository libroRepository,
                             IUsuarioRepository usuarioRepository,
                             IPrestamoRepository prestamoRepository,
                             IAutorRepository autorRepository,
                             IBibliotecaEntidadRepository bibliotecaEntidadRepository,
                             IBibliotecarioRepository bibliotecarioRepository) {
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
        this.autorRepository = autorRepository;
        this.bibliotecaEntidadRepository = bibliotecaEntidadRepository;
        this.bibliotecarioRepository = bibliotecarioRepository;
    }

    // ================== LIBROS ==================

    /**
     * Registra un libro con solo titulo y autor (sin editora ni fecha).
     * Lanza excepcion si ya existe un libro con el mismo ID.
     *
     * @param id     ID unico del libro.
     * @param titulo Titulo del libro.
     * @param autor  Nombre del autor.
     */
    @Override
    public void registrarLibro(int id, String titulo, String autor) {
        if (libroRepository.buscarPorId(id) != null) {
            throw new IllegalArgumentException("Ya existe un libro con ese ID.");
        }
        asegurarAutorRegistrado(autor);
        libroRepository.guardar(new Libro(id, titulo, autor));
    }

    /**
     * Registra un libro completo con editora y fecha de publicacion.
     * Lanza excepcion si ya existe un libro con el mismo ID.
     *
     * @param id               ID unico del libro.
     * @param titulo           Titulo del libro.
     * @param autor            Nombre del autor.
     * @param editora          Editorial del libro.
     * @param fechaPublicacion Fecha de publicacion.
     */
    @Override
    public void registrarLibro(int id, String titulo, String autor, String editora, LocalDate fechaPublicacion) {
        if (libroRepository.buscarPorId(id) != null) {
            throw new IllegalArgumentException("Ya existe un libro con ese ID.");
        }
        asegurarAutorRegistrado(autor);
        libroRepository.guardar(new Libro(id, titulo, autor, editora, fechaPublicacion));
    }

    /**
     * Garantiza que el autor de un libro exista en el modulo de autores.
     * Si no existe por nombre, se registra automaticamente con datos base.
     */
    private void asegurarAutorRegistrado(String nombreAutor) {
        if (nombreAutor == null || nombreAutor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor del libro no puede estar vacio.");
        }

        String nombreNormalizado = nombreAutor.trim();
        Autor existente = autorRepository.buscarPorNombre(nombreNormalizado);
        if (existente != null) {
            return;
        }

        int nuevoId = autorRepository.obtenerTodos().stream()
                .mapToInt(Autor::getId)
                .max()
                .orElse(0) + 1;

        autorRepository.guardar(new Autor(nuevoId, nombreNormalizado, "Desconocida"));
    }

    /**
     * Edita los datos de un libro existente.
     * Valida que el libro exista y que titulo y autor no esten vacios.
     *
     * @param id               ID del libro a editar.
     * @param titulo           Nuevo titulo.
     * @param autor            Nuevo autor.
     * @param editora          Nueva editorial.
     * @param fechaPublicacion Nueva fecha de publicacion.
     */
    @Override
    public void editarLibro(int id, String titulo, String autor, String editora, LocalDate fechaPublicacion) {
        Libro existente = libroRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un libro con ese ID.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El titulo del libro no puede estar vacio.");
        }
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor del libro no puede estar vacio.");
        }

        existente.setTitulo(titulo.trim());
        existente.setAutor(autor.trim());
        existente.setEditora(editora != null ? editora.trim() : null);
        existente.setFechaPublicacion(fechaPublicacion);

        libroRepository.actualizar(existente);
    }

    /**
     * Elimina un libro por ID.
     * No permite eliminar si tiene prestamos activos registrados.
     *
     * @param id ID del libro a eliminar.
     */
    @Override
    public void eliminarLibro(int id) {
        Libro existente = libroRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un libro con ese ID.");
        }

        // Verificar si el libro tiene prestamos activos antes de eliminar
        boolean tienePrestamos = prestamoRepository.obtenerTodos().stream()
                .anyMatch(prestamo -> prestamo.getLibroId() == id);
        if (tienePrestamos) {
            throw new IllegalArgumentException("No se puede eliminar el libro porque tiene prestamos registrados.");
        }

        boolean eliminado = libroRepository.eliminarPorId(id);
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el libro.");
        }
    }

    /**
     * Obtiene un libro por su posicion en la lista.
     *
     * @param index Indice del libro.
     * @return Objeto Libro en la posicion indicada.
     */
    @Override
    public Libro obtenerLibroPorIndice(int index) {
        return libroRepository.obtenerTodos().get(index);
    }

    /**
     * Retorna todos los libros registrados.
     *
     * @return Lista de libros.
     */
    @Override
    public List<Libro> getLibros() {
        return libroRepository.obtenerTodos();
    }

    // ================== USUARIOS ==================

    /**
     * Registra un usuario simplificado con solo id y nombre.
     *
     * @param id     ID unico del usuario.
     * @param nombre Nombre del usuario.
     */
    @Override
    public void registrarUsuario(int id, String nombre) {
        if (usuarioRepository.buscarPorId(id) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese ID.");
        }
        usuarioRepository.guardar(new Usuario(id, nombre));
    }

    /**
     * Registra un usuario completo con identificacion y tipo.
     *
     * @param id             ID unico del usuario.
     * @param nombre         Nombre completo.
     * @param identificacion Numero de identificacion.
     * @param tipo           Tipo de usuario.
     */
    @Override
    public void registrarUsuario(int id, String nombre, String identificacion, String tipo) {
        if (usuarioRepository.buscarPorId(id) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese ID.");
        }
        usuarioRepository.guardar(new Usuario(id, nombre, identificacion, tipo));
    }

    /**
     * Edita los datos de un usuario existente.
     * Valida que el usuario exista y que el nombre no este vacio.
     *
     * @param id             ID del usuario a editar.
     * @param nombre         Nuevo nombre.
     * @param identificacion Nueva identificacion.
     * @param tipo           Nuevo tipo.
     */
    @Override
    public void editarUsuario(int id, String nombre, String identificacion, String tipo) {
        Usuario existente = usuarioRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un usuario con ese ID.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacio.");
        }

        existente.setNombre(nombre.trim());
        existente.setIdentificacion(identificacion != null ? identificacion.trim() : null);
        existente.setTipo(tipo != null ? tipo.trim() : null);

        usuarioRepository.actualizar(existente);
    }

    /**
     * Elimina un usuario por ID.
     * No permite eliminar si tiene prestamos registrados.
     *
     * @param id ID del usuario a eliminar.
     */
    @Override
    public void eliminarUsuario(int id) {
        Usuario existente = usuarioRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un usuario con ese ID.");
        }

        // Verificar si el usuario tiene prestamos antes de eliminar
        boolean tienePrestamos = prestamoRepository.obtenerTodos().stream()
                .anyMatch(prestamo -> prestamo.getUsuarioId() == id);
        if (tienePrestamos) {
            throw new IllegalArgumentException("No se puede eliminar el usuario porque tiene prestamos registrados.");
        }

        boolean eliminado = usuarioRepository.eliminarPorId(id);
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el usuario.");
        }
    }

    /**
     * Obtiene un usuario por su posicion en la lista.
     *
     * @param index Indice del usuario.
     * @return Objeto Usuario en la posicion indicada.
     */
    @Override
    public Usuario obtenerUsuarioPorIndice(int index) {
        return usuarioRepository.obtenerTodos().get(index);
    }

    /**
     * Retorna todos los usuarios registrados.
     *
     * @return Lista de usuarios.
     */
    @Override
    public List<Usuario> getUsuarios() {
        return usuarioRepository.obtenerTodos();
    }

    // ================== PRESTAMOS ==================

    /**
     * Registra un nuevo prestamo.
     * Valida que libro, usuario y bibliotecario existan.
     * Valida que el libro este disponible.
     * Al registrar, marca el libro como no disponible y calcula 7 dias para devolucion.
     *
     * @param idPrestamo                 ID unico del prestamo.
     * @param idLibro                    ID del libro a prestar.
     * @param idUsuario                  ID del usuario que solicita el prestamo.
     * @param bibliotecaNombre           Nombre de la biblioteca (puede estar vacio).
     * @param bibliotecarioIdentificacion Identificacion del bibliotecario que gestiona el prestamo.
     */
    @Override
    public void registrarPrestamo(int idPrestamo, int idLibro, int idUsuario, String bibliotecaNombre, String bibliotecarioIdentificacion) {
        // Verificar que no exista ya un prestamo con ese ID
        boolean prestamoExistente = prestamoRepository.obtenerTodos().stream()
                .anyMatch(prestamo -> prestamo.getId() == idPrestamo);
        if (prestamoExistente) {
            throw new IllegalArgumentException("Ya existe un prestamo con ese ID.");
        }

        // Verificar que el libro exista y este disponible
        Libro libro = libroRepository.buscarPorId(idLibro);
        if (libro == null) {
            throw new IllegalArgumentException("No existe el libro indicado.");
        }
        if (!libro.isDisponible()) {
            throw new IllegalArgumentException("El libro no esta disponible.");
        }

        // Verificar que el usuario exista
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("No existe el usuario indicado.");
        }

        // Verificar que el bibliotecario exista
        if (bibliotecarioIdentificacion == null || bibliotecarioIdentificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el bibliotecario que realiza el prestamo.");
        }
        Bibliotecario bibliotecario = bibliotecarioRepository.buscarPorIdentificacion(bibliotecarioIdentificacion.trim());
        if (bibliotecario == null) {
            throw new IllegalArgumentException("No existe el bibliotecario indicado.");
        }

        // Obtener nombre de biblioteca: primero el ingresado, luego la activa
        String nombreBibliotecaFinal = (bibliotecaNombre != null && !bibliotecaNombre.trim().isEmpty())
                ? bibliotecaNombre.trim()
                : (bibliotecaEntidadRepository.obtenerActiva() != null ? bibliotecaEntidadRepository.obtenerActiva().getNombre() : "");

        // Crear el prestamo con 7 dias de devolucion por defecto
        Prestamo prestamo = new Prestamo(idPrestamo, libro, usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(7));
        prestamo.setBibliotecaNombre(nombreBibliotecaFinal);
        prestamo.setBibliotecarioIdentificacion(bibliotecario.getIdentificacion());

        // Marcar el libro como no disponible
        libro.setDisponible(false);

        prestamoRepository.guardar(prestamo);
    }

    /**
     * Edita los datos de un prestamo existente.
     * Valida que el prestamo, libro, usuario y bibliotecario existan.
     *
     * @param id                         ID del prestamo a editar.
     * @param libroId                    Nuevo ID de libro.
     * @param usuarioId                  Nuevo ID de usuario.
     * @param fechaPrestamo              Nueva fecha de prestamo.
     * @param fechaDevolucion            Nueva fecha de devolucion.
     * @param bibliotecaNombre           Nombre de la biblioteca.
     * @param bibliotecarioIdentificacion Identificacion del bibliotecario.
     */
    @Override
    public void editarPrestamo(int id, int libroId, int usuarioId, LocalDate fechaPrestamo, LocalDate fechaDevolucion,
                               String bibliotecaNombre, String bibliotecarioIdentificacion) {
        Prestamo existente = prestamoRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un prestamo con ese ID.");
        }

        Libro libro = libroRepository.buscarPorId(libroId);
        if (libro == null) {
            throw new IllegalArgumentException("No existe el libro indicado.");
        }

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("No existe el usuario indicado.");
        }

        if (bibliotecarioIdentificacion == null || bibliotecarioIdentificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el bibliotecario que realiza el prestamo.");
        }
        Bibliotecario bibliotecario = bibliotecarioRepository.buscarPorIdentificacion(bibliotecarioIdentificacion.trim());
        if (bibliotecario == null) {
            throw new IllegalArgumentException("No existe el bibliotecario indicado.");
        }

        String nombreBibliotecaFinal = (bibliotecaNombre != null && !bibliotecaNombre.trim().isEmpty())
                ? bibliotecaNombre.trim()
                : (bibliotecaEntidadRepository.obtenerActiva() != null ? bibliotecaEntidadRepository.obtenerActiva().getNombre() : "");

        // Actualizar todos los campos del prestamo existente
        existente.setLibroId(libroId);
        existente.setUsuarioId(usuarioId);
        existente.setFechaPrestamo(fechaPrestamo);
        existente.setFechaDevolucion(fechaDevolucion);
        existente.setLibro(libro);
        existente.setUsuario(usuario);
        existente.setBibliotecaNombre(nombreBibliotecaFinal);
        existente.setBibliotecarioIdentificacion(bibliotecario.getIdentificacion());

        prestamoRepository.actualizar(existente);
    }

    /**
     * Elimina un prestamo por ID.
     *
     * @param id ID del prestamo a eliminar.
     */
    @Override
    public void eliminarPrestamo(int id) {
        Prestamo existente = prestamoRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un prestamo con ese ID.");
        }

        boolean eliminado = prestamoRepository.eliminarPorId(id);
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el prestamo.");
        }
    }

    /**
     * Retorna todos los prestamos registrados.
     * Para cada prestamo, intenta cargar los objetos Libro y Usuario completos
     * si solo tiene IDs.
     *
     * @return Lista de prestamos con referencias a libro y usuario si estan disponibles.
     */
    @Override
    public List<Prestamo> getPrestamos() {
        List<Prestamo> prestamos = prestamoRepository.obtenerTodos();
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro() == null) {
                prestamo.setLibro(libroRepository.buscarPorId(prestamo.getLibroId()));
            }
            if (prestamo.getUsuario() == null) {
                prestamo.setUsuario(usuarioRepository.buscarPorId(prestamo.getUsuarioId()));
            }
        }
        return prestamos;
    }

    // ================== AUTORES ==================

    /**
     * Registra un autor sin anio de nacimiento.
     *
     * @param id           ID unico del autor.
     * @param nombre       Nombre del autor.
     * @param nacionalidad Nacionalidad del autor.
     */
    @Override
    public void registrarAutor(int id, String nombre, String nacionalidad) {
        if (autorRepository.buscarPorId(id) != null) {
            throw new IllegalArgumentException("Ya existe un autor con ese ID.");
        }
        autorRepository.guardar(new Autor(id, nombre, nacionalidad));
    }

    /**
     * Registra un autor completo con anio de nacimiento.
     *
     * @param id              ID unico del autor.
     * @param nombre          Nombre del autor.
     * @param nacionalidad    Nacionalidad del autor.
     * @param anioNacimiento  Anio de nacimiento.
     */
    @Override
    public void registrarAutor(int id, String nombre, String nacionalidad, int anioNacimiento) {
        if (autorRepository.buscarPorId(id) != null) {
            throw new IllegalArgumentException("Ya existe un autor con ese ID.");
        }
        autorRepository.guardar(new Autor(id, nombre, nacionalidad, anioNacimiento));
    }

    /**
     * Edita los datos de un autor existente.
     * Valida que nombre y nacionalidad no esten vacios.
     *
     * @param id              ID del autor a editar.
     * @param nombre          Nuevo nombre.
     * @param nacionalidad    Nueva nacionalidad.
     * @param anioNacimiento  Nuevo anio de nacimiento.
     */
    @Override
    public void editarAutor(int id, String nombre, String nacionalidad, int anioNacimiento) {
        Autor existente = autorRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un autor con ese ID.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del autor no puede estar vacio.");
        }
        if (nacionalidad == null || nacionalidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La nacionalidad del autor no puede estar vacia.");
        }

        existente.setNombre(nombre.trim());
        existente.setNacionalidad(nacionalidad.trim());
        existente.setAnioNacimiento(anioNacimiento);

        autorRepository.actualizar(existente);
    }

    /**
     * Elimina un autor por ID.
     *
     * @param id ID del autor a eliminar.
     */
    @Override
    public void eliminarAutor(int id) {
        Autor existente = autorRepository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No existe un autor con ese ID.");
        }

        boolean eliminado = autorRepository.eliminarPorId(id);
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el autor.");
        }
    }

    /**
     * Obtiene un autor por su posicion en la lista.
     *
     * @param index Indice del autor.
     * @return Objeto Autor en la posicion indicada.
     */
    @Override
    public Autor obtenerAutorPorIndice(int index) {
        return autorRepository.obtenerTodos().get(index);
    }

    /**
     * Retorna todos los autores registrados.
     *
     * @return Lista de autores.
     */
    @Override
    public List<Autor> getAutores() {
        return autorRepository.obtenerTodos();
    }

    // ================== BIBLIOTECA ==================

    /**
     * Crea o actualiza la biblioteca activa del sistema.
     * Solo puede existir una biblioteca activa a la vez.
     * Al crearla, asigna automaticamente la biblioteca a todos los bibliotecarios.
     *
     * @param nombre    Nombre de la biblioteca.
     * @param direccion Direccion fisica.
     * @param telefono  Telefono de contacto.
     * @param capacidad Capacidad maxima de libros.
     */
    @Override
    public void crearBiblioteca(String nombre, String direccion, String telefono, int capacidad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la biblioteca es obligatorio.");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        }

        Biblioteca biblioteca = new Biblioteca(nombre.trim(),
                direccion != null ? direccion.trim() : "",
                telefono != null ? telefono.trim() : "",
                capacidad);

        bibliotecaEntidadRepository.guardarOActualizar(biblioteca);
        // Asignar la biblioteca a todos los bibliotecarios registrados
        bibliotecarioRepository.asignarBibliotecaATodos(biblioteca.getNombre());
    }

    /**
     * Retorna la biblioteca activa registrada en el sistema.
     *
     * @return Objeto Biblioteca activo, o null si no hay ninguno.
     */
    @Override
    public Biblioteca getBiblioteca() {
        return bibliotecaEntidadRepository.obtenerActiva();
    }

    // ================== BIBLIOTECARIOS ==================

    /**
     * Registra un nuevo bibliotecario.
     * Si existe una biblioteca activa, la asigna automaticamente al bibliotecario.
     *
     * @param nombre         Nombre del bibliotecario.
     * @param identificacion Identificacion unica.
     * @param turno          Turno de trabajo.
     */
    @Override
    public void registrarBibliotecario(String nombre, String identificacion, String turno) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del bibliotecario es obligatorio.");
        }
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificacion del bibliotecario es obligatoria.");
        }
        if (bibliotecarioRepository.buscarPorIdentificacion(identificacion.trim()) != null) {
            throw new IllegalArgumentException("Ya existe un bibliotecario con esa identificacion.");
        }

        Bibliotecario bibliotecario = new Bibliotecario(nombre.trim(), identificacion.trim(),
                turno != null ? turno.trim() : "");

        // Asignar la biblioteca activa si existe
        Biblioteca biblioteca = bibliotecaEntidadRepository.obtenerActiva();
        if (biblioteca != null) {
            bibliotecario.setBibliotecaAsignada(biblioteca);
        }

        bibliotecarioRepository.guardar(bibliotecario);
    }

    /**
     * Edita el nombre y turno de un bibliotecario existente.
     *
     * @param identificacion Identificacion del bibliotecario a editar.
     * @param nombre         Nuevo nombre.
     * @param turno          Nuevo turno.
     */
    @Override
    public void editarBibliotecario(String identificacion, String nombre, String turno) {
        Bibliotecario existente = bibliotecarioRepository.buscarPorIdentificacion(identificacion.trim());
        if (existente == null) {
            throw new IllegalArgumentException("No existe un bibliotecario con esa identificacion.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del bibliotecario no puede estar vacio.");
        }

        existente.setNombre(nombre.trim());
        existente.setTurno(turno != null ? turno.trim() : "");

        bibliotecarioRepository.actualizar(existente);
    }

    /**
     * Elimina un bibliotecario por su identificacion.
     *
     * @param identificacion Identificacion unica del bibliotecario a eliminar.
     */
    @Override
    public void eliminarBibliotecario(String identificacion) {
        Bibliotecario existente = bibliotecarioRepository.buscarPorIdentificacion(identificacion.trim());
        if (existente == null) {
            throw new IllegalArgumentException("No existe un bibliotecario con esa identificacion.");
        }

        boolean eliminado = bibliotecarioRepository.eliminarPorIdentificacion(identificacion.trim());
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el bibliotecario.");
        }
    }

    /**
     * Retorna todos los bibliotecarios registrados.
     *
     * @return Lista de bibliotecarios.
     */
    @Override
    public List<Bibliotecario> getBibliotecarios() {
        return bibliotecarioRepository.obtenerTodos();
    }
}
