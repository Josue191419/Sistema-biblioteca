package org.example;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Biblioteca;
import org.example.model.Bibliotecario;
import org.example.model.Libro;
import org.example.model.Prestamo;
import org.example.model.Usuario;
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
import org.example.service.OpenLibraryService;

import java.time.LocalDate;
import java.util.List;

public class MainFx extends Application {

    private IBibliotecaService service;
    private final OpenLibraryService openLibraryService = new OpenLibraryService();

    private final TableView<Libro> tablaLibros = new TableView<>();
    private final TableView<Usuario> tablaUsuarios = new TableView<>();
    private final TableView<Prestamo> tablaPrestamos = new TableView<>();
    private final TableView<Bibliotecario> tablaBibliotecarios = new TableView<>();

    private final TextField libroId = new TextField();
    private final TextField libroTitulo = new TextField();
    private final TextField libroAutor = new TextField();
    private final TextField libroEditora = new TextField();
    private final DatePicker libroFecha = new DatePicker();

    private final TextField usuarioId = new TextField();
    private final TextField usuarioNombre = new TextField();
    private final TextField usuarioIdentificacion = new TextField();
    private final TextField usuarioTipo = new TextField();

    private final TextField prestamoId = new TextField();
    private final TextField prestamoLibroId = new TextField();
    private final TextField prestamoUsuarioId = new TextField();
    private final TextField prestamoBiblioteca = new TextField();
    private final TextField prestamoBibliotecario = new TextField();

    private final TextField bibliotecaNombre = new TextField();
    private final TextField bibliotecaDireccion = new TextField();
    private final TextField bibliotecaTelefono = new TextField();
    private final TextField bibliotecaCapacidad = new TextField();
    private final Label bibliotecaActivaInfo = new Label("Sin biblioteca activa");

    private final TextField bibliotecarioNombre = new TextField();
    private final TextField bibliotecarioIdentificacion = new TextField();
    private final TextField bibliotecarioTurno = new TextField();

    private final TextField apiBusqueda = new TextField();
    private final ListView<ApiLibroItem> apiResultados = new ListView<>();

    @Override
    public void start(Stage stage) {
        this.service = crearServicioMySql();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Label titulo = new Label("Sistema Biblioteca - Panel JavaFX");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Libros", construirModuloLibros()));
        tabs.getTabs().add(new Tab("Usuarios", construirModuloUsuarios()));
        tabs.getTabs().add(new Tab("Prestamos", construirModuloPrestamos()));
        tabs.getTabs().add(new Tab("Biblioteca", construirModuloBiblioteca()));
        tabs.getTabs().add(new Tab("Bibliotecarios", construirModuloBibliotecarios()));
        tabs.getTabs().forEach(tab -> tab.setClosable(false));

        root.setTop(titulo);
        BorderPane.setMargin(titulo, new Insets(0, 0, 10, 0));
        root.setCenter(tabs);

        refrescarLibros();
        refrescarUsuarios();
        refrescarPrestamos();
        refrescarBibliotecaActiva();
        refrescarBibliotecarios();

        stage.setScene(new Scene(root, 1100, 680));
        stage.setTitle("Biblioteca JavaFX");
        stage.show();
    }

    private VBox construirModuloLibros() {
        configurarTablaLibros();

        GridPane form = crearFormulario(5);
        form.addRow(0, new Label("ID"), libroId);
        form.addRow(1, new Label("Titulo"), libroTitulo);
        form.addRow(2, new Label("Autor"), libroAutor);
        form.addRow(3, new Label("Editora"), libroEditora);
        form.addRow(4, new Label("Fecha publicacion"), libroFecha);

        Button btnGuardar = new Button("Guardar");
        Button btnEditar = new Button("Editar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");
        Button btnRefrescar = new Button("Refrescar");

        btnGuardar.setOnAction(e -> guardarLibro());
        btnEditar.setOnAction(e -> editarLibro());
        btnEliminar.setOnAction(e -> eliminarLibro());
        btnLimpiar.setOnAction(e -> limpiarFormularioLibro());
        btnRefrescar.setOnAction(e -> refrescarLibros());

        HBox acciones = new HBox(10, btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnRefrescar);

        VBox panelForm = new VBox(10, form, acciones, construirPanelApiLibros());
        panelForm.setPadding(new Insets(0, 12, 0, 0));
        panelForm.setPrefWidth(420);

        tablaLibros.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, libro) -> {
            if (libro != null) {
                libroId.setText(String.valueOf(libro.getId()));
                libroTitulo.setText(libro.getTitulo());
                libroAutor.setText(libro.getAutor());
                libroEditora.setText(libro.getEditora() != null ? libro.getEditora() : "");
                libroFecha.setValue(libro.getFechaPublicacion());
            }
        });

        VBox contenedorTabla = new VBox(tablaLibros);
        VBox.setVgrow(tablaLibros, Priority.ALWAYS);

        HBox layout = new HBox(panelForm, contenedorTabla);
        HBox.setHgrow(contenedorTabla, Priority.ALWAYS);
        return new VBox(10, layout);
    }

    private VBox construirPanelApiLibros() {
        apiBusqueda.setPromptText("Buscar en Open Library");
        Button btnBuscar = new Button("Buscar API");
        Button btnUsar = new Button("Usar seleccionado");

        btnBuscar.setOnAction(e -> buscarEnApi());
        btnUsar.setOnAction(e -> usarResultadoApi());

        HBox top = new HBox(8, apiBusqueda, btnBuscar, btnUsar);
        HBox.setHgrow(apiBusqueda, Priority.ALWAYS);

        apiResultados.setPrefHeight(170);

        VBox panel = new VBox(8, new Label("Open Library"), top, apiResultados);
        panel.setPadding(new Insets(8));
        panel.setStyle("-fx-background-color: #f4f6f8; -fx-background-radius: 8;");
        return panel;
    }

    private VBox construirModuloUsuarios() {
        configurarTablaUsuarios();

        GridPane form = crearFormulario(4);
        form.addRow(0, new Label("ID"), usuarioId);
        form.addRow(1, new Label("Nombre"), usuarioNombre);
        form.addRow(2, new Label("Identificacion"), usuarioIdentificacion);
        form.addRow(3, new Label("Tipo"), usuarioTipo);

        Button btnGuardar = new Button("Guardar");
        Button btnEditar = new Button("Editar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");
        Button btnRefrescar = new Button("Refrescar");

        btnGuardar.setOnAction(e -> guardarUsuario());
        btnEditar.setOnAction(e -> editarUsuario());
        btnEliminar.setOnAction(e -> eliminarUsuario());
        btnLimpiar.setOnAction(e -> limpiarFormularioUsuario());
        btnRefrescar.setOnAction(e -> refrescarUsuarios());

        HBox acciones = new HBox(10, btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnRefrescar);

        VBox panelForm = new VBox(10, form, acciones);
        panelForm.setPadding(new Insets(0, 12, 0, 0));
        panelForm.setPrefWidth(420);

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, usuario) -> {
            if (usuario != null) {
                usuarioId.setText(String.valueOf(usuario.getId()));
                usuarioNombre.setText(usuario.getNombre());
                usuarioIdentificacion.setText(usuario.getIdentificacion() != null ? usuario.getIdentificacion() : "");
                usuarioTipo.setText(usuario.getTipo() != null ? usuario.getTipo() : "");
            }
        });

        VBox contenedorTabla = new VBox(tablaUsuarios);
        VBox.setVgrow(tablaUsuarios, Priority.ALWAYS);

        HBox layout = new HBox(panelForm, contenedorTabla);
        HBox.setHgrow(contenedorTabla, Priority.ALWAYS);
        return new VBox(10, layout);
    }

    private VBox construirModuloPrestamos() {
        configurarTablaPrestamos();

        GridPane form = crearFormulario(5);
        form.addRow(0, new Label("ID prestamo"), prestamoId);
        form.addRow(1, new Label("ID libro"), prestamoLibroId);
        form.addRow(2, new Label("ID usuario"), prestamoUsuarioId);
        form.addRow(3, new Label("Biblioteca"), prestamoBiblioteca);
        form.addRow(4, new Label("ID bibliotecario"), prestamoBibliotecario);

        Button btnGuardar = new Button("Guardar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");
        Button btnRefrescar = new Button("Refrescar");

        btnGuardar.setOnAction(e -> guardarPrestamo());
        btnEliminar.setOnAction(e -> eliminarPrestamo());
        btnLimpiar.setOnAction(e -> limpiarFormularioPrestamo());
        btnRefrescar.setOnAction(e -> refrescarPrestamos());

        HBox acciones = new HBox(10, btnGuardar, btnEliminar, btnLimpiar, btnRefrescar);

        VBox panelForm = new VBox(10, form, acciones);
        panelForm.setPadding(new Insets(0, 12, 0, 0));
        panelForm.setPrefWidth(420);

        tablaPrestamos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, p) -> {
            if (p != null) {
                prestamoId.setText(String.valueOf(p.getId()));
                prestamoLibroId.setText(String.valueOf(p.getLibroId()));
                prestamoUsuarioId.setText(String.valueOf(p.getUsuarioId()));
                prestamoBiblioteca.setText(p.getBibliotecaNombre() != null ? p.getBibliotecaNombre() : "");
                prestamoBibliotecario.setText(p.getBibliotecarioIdentificacion() != null ? p.getBibliotecarioIdentificacion() : "");
            }
        });

        VBox contenedorTabla = new VBox(tablaPrestamos);
        VBox.setVgrow(tablaPrestamos, Priority.ALWAYS);

        HBox layout = new HBox(panelForm, contenedorTabla);
        HBox.setHgrow(contenedorTabla, Priority.ALWAYS);
        return new VBox(10, layout);
    }

    private VBox construirModuloBiblioteca() {
        GridPane form = crearFormulario(4);
        form.addRow(0, new Label("Nombre"), bibliotecaNombre);
        form.addRow(1, new Label("Direccion"), bibliotecaDireccion);
        form.addRow(2, new Label("Telefono"), bibliotecaTelefono);
        form.addRow(3, new Label("Capacidad"), bibliotecaCapacidad);

        Button btnGuardar = new Button("Guardar / Actualizar");
        Button btnCargar = new Button("Cargar activa");
        Button btnLimpiar = new Button("Limpiar");

        btnGuardar.setOnAction(e -> guardarBiblioteca());
        btnCargar.setOnAction(e -> cargarBibliotecaActivaEnFormulario());
        btnLimpiar.setOnAction(e -> limpiarFormularioBiblioteca());

        HBox acciones = new HBox(10, btnGuardar, btnCargar, btnLimpiar);

        VBox panelInfo = new VBox(6,
                new Label("Biblioteca activa"),
                bibliotecaActivaInfo
        );
        panelInfo.setPadding(new Insets(8));
        panelInfo.setStyle("-fx-background-color: #f4f6f8; -fx-background-radius: 8;");

        return new VBox(10, form, acciones, panelInfo);
    }

    private VBox construirModuloBibliotecarios() {
        configurarTablaBibliotecarios();

        GridPane form = crearFormulario(3);
        form.addRow(0, new Label("Nombre"), bibliotecarioNombre);
        form.addRow(1, new Label("Identificacion"), bibliotecarioIdentificacion);
        form.addRow(2, new Label("Turno"), bibliotecarioTurno);

        Button btnGuardar = new Button("Guardar");
        Button btnEditar = new Button("Editar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");
        Button btnRefrescar = new Button("Refrescar");

        btnGuardar.setOnAction(e -> guardarBibliotecario());
        btnEditar.setOnAction(e -> editarBibliotecario());
        btnEliminar.setOnAction(e -> eliminarBibliotecario());
        btnLimpiar.setOnAction(e -> limpiarFormularioBibliotecario());
        btnRefrescar.setOnAction(e -> refrescarBibliotecarios());

        HBox acciones = new HBox(10, btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnRefrescar);

        VBox panelForm = new VBox(10, form, acciones);
        panelForm.setPadding(new Insets(0, 12, 0, 0));
        panelForm.setPrefWidth(420);

        tablaBibliotecarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, b) -> {
            if (b != null) {
                bibliotecarioNombre.setText(b.getNombre());
                bibliotecarioIdentificacion.setText(b.getIdentificacion());
                bibliotecarioTurno.setText(b.getTurno() != null ? b.getTurno() : "");
            }
        });

        VBox contenedorTabla = new VBox(tablaBibliotecarios);
        VBox.setVgrow(tablaBibliotecarios, Priority.ALWAYS);

        HBox layout = new HBox(panelForm, contenedorTabla);
        HBox.setHgrow(contenedorTabla, Priority.ALWAYS);
        return new VBox(10, layout);
    }

    private void configurarTablaLibros() {
        tablaLibros.getColumns().clear();

        TableColumn<Libro, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Libro, String> cTitulo = new TableColumn<>("Titulo");
        cTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        TableColumn<Libro, String> cAutor = new TableColumn<>("Autor");
        cAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));

        TableColumn<Libro, String> cEditora = new TableColumn<>("Editora");
        cEditora.setCellValueFactory(new PropertyValueFactory<>("editora"));

        TableColumn<Libro, String> cFecha = new TableColumn<>("Fecha");
        cFecha.setCellValueFactory(data -> {
            LocalDate fecha = data.getValue().getFechaPublicacion();
            return new SimpleStringProperty(fecha != null ? fecha.toString() : "");
        });

        TableColumn<Libro, Boolean> cDisp = new TableColumn<>("Disponible");
        cDisp.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isDisponible()));

        tablaLibros.getColumns().addAll(cId, cTitulo, cAutor, cEditora, cFecha, cDisp);
        tablaLibros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void configurarTablaUsuarios() {
        tablaUsuarios.getColumns().clear();

        TableColumn<Usuario, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Usuario, String> cNombre = new TableColumn<>("Nombre");
        cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> cIdent = new TableColumn<>("Identificacion");
        cIdent.setCellValueFactory(new PropertyValueFactory<>("identificacion"));

        TableColumn<Usuario, String> cTipo = new TableColumn<>("Tipo");
        cTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        tablaUsuarios.getColumns().addAll(cId, cNombre, cIdent, cTipo);
        tablaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void configurarTablaPrestamos() {
        tablaPrestamos.getColumns().clear();

        TableColumn<Prestamo, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<Prestamo, Integer> cLibro = new TableColumn<>("Libro ID");
        cLibro.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLibroId()).asObject());

        TableColumn<Prestamo, Integer> cUsuario = new TableColumn<>("Usuario ID");
        cUsuario.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getUsuarioId()).asObject());

        TableColumn<Prestamo, String> cFechaP = new TableColumn<>("Prestamo");
        cFechaP.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaPrestamo().toString()));

        TableColumn<Prestamo, String> cFechaD = new TableColumn<>("Devolucion");
        cFechaD.setCellValueFactory(data -> {
            LocalDate fecha = data.getValue().getFechaDevolucion();
            return new SimpleStringProperty(fecha != null ? fecha.toString() : "");
        });

        TableColumn<Prestamo, String> cBiblioteca = new TableColumn<>("Biblioteca");
        cBiblioteca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBibliotecaNombre()));

        TableColumn<Prestamo, String> cBibliotecario = new TableColumn<>("Bibliotecario");
        cBibliotecario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBibliotecarioIdentificacion()));

        tablaPrestamos.getColumns().addAll(cId, cLibro, cUsuario, cFechaP, cFechaD, cBiblioteca, cBibliotecario);
        tablaPrestamos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void configurarTablaBibliotecarios() {
        tablaBibliotecarios.getColumns().clear();

        TableColumn<Bibliotecario, String> cNombre = new TableColumn<>("Nombre");
        cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Bibliotecario, String> cIdentificacion = new TableColumn<>("Identificacion");
        cIdentificacion.setCellValueFactory(new PropertyValueFactory<>("identificacion"));

        TableColumn<Bibliotecario, String> cTurno = new TableColumn<>("Turno");
        cTurno.setCellValueFactory(new PropertyValueFactory<>("turno"));

        TableColumn<Bibliotecario, String> cBiblioteca = new TableColumn<>("Biblioteca");
        cBiblioteca.setCellValueFactory(data -> {
            Biblioteca biblioteca = data.getValue().getBibliotecaAsignada();
            return new SimpleStringProperty(biblioteca != null ? biblioteca.getNombre() : "");
        });

        tablaBibliotecarios.getColumns().addAll(cNombre, cIdentificacion, cTurno, cBiblioteca);
        tablaBibliotecarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private GridPane crearFormulario(int filas) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(8));
        grid.setStyle("-fx-background-color: #f8f9fb; -fx-background-radius: 8;");
        for (int i = 0; i < filas; i++) {
            grid.getRowConstraints().add(new javafx.scene.layout.RowConstraints(34));
        }
        return grid;
    }

    private void guardarLibro() {
        try {
            int id = parseEntero(libroId.getText(), "ID libro");
            String titulo = libroTitulo.getText();
            String autor = libroAutor.getText();
            String editora = libroEditora.getText();
            LocalDate fecha = libroFecha.getValue();

            if (fecha == null && (editora == null || editora.isBlank())) {
                service.registrarLibro(id, titulo, autor);
            } else {
                service.registrarLibro(id, titulo, autor, editora, fecha);
            }
            refrescarLibros();
            limpiarFormularioLibro();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void editarLibro() {
        try {
            service.editarLibro(
                    parseEntero(libroId.getText(), "ID libro"),
                    libroTitulo.getText(),
                    libroAutor.getText(),
                    libroEditora.getText(),
                    libroFecha.getValue()
            );
            refrescarLibros();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarLibro() {
        try {
            service.eliminarLibro(parseEntero(libroId.getText(), "ID libro"));
            refrescarLibros();
            limpiarFormularioLibro();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void guardarUsuario() {
        try {
            service.registrarUsuario(
                    parseEntero(usuarioId.getText(), "ID usuario"),
                    usuarioNombre.getText(),
                    usuarioIdentificacion.getText(),
                    usuarioTipo.getText()
            );
            refrescarUsuarios();
            limpiarFormularioUsuario();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void editarUsuario() {
        try {
            service.editarUsuario(
                    parseEntero(usuarioId.getText(), "ID usuario"),
                    usuarioNombre.getText(),
                    usuarioIdentificacion.getText(),
                    usuarioTipo.getText()
            );
            refrescarUsuarios();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarUsuario() {
        try {
            service.eliminarUsuario(parseEntero(usuarioId.getText(), "ID usuario"));
            refrescarUsuarios();
            limpiarFormularioUsuario();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void guardarPrestamo() {
        try {
            service.registrarPrestamo(
                    parseEntero(prestamoId.getText(), "ID prestamo"),
                    parseEntero(prestamoLibroId.getText(), "ID libro"),
                    parseEntero(prestamoUsuarioId.getText(), "ID usuario"),
                    prestamoBiblioteca.getText(),
                    prestamoBibliotecario.getText()
            );
            refrescarPrestamos();
            refrescarLibros();
            limpiarFormularioPrestamo();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarPrestamo() {
        try {
            service.eliminarPrestamo(parseEntero(prestamoId.getText(), "ID prestamo"));
            refrescarPrestamos();
            limpiarFormularioPrestamo();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void guardarBiblioteca() {
        try {
            service.crearBiblioteca(
                    bibliotecaNombre.getText(),
                    bibliotecaDireccion.getText(),
                    bibliotecaTelefono.getText(),
                    parseEntero(bibliotecaCapacidad.getText(), "Capacidad biblioteca")
            );
            refrescarBibliotecaActiva();
            refrescarBibliotecarios();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarBibliotecaActivaEnFormulario() {
        Biblioteca activa = service.getBiblioteca();
        if (activa == null) {
            mostrarError("No hay biblioteca activa registrada.");
            return;
        }
        bibliotecaNombre.setText(activa.getNombre());
        bibliotecaDireccion.setText(activa.getDireccion());
        bibliotecaTelefono.setText(activa.getTelefono());
        bibliotecaCapacidad.setText(String.valueOf(activa.getCapacidad()));
    }

    private void guardarBibliotecario() {
        try {
            service.registrarBibliotecario(
                    bibliotecarioNombre.getText(),
                    bibliotecarioIdentificacion.getText(),
                    bibliotecarioTurno.getText()
            );
            refrescarBibliotecarios();
            limpiarFormularioBibliotecario();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void editarBibliotecario() {
        try {
            service.editarBibliotecario(
                    bibliotecarioIdentificacion.getText(),
                    bibliotecarioNombre.getText(),
                    bibliotecarioTurno.getText()
            );
            refrescarBibliotecarios();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarBibliotecario() {
        try {
            service.eliminarBibliotecario(bibliotecarioIdentificacion.getText());
            refrescarBibliotecarios();
            limpiarFormularioBibliotecario();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void buscarEnApi() {
        try {
            String criterio = apiBusqueda.getText();
            List<Libro> encontrados = openLibraryService.buscarLibros(criterio);
            apiResultados.setItems(FXCollections.observableArrayList(
                    encontrados.stream().map(ApiLibroItem::new).toList()
            ));
        } catch (Exception ex) {
            mostrarError("Error consultando API: " + ex.getMessage());
        }
    }

    private void usarResultadoApi() {
        ApiLibroItem item = apiResultados.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        libroTitulo.setText(item.libro.getTitulo());
        libroAutor.setText(item.libro.getAutor());
        libroEditora.setText(item.libro.getEditora());
        libroFecha.setValue(item.libro.getFechaPublicacion());
    }

    private void refrescarLibros() {
        tablaLibros.setItems(FXCollections.observableArrayList(service.getLibros()));
    }

    private void refrescarUsuarios() {
        tablaUsuarios.setItems(FXCollections.observableArrayList(service.getUsuarios()));
    }

    private void refrescarPrestamos() {
        tablaPrestamos.setItems(FXCollections.observableArrayList(service.getPrestamos()));
    }

    private void refrescarBibliotecaActiva() {
        Biblioteca activa = service.getBiblioteca();
        if (activa == null) {
            bibliotecaActivaInfo.setText("Sin biblioteca activa");
            return;
        }
        bibliotecaActivaInfo.setText(
                "Nombre: " + activa.getNombre() +
                        " | Direccion: " + activa.getDireccion() +
                        " | Telefono: " + activa.getTelefono() +
                        " | Capacidad: " + activa.getCapacidad()
        );
    }

    private void refrescarBibliotecarios() {
        tablaBibliotecarios.setItems(FXCollections.observableArrayList(service.getBibliotecarios()));
    }

    private void limpiarFormularioLibro() {
        libroId.clear();
        libroTitulo.clear();
        libroAutor.clear();
        libroEditora.clear();
        libroFecha.setValue(null);
    }

    private void limpiarFormularioUsuario() {
        usuarioId.clear();
        usuarioNombre.clear();
        usuarioIdentificacion.clear();
        usuarioTipo.clear();
    }

    private void limpiarFormularioPrestamo() {
        prestamoId.clear();
        prestamoLibroId.clear();
        prestamoUsuarioId.clear();
        prestamoBiblioteca.clear();
        prestamoBibliotecario.clear();
    }

    private void limpiarFormularioBiblioteca() {
        bibliotecaNombre.clear();
        bibliotecaDireccion.clear();
        bibliotecaTelefono.clear();
        bibliotecaCapacidad.clear();
    }

    private void limpiarFormularioBibliotecario() {
        bibliotecarioNombre.clear();
        bibliotecarioIdentificacion.clear();
        bibliotecarioTurno.clear();
    }

    private int parseEntero(String valor, String campo) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Valor invalido para " + campo + ".");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Operacion no completada");
        alert.setContentText(mensaje != null ? mensaje : "Error inesperado");
        alert.showAndWait();
    }

    private IBibliotecaService crearServicioMySql() {
        ILibroRepository libroRepository = new LibroRepositoryMySQL();
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryMySQL();
        IPrestamoRepository prestamoRepository = new PrestamoRepositoryMySQL();
        IAutorRepository autorRepository = new AutorRepositoryMySQL();
        IBibliotecaEntidadRepository bibliotecaEntidadRepository = new BibliotecaEntidadRepositoryMySQL();
        IBibliotecarioRepository bibliotecarioRepository = new BibliotecarioRepositoryMySQL();

        return new BibliotecaService(
                libroRepository,
                usuarioRepository,
                prestamoRepository,
                autorRepository,
                bibliotecaEntidadRepository,
                bibliotecarioRepository
        );
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final class ApiLibroItem {
        private final Libro libro;

        private ApiLibroItem(Libro libro) {
            this.libro = libro;
        }

        @Override
        public String toString() {
            String fecha = libro.getFechaPublicacion() != null ? libro.getFechaPublicacion().toString() : "N/A";
            return libro.getTitulo() + " | " + libro.getAutor() + " | " + fecha;
        }
    }
}
