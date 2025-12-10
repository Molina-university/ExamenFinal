package com.agrosense.ui;

import com.agrosense.model.*;
import com.agrosense.service.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class AgroSenseFX extends Application {

    // Services
    private GestorLotes gestorLotes = new GestorLotes();
    private SensorService sensorService = new SensorService();
    private AlertaService alertaService = new AlertaService();
    private RecomendacionService recomendacionService = new RecomendacionService();
    private ToonPersistenceService toonService = new ToonPersistenceService();

    // UI Components
    private TableView<Lote> tableLotes;
    private TableView<MedicionViewModel> tableMonitoreo;
    private TableView<Alerta> tableAlertas;
    private TableView<SensorViewModel> tableSensores;
    private TextArea txtRecomendaciones;
    private ComboBox<String> comboLotesSensor;

    // Colors - Modern Premium Palette
    private static final String PRIMARY_COLOR = "#2E7D32";
    private static final String PRIMARY_DARK = "#1B5E20";
    private static final String ACCENT_COLOR = "#4CAF50";
    private static final String ACCENT_LIGHT = "#66BB6A";
    private static final String BG_GRADIENT = "linear-gradient(to bottom right, #E8F5E9, #F1F8F4, #E3F2FD)";
    private static final String SUCCESS_COLOR = "#4CAF50";
    private static final String WARNING_COLOR = "#FF9800";
    private static final String DANGER_COLOR = "#F44336";
    private static final String INFO_COLOR = "#2196F3";

    private boolean isReady = false; // Flag to prevent saves during initialization

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Auto-load data BEFORE creating UI (don't update tables yet)
        cargarDatosAutomaticamente();

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        root.setStyle("-fx-background-color: " + BG_GRADIENT + ";");

        // Header
        root.setTop(createHeader());

        // Tabs
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        tabPane.getTabs().addAll(
                createTab("🌱 Gestión de Lotes", createLotesView()),
                createTab("📡 Sensores", createSensoresView()),
                createTab("📊 Monitoreo", createMonitoreoView()),
                createTab("⚠️ Alertas", createAlertasView()));

        root.setCenter(tabPane);

        // NOW update tables with loaded data (tables exist now)
        actualizarTablaLotes();
        actualizarTablaSensores(); // Cargar sensores al inicio
        actualizarCombos();
        actualizarAlertas();

        // IMPORTANT: Enable auto-save ONLY after everything is loaded
        isReady = true;
        System.out.println("[INFO] Aplicación lista. Auto-guardado activado.");

        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setTitle("AgroSense - Sistema de Monitoreo Agrícola");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        guardarDatosAutomaticamente();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #2E7D32, #1B5E20, #004D40); " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);");
        header.setSpacing(20);

        VBox titleBox = new VBox(5);
        Label title = new Label("🌿 AgroSense");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 4, 0, 0, 2);");

        Label subtitle = new Label("Sistema Inteligente de Monitoreo Agrícola");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        subtitle.setTextFill(Color.web("#A5D6A7"));
        subtitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer);
        return header;
    }

    private Tab createTab(String title, javafx.scene.Node content) {
        Tab tab = new Tab(title);
        tab.setContent(content);
        tab.setClosable(false);
        return tab;
    }

    // --- Lotes View ---
    private HBox createLotesView() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));

        // Form (Left Side)
        VBox formCard = createCard("➕ Registrar Nuevo Lote");
        formCard.setPrefWidth(400);
        formCard.setMaxWidth(450);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        TextField txtId = new TextField();
        TextField txtNombre = new TextField();
        TextField txtCultivo = new TextField();
        TextField txtArea = new TextField();

        addFormField(grid, "ID del Lote:", txtId, 0);
        addFormField(grid, "Nombre:", txtNombre, 1);
        addFormField(grid, "Tipo de Cultivo:", txtCultivo, 2);
        addFormField(grid, "Área (ha):", txtArea, 3);

        Button btnRegistrar = createStyledButton("Registrar Lote");
        btnRegistrar.setId("btnRegistrar");
        btnRegistrar.getStyleClass().add("btn-registrar");
        btnRegistrar.setOnAction(e -> {
            try {
                String id = txtId.getText();
                String nombre = txtNombre.getText();
                String cultivo = txtCultivo.getText();
                double area = Double.parseDouble(txtArea.getText());

                if (id.isEmpty() || nombre.isEmpty()) {
                    showAlert("Error", "ID y Nombre son obligatorios", Alert.AlertType.ERROR);
                    return;
                }

                gestorLotes.registrarLote(new Lote(id, nombre, cultivo, area));
                guardarDatosAutomaticamente();
                actualizarTablaLotes();
                actualizarCombos();
                txtId.clear();
                txtNombre.clear();
                txtCultivo.clear();
                txtArea.clear();
                showAlert("Éxito", "Lote registrado correctamente", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException ex) {
                showAlert("Error", "El área debe ser un número válido", Alert.AlertType.ERROR);
            }
        });

        grid.add(btnRegistrar, 1, 4);
        formCard.getChildren().add(grid);

        // Table (Right Side)
        VBox tableCard = createCard("📋 Lotes Registrados");
        tableLotes = new TableView<>();

        TableColumn<Lote, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(100);
        colId.setMinWidth(80);

        TableColumn<Lote, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(200);
        colNombre.setMinWidth(150);

        TableColumn<Lote, String> colCultivo = new TableColumn<>("Cultivo");
        colCultivo.setCellValueFactory(new PropertyValueFactory<>("tipoCultivo"));
        colCultivo.setPrefWidth(150);
        colCultivo.setMinWidth(120);

        TableColumn<Lote, Double> colArea = new TableColumn<>("Área (ha)");
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colArea.setPrefWidth(180);
        colArea.setMinWidth(100);

        // Columna de Acciones
        TableColumn<Lote, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(240);
        colAcciones.setMinWidth(240);
        colAcciones.setCellFactory(col -> {
            return new TableCell<Lote, Void>() {
                private final Button btnEditar = new Button("Editar");
                private final Button btnEliminar = new Button("Eliminar");
                private final HBox pane = new HBox(10, btnEditar, btnEliminar);

                {
                    btnEditar.setId("btnEditar");
                    btnEliminar.setId("btnEliminar");
                    btnEditar.setStyle(
                            "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 100px; -fx-pref-width: 100px; -fx-padding: 8 16;");
                    btnEliminar.setStyle(
                            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 100px; -fx-pref-width: 100px; -fx-padding: 8 16;");
                    pane.setAlignment(Pos.CENTER);

                    btnEditar.setOnAction(event -> {
                        Lote lote = getTableView().getItems().get(getIndex());
                        editarLote(lote);
                    });

                    btnEliminar.setOnAction(event -> {
                        Lote lote = getTableView().getItems().get(getIndex());
                        eliminarLote(lote);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
                }
            };
        });

        tableLotes.getColumns().addAll(colId, colNombre, colCultivo, colArea, colAcciones);
        // Use UNCONSTRAINED to show full text
        // Use CONSTRAINED to adapt to screen size
        tableLotes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Disable editing and column reordering to prevent errors
        tableLotes.setEditable(false);
        tableLotes.getColumns().forEach(col -> col.setReorderable(false));
        // Add CSS class for individual styling
        tableLotes.getStyleClass().add("tabla-lotes");

        tableCard.getChildren().add(tableLotes);
        VBox.setVgrow(tableLotes, Priority.ALWAYS);
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        layout.getChildren().addAll(formCard, tableCard);
        return layout;
    }

    private void editarLote(Lote lote) {
        Dialog<Lote> dialog = new Dialog<>();
        dialog.setTitle("Editar Lote");
        dialog.setHeaderText("Modificar datos del lote: " + lote.getId());

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField(lote.getNombre());
        TextField txtCultivo = new TextField(lote.getTipoCultivo());
        TextField txtArea = new TextField(String.valueOf(lote.getArea()));

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Cultivo:"), 0, 1);
        grid.add(txtCultivo, 1, 1);
        grid.add(new Label("Área (ha):"), 0, 2);
        grid.add(txtArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                try {
                    // Remover y crear nuevo lote con datos actualizados
                    gestorLotes.eliminarLote(lote.getId());
                    Lote loteActualizado = new Lote(lote.getId(), txtNombre.getText(),
                            txtCultivo.getText(), Double.parseDouble(txtArea.getText()));
                    // Restaurar sensores
                    for (Sensor s : lote.getSensores()) {
                        loteActualizado.agregarSensor(s);
                    }
                    gestorLotes.registrarLote(loteActualizado);
                    return loteActualizado;
                } catch (NumberFormatException e) {
                    showAlert("Error", "El área debe ser un número válido", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                guardarDatosAutomaticamente();
                actualizarTablaLotes();
                actualizarCombos();
                showAlert("Éxito", "Lote actualizado correctamente", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void eliminarLote(Lote lote) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar el lote '" + lote.getNombre() + "'?\n" +
                        "Esto eliminará también todos sus sensores.",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                gestorLotes.eliminarLote(lote.getId());
                guardarDatosAutomaticamente();
                actualizarTablaLotes();
                actualizarCombos();
                showAlert("Éxito", "Lote eliminado correctamente", Alert.AlertType.INFORMATION);
            }
        });
    }

    // --- Sensores View ---
    private HBox createSensoresView() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));

        // 1. Formulario (Left Side)
        VBox formCard = createCard("📡 Gestión de Sensores");
        formCard.setPrefWidth(400);
        formCard.setMaxWidth(450);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        comboLotesSensor = new ComboBox<>();
        comboLotesSensor.setPromptText("Seleccionar Lote para Agregar");
        comboLotesSensor.setMaxWidth(Double.MAX_VALUE);

        TextField txtId = new TextField();
        ComboBox<String> comboTipo = new ComboBox<>(FXCollections.observableArrayList("HUMEDAD", "TEMPERATURA"));
        comboTipo.getSelectionModel().selectFirst();
        comboTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtUbicacion = new TextField();

        addFormField(grid, "Lote:", comboLotesSensor, 0);
        addFormField(grid, "ID Sensor:", txtId, 1);
        addFormField(grid, "Tipo:", comboTipo, 2);
        addFormField(grid, "Ubicación:", txtUbicacion, 3);

        Button btnAgregar = createStyledButton("Agregar Sensor");
        btnAgregar.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold;");

        grid.add(btnAgregar, 1, 4);

        formCard.getChildren().add(grid);

        // 2. Tabla de Sensores (Right Side)
        VBox tableCard = createCard("📋 Lista de Sensores");
        tableSensores = new TableView<>();

        TableColumn<SensorViewModel, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(new PropertyValueFactory<>("loteNombre"));
        colLote.setPrefWidth(200);
        colLote.setMinWidth(150);

        TableColumn<SensorViewModel, String> colId = new TableColumn<>("ID Sensor");
        colId.setCellValueFactory(new PropertyValueFactory<>("sensorId"));
        colId.setPrefWidth(130);
        colId.setMinWidth(100);

        TableColumn<SensorViewModel, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setPrefWidth(150);
        colTipo.setMinWidth(120);

        TableColumn<SensorViewModel, String> colUbicacion = new TableColumn<>("Ubicación");
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        colUbicacion.setPrefWidth(200);
        colUbicacion.setMinWidth(180);

        // Columna de Acciones
        TableColumn<SensorViewModel, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(240);
        colAcciones.setMinWidth(240);
        colAcciones.setCellFactory(col -> {
            return new TableCell<SensorViewModel, Void>() {
                private final Button btnEditar = new Button("Editar");
                private final Button btnEliminar = new Button("Eliminar");
                private final HBox pane = new HBox(10, btnEditar, btnEliminar);

                {
                    btnEditar.setId("btnEditarSensor");
                    btnEliminar.setId("btnEliminarSensor");
                    btnEditar.setStyle(
                            "-fx-background-color: #18c838ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 100px; -fx-pref-width: 100px; -fx-padding: 8 16;");
                    btnEliminar.setStyle(
                            "-fx-background-color: #272424ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 100px; -fx-pref-width: 100px; -fx-padding: 8 16;");
                    pane.setAlignment(Pos.CENTER);

                    btnEditar.setOnAction(event -> {
                        SensorViewModel svm = getTableView().getItems().get(getIndex());
                        editarSensor(svm.getSensor(), svm.getLoteId());
                    });

                    btnEliminar.setOnAction(event -> {
                        SensorViewModel svm = getTableView().getItems().get(getIndex());
                        eliminarSensor(svm.getSensor(), svm.getLoteId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
                }
            };
        });

        tableSensores.getColumns().addAll(colLote, colId, colTipo, colUbicacion, colAcciones);
        // Use UNCONSTRAINED to show full text
        // Use CONSTRAINED to adapt to screen size
        tableSensores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Disable editing and column reordering to prevent errors
        tableSensores.setEditable(false);
        tableSensores.getColumns().forEach(col -> col.setReorderable(false));
        // Add CSS class for individual styling
        tableSensores.getStyleClass().add("tabla-sensores");

        tableCard.getChildren().add(tableSensores);
        VBox.setVgrow(tableSensores, Priority.ALWAYS);
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        // Lógica de Botones
        btnAgregar.setOnAction(e -> {
            String selectedLote = comboLotesSensor.getValue();
            if (selectedLote == null) {
                showAlert("Error", "Seleccione un lote", Alert.AlertType.ERROR);
                return;
            }
            if (txtId.getText().isEmpty() || txtUbicacion.getText().isEmpty()) {
                showAlert("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
                return;
            }

            String loteId = selectedLote.split(" - ")[0];
            var loteOpt = gestorLotes.buscarPorId(loteId);

            if (loteOpt.isPresent()) {
                Lote lote = loteOpt.get();
                // Verificar si ya existe
                boolean existe = lote.getSensores().stream().anyMatch(s -> s.getId().equals(txtId.getText()));
                if (existe) {
                    showAlert("Error", "Ya existe un sensor con este ID en el lote", Alert.AlertType.ERROR);
                    return;
                }

                Sensor sensor = comboTipo.getValue().equals("HUMEDAD")
                        ? new SensorHumedad(txtId.getText(), txtUbicacion.getText())
                        : new SensorTemperatura(txtId.getText(), txtUbicacion.getText());

                lote.agregarSensor(sensor);
                guardarDatosAutomaticamente();
                actualizarTablaSensores();
                txtId.clear();
                txtUbicacion.clear();
                showAlert("Éxito", "Sensor agregado correctamente", Alert.AlertType.INFORMATION);
            }
        });

        layout.getChildren().addAll(formCard, tableCard);
        return layout;
    }

    private void editarSensor(Sensor sensor, String loteId) {
        // Crear un diálogo personalizado con dos campos
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Sensor");
        dialog.setHeaderText("Modificar datos del sensor");

        // Configurar botones
        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        // Crear grid con los campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtId = new TextField(sensor.getId());
        TextField txtUbicacion = new TextField(sensor.getUbicacion());
        TextField txtTipo = new TextField(sensor.getTipo());

        grid.add(new Label("ID del Sensor:"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Ubicación:"), 0, 1);
        grid.add(txtUbicacion, 1, 1);
        grid.add(new Label("Tipo:"), 0, 2);
        grid.add(txtTipo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Mostrar diálogo y procesar resultado
        dialog.showAndWait().ifPresent(response -> {
            if (response == guardarButtonType) {
                String nuevoId = txtId.getText().trim();
                String nuevaUbicacion = txtUbicacion.getText().trim();

                if (nuevoId.isEmpty() || nuevaUbicacion.isEmpty()) {
                    showAlert("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
                    return;
                }

                // Actualizar el sensor
                sensor.setId(nuevoId);
                sensor.setUbicacion(nuevaUbicacion);
                guardarDatosAutomaticamente();
                actualizarTablaSensores();
                showAlert("Éxito", "Sensor actualizado correctamente", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void eliminarSensor(Sensor sensor, String loteId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar el sensor '" + sensor.getId() + "'?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                var loteOpt = gestorLotes.buscarPorId(loteId);
                if (loteOpt.isPresent()) {
                    loteOpt.get().getSensores().remove(sensor);
                    guardarDatosAutomaticamente();
                    actualizarTablaSensores();
                    showAlert("Éxito", "Sensor eliminado correctamente", Alert.AlertType.INFORMATION);
                }
            }
        });
    }

    // --- Monitoreo View ---
    private VBox createMonitoreoView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        Button btnSimular = createStyledButton("▶️ Simular Lectura de Sensores");
        btnSimular.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnSimular.setMaxWidth(Double.MAX_VALUE);
        btnSimular.setOnAction(e -> simularMonitoreo());

        VBox tableCard = createCard("📊 Lecturas en Tiempo Real");
        tableMonitoreo = new TableView<>();

        TableColumn<MedicionViewModel, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(new PropertyValueFactory<>("lote"));
        colLote.setPrefWidth(300);
        colLote.setMinWidth(150);

        TableColumn<MedicionViewModel, String> colSensor = new TableColumn<>("Sensor");
        colSensor.setCellValueFactory(new PropertyValueFactory<>("sensorId"));
        colSensor.setPrefWidth(200);
        colSensor.setMinWidth(100);

        TableColumn<MedicionViewModel, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setPrefWidth(300);
        colTipo.setMinWidth(180);

        TableColumn<MedicionViewModel, String> colCultivo = new TableColumn<>("Cultivo");
        colCultivo.setCellValueFactory(new PropertyValueFactory<>("cultivo"));
        colCultivo.setPrefWidth(300);
        colCultivo.setMinWidth(180);

        TableColumn<MedicionViewModel, String> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colValor.setPrefWidth(200);
        colValor.setMinWidth(100);

        TableColumn<MedicionViewModel, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setPrefWidth(200);
        colEstado.setMinWidth(150);

        tableMonitoreo.getColumns().addAll(colLote, colSensor, colTipo, colCultivo, colValor, colEstado);
        // Use UNCONSTRAINED to show full text
        // Use CONSTRAINED to adapt to screen size
        tableMonitoreo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Disable editing and column reordering to prevent errors
        tableMonitoreo.setEditable(false);
        tableMonitoreo.getColumns().forEach(col -> col.setReorderable(false));
        // Add CSS class for individual styling
        tableMonitoreo.getStyleClass().add("tabla-monitoreo");

        tableCard.getChildren().add(tableMonitoreo);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        layout.getChildren().addAll(btnSimular, tableCard);
        return layout;
    }

    // --- Alertas View ---
    private HBox createAlertasView() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));

        // Left Side: Alertas Table
        VBox alertsCard = createCard("⚠️ Historial de Alertas");
        alertsCard.setPrefWidth(800);
        tableAlertas = new TableView<>();

        TableColumn<Alerta, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getFechaHora().toString().replace("T", " ")));
        colFecha.setPrefWidth(200);
        colFecha.setMinWidth(180);

        TableColumn<Alerta, String> colNivel = new TableColumn<>("Nivel");
        colNivel.setCellValueFactory(new PropertyValueFactory<>("nivel"));
        colNivel.setPrefWidth(130);
        colNivel.setMinWidth(100);

        TableColumn<Alerta, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(new PropertyValueFactory<>("loteId"));
        colLote.setPrefWidth(130);
        colLote.setMinWidth(100);

        TableColumn<Alerta, String> colMensaje = new TableColumn<>("Mensaje");
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colMensaje.setPrefWidth(450);
        colMensaje.setMinWidth(350);

        tableAlertas.getColumns().addAll(colFecha, colNivel, colLote, colMensaje);
        // Use UNCONSTRAINED to show full text
        // Use CONSTRAINED to adapt to screen size
        tableAlertas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Disable editing and column reordering to prevent errors
        tableAlertas.setEditable(false);
        tableAlertas.getColumns().forEach(col -> col.setReorderable(false));
        // Add CSS class for individual styling
        tableAlertas.getStyleClass().add("tabla-alertas");

        alertsCard.getChildren().add(tableAlertas);
        VBox.setVgrow(tableAlertas, Priority.ALWAYS);

        // Botón para limpiar alertas
        Button btnLimpiarAlertas = createStyledButton("🧹 Limpiar Alertas");
        btnLimpiarAlertas.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLimpiarAlertas.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Está seguro de eliminar todas las alertas?",
                    ButtonType.YES, ButtonType.NO);
            confirmacion.setTitle("Confirmar Limpieza");
            confirmacion.setHeaderText(null);
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    alertaService.getHistorialAlertas().clear();
                    guardarDatosAutomaticamente();
                    actualizarAlertas();
                    showAlert("Éxito", "Todas las alertas han sido eliminadas", Alert.AlertType.INFORMATION);
                }
            });
        });

        alertsCard.getChildren().add(btnLimpiarAlertas);

        // Right Side: Recomendaciones
        VBox recomCard = createCard("💡 Recomendaciones Inteligentes");
        txtRecomendaciones = new TextArea();
        txtRecomendaciones.setEditable(false);
        txtRecomendaciones.setWrapText(true);
        txtRecomendaciones.setPrefHeight(400);

        Button btnRefresh = createStyledButton("🔄 Actualizar");
        btnRefresh.setOnAction(e -> actualizarAlertas());

        recomCard.getChildren().addAll(txtRecomendaciones, btnRefresh);
        VBox.setVgrow(txtRecomendaciones, Priority.ALWAYS);
        HBox.setHgrow(recomCard, Priority.ALWAYS);

        layout.getChildren().addAll(alertsCard, recomCard);
        return layout;
    }

    // --- Helpers ---
    private VBox createCard(String title) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95); " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5); " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: rgba(200, 230, 201, 0.3); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12;");

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.web(PRIMARY_DARK));
        lblTitle.setStyle("-fx-padding: 0 0 10 0;");

        card.getChildren().add(lblTitle);
        return card;
    }

    private void addFormField(GridPane grid, String label, Control field, int row) {
        Label lbl = new Label(label);
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + ACCENT_COLOR + ", #45A049); " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 10 20; " +
                        "-fx-font-weight: 600; " +
                        "-fx-font-size: 13px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(46,125,50,0.3), 8, 0, 0, 3);");
        btn.setCursor(javafx.scene.Cursor.HAND);

        // Add hover effect
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #66BB6A, " + ACCENT_COLOR + "); " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 10 20; " +
                        "-fx-font-weight: 600; " +
                        "-fx-font-size: 13px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(46,125,50,0.4), 12, 0, 0, 5);"));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + ACCENT_COLOR + ", #45A049); " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 10 20; " +
                        "-fx-font-weight: 600; " +
                        "-fx-font-size: 13px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(46,125,50,0.3), 8, 0, 0, 3);"));

        return btn;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Logic ---
    private void actualizarTablaLotes() {
        tableLotes.setItems(FXCollections.observableArrayList(gestorLotes.obtenerTodos()));
    }

    private void actualizarCombos() {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Lote lote : gestorLotes.obtenerTodos()) {
            items.add(lote.getId() + " - " + lote.getNombre());
        }
        comboLotesSensor.setItems(items);
    }

    private void actualizarTablaSensores() {
        ObservableList<SensorViewModel> data = FXCollections.observableArrayList();

        for (Lote lote : gestorLotes.obtenerTodos()) {

            // Agregar sensores de este lote a la tabla
            for (Sensor sensor : lote.getSensores()) {
                data.add(new SensorViewModel(lote.getId(), lote.getNombre(), lote.getTipoCultivo(), sensor));
            }
        }

        tableSensores.setItems(data);
    }

    private void simularMonitoreo() {
        ObservableList<MedicionViewModel> data = FXCollections.observableArrayList();

        for (Lote lote : gestorLotes.obtenerTodos()) {
            for (Sensor sensor : lote.getSensores()) {
                double valor = sensorService.leerSensor(sensor);
                Medicion medicion = new Medicion(valor, sensor.getId(), sensor.getTipo());
                alertaService.verificarMedicion(medicion, lote.getId());

                String estado = "✅ Normal";
                if (sensor.getTipo().equals("HUMEDAD")) {
                    if (valor < 30)
                        estado = "🔴 CRÍTICO";
                    else if (valor < 50)
                        estado = "⚠️ Bajo";
                } else if (sensor.getTipo().equals("TEMPERATURA")) {
                    if (valor > 35)
                        estado = "🔴 CRÍTICO";
                    else if (valor > 30)
                        estado = "⚠️ Alto";
                }

                String valorStr = String.format("%.1f%s", valor, sensor.getTipo().equals("HUMEDAD") ? "%" : "°C");
                data.add(new MedicionViewModel(lote.getNombre(), sensor.getId(), sensor.getTipo(),
                        lote.getTipoCultivo(), valorStr, estado));
            }
        }
        tableMonitoreo.setItems(data);
        actualizarAlertas();
        if (!data.isEmpty()) {
            guardarDatosAutomaticamente();
        }
    }

    private void actualizarAlertas() {
        List<Alerta> alertas = alertaService.getHistorialAlertas();
        tableAlertas.setItems(FXCollections.observableArrayList(alertas));

        List<Recomendacion> recomendaciones = recomendacionService.generarRecomendaciones(alertas);
        StringBuilder sb = new StringBuilder();
        if (recomendaciones.isEmpty()) {
            sb.append("✅ No hay recomendaciones pendientes.\n\nSus cultivos están en buen estado.");
        } else {
            int count = 1;
            for (Recomendacion rec : recomendaciones) {
                sb.append(count++).append(". Lote ").append(rec.getLoteId()).append("\n");
                sb.append("   ").append(rec.getMensaje()).append("\n");
                sb.append("   ➜ ACCIÓN: ").append(rec.getAccionSugerida()).append("\n\n");
            }
        }
        txtRecomendaciones.setText(sb.toString());
    }

    private void cargarDatosAutomaticamente() {
        try {
            var data = toonService.importarDatos();
            System.out.println(
                    "✓ Archivo TOON encontrado. Lotes: " + data.lotes.size() + ", Alertas: " + data.alertas.size());
            for (Lote lote : data.lotes)
                gestorLotes.registrarLote(lote);
            for (Alerta alerta : data.alertas)
                alertaService.getHistorialAlertas().add(alerta);
            // Don't update tables here - they don't exist yet!
            System.out.println("Datos cargados automáticamente desde TOON");
        } catch (Exception e) {
            System.out.println("Iniciando con datos vacíos (TOON no encontrado o error)");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardarDatosAutomaticamente() {
        if (!isReady) {
            System.out.println("[SKIP] Guardado omitido - aplicación aún inicializando");
            return;
        }
        try {
            System.out.println("[DEBUG] Guardando datos...");
            System.out.println("[DEBUG] Total de lotes: " + gestorLotes.obtenerTodos().size());
            for (Lote lote : gestorLotes.obtenerTodos()) {
                System.out
                        .println("[DEBUG] Lote " + lote.getId() + " tiene " + lote.getSensores().size() + " sensores");
            }
            toonService.exportarDatos(gestorLotes, alertaService);
            System.out.println("Datos guardados automáticamente en TOON");
        } catch (Exception e) {
            System.err.println("Error al guardar datos automáticamente: " + e.getMessage());
        }
    }

    // ViewModel for Monitoring Table
    public static class MedicionViewModel {
        private final SimpleStringProperty lote;
        private final SimpleStringProperty sensorId;
        private final SimpleStringProperty tipo;
        private final SimpleStringProperty cultivo;
        private final SimpleStringProperty valor;
        private final SimpleStringProperty estado;

        public MedicionViewModel(String lote, String sensorId, String tipo, String cultivo, String valor,
                String estado) {
            this.lote = new SimpleStringProperty(lote);
            this.sensorId = new SimpleStringProperty(sensorId);
            this.tipo = new SimpleStringProperty(tipo);
            this.cultivo = new SimpleStringProperty(cultivo);
            this.valor = new SimpleStringProperty(valor);
            this.estado = new SimpleStringProperty(estado);
        }

        public String getLote() {
            return lote.get();
        }

        public String getSensorId() {
            return sensorId.get();
        }

        public String getTipo() {
            return tipo.get();
        }

        public String getValor() {
            return valor.get();
        }

        public String getEstado() {
            return estado.get();
        }

        public String getCultivo() {
            return cultivo.get();
        }

        public void setCultivo(String cultivo) {
            this.cultivo.set(cultivo);
        }
    }

    // ViewModel for Sensors Table
    public static class SensorViewModel {
        private final SimpleStringProperty loteId;
        private final SimpleStringProperty loteNombre;
        private final SimpleStringProperty sensorId;
        private final SimpleStringProperty tipo;
        private final SimpleStringProperty cultivo;
        private final SimpleStringProperty ubicacion;
        private final Sensor sensor;

        public SensorViewModel(String loteId, String loteNombre, String cultivo, Sensor sensor) {
            this.loteId = new SimpleStringProperty(loteId);
            this.loteNombre = new SimpleStringProperty(loteNombre);
            this.sensor = sensor;
            this.sensorId = new SimpleStringProperty(sensor.getId());
            this.tipo = new SimpleStringProperty(sensor.getTipo());
            this.ubicacion = new SimpleStringProperty(sensor.getUbicacion());
            this.cultivo = new SimpleStringProperty(cultivo);
        }

        public String getLoteId() {
            return loteId.get();
        }

        public String getLoteNombre() {
            return loteNombre.get();
        }

        public String getSensorId() {
            return sensorId.get();
        }

        public String getTipo() {
            return tipo.get();
        }

        public String getUbicacion() {
            return ubicacion.get();
        }

        public Sensor getSensor() {
            return sensor;
        }

    }
}
