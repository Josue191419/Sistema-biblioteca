package org.example;

import org.example.controller.BibliotecaController;
import org.example.repository.AutorRepositoryMySQL;
import org.example.repository.BibliotecaEntidadRepositoryMySQL;
import org.example.repository.BibliotecarioRepositoryMySQL;
import org.example.repository.IAutorRepository;
import org.example.repository.IBibliotecaEntidadRepository;
import org.example.repository.IBibliotecarioRepository;
import org.example.repository.ILibroRepository;
import org.example.repository.IPrestamoRepository;
import org.example.repository.IUsuarioRepository;
import org.example.repository.LibroRepositoryMySQL;
import org.example.repository.PrestamoRepositoryMySQL;
import org.example.repository.UsuarioRepositoryMySQL;
import org.example.service.BibliotecaService;
import org.example.service.IBibliotecaService;
import org.example.view.BibliotecaView;
/**
 * Clase principal de entrada del sistema de gestion bibliotecaria.
 *
 * <p><b>Responsabilidades:</b></p>
 * <ul>
 *   <li>Crear la vista principal (BibliotecaView).</li>
 *   <li>Instanciar los repositorios MySQL para cada entidad.</li>
 *   <li>Construir el servicio (BibliotecaService) inyectando todos los repositorios.</li>
 *   <li>Crear el controlador principal y arrancar el sistema con iniciar().</li>
 * </ul>
 *
 * <p><b>Patron aplicado:</b> Inyeccion de dependencias manual (sin framework).</p>
 *
 * @author Josue
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // Inicializar la vista principal que contiene todas las sub-vistas
        BibliotecaView vista = new BibliotecaView();

        // Crear los repositorios MySQL para cada entidad del dominio
        ILibroRepository libroRepository = new LibroRepositoryMySQL();
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryMySQL();
        IPrestamoRepository prestamoRepository = new PrestamoRepositoryMySQL();
        IAutorRepository autorRepository = new AutorRepositoryMySQL();
        IBibliotecaEntidadRepository bibliotecaEntidadRepository = new BibliotecaEntidadRepositoryMySQL();
        IBibliotecarioRepository bibliotecarioRepository = new BibliotecarioRepositoryMySQL();

        // Crear el servicio inyectando todos los repositorios (patron inyeccion de dependencias)
        IBibliotecaService service = new BibliotecaService(
                libroRepository,
                usuarioRepository,
                prestamoRepository,
                autorRepository,
                bibliotecaEntidadRepository,
                bibliotecarioRepository
        );

        // Crear el controlador principal y arrancar el menu principal
        BibliotecaController controller = new BibliotecaController(vista, service);
        controller.iniciar();
    }
}