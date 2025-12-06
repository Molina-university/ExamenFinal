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
    private TextArea txtRecomendaciones;
    private ComboBox<String> comboLotesSensor;

    // Colors
    private static final String PRIMARY_COLOR = "#228B22";
    private static final String ACCENT_COLOR = "#4CAF50";
    private static final String BG_COLOR = "#F5F8FA";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Auto-load data
        cargarDatosAutomaticamente();

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

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

        Scene scene = new Scene(root, 1100, 700);
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
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";");
        header.setSpacing(20);

        VBox titleBox = new VBox();
        Label title = new Label("🌿 AgroSense");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Sistema Inteligente de Monitoreo Agrícola");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#C8E6C9"));

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
    private VBox createLotesView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        // Form
        VBox formCard = createCard("➕ Registrar Nuevo Lote");
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
        btnRegistrar
                .setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold;");
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

        // Table
        VBox tableCard = createCard("📋 Lotes Registrados");
        tableLotes = new TableView<>();

        TableColumn<Lote, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Lote, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Lote, String> colCultivo = new TableColumn<>("Cultivo");
        colCultivo.setCellValueFactory(new PropertyValueFactory<>("tipoCultivo"));

        TableColumn<Lote, Double> colArea = new TableColumn<>("Área (ha)");
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));

        tableLotes.getColumns().addAll(colId, colNombre, colCultivo, colArea);
        tableLotes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableCard.getChildren().add(tableLotes);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        layout.getChildren().addAll(formCard, tableCard);
        return layout;
    }

    // --- Sensores View ---
    private VBox createSensoresView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        VBox formCard = createCard("📡 Agregar Sensor a Lote");
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        comboLotesSensor = new ComboBox<>();
        comboLotesSensor.setPromptText("Seleccionar Lote");
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
        btnAgregar.setOnAction(e -> {
            String selectedLote = comboLotesSensor.getValue();
            if (selectedLote == null) {
                showAlert("Error", "Seleccione un lote", Alert.AlertType.ERROR);
                return;
            }

            String loteId = selectedLote.split(" - ")[0];
            var loteOpt = gestorLotes.buscarPorId(loteId);

            if (loteOpt.isPresent()) {
                Lote lote = loteOpt.get();
                Sensor sensor = comboTipo.getValue().equals("HUMEDAD")
                        ? new SensorHumedad(txtId.getText(), txtUbicacion.getText())
                        : new SensorTemperatura(txtId.getText(), txtUbicacion.getText());

                lote.agregarSensor(sensor);
                guardarDatosAutomaticamente();
                actualizarTablaLotes();
                txtId.clear();
                txtUbicacion.clear();
                showAlert("Éxito", "Sensor agregado correctamente", Alert.AlertType.INFORMATION);
            }
        });

        grid.add(btnAgregar, 1, 4);
        formCard.getChildren().add(grid);

        // Info Card
        VBox infoCard = createCard("💡 Información");
        Label info = new Label("Los sensores IoT monitorean las condiciones del cultivo en tiempo real.\n\n" +
                "• Sensores de HUMEDAD: Miden el nivel de humedad del suelo (0-100%)\n" +
                "• Sensores de TEMPERATURA: Registran la temperatura ambiente (°C)");
        info.setWrapText(true);
        info.setStyle("-fx-font-size: 14px;");
        infoCard.getChildren().add(info);

        layout.getChildren().addAll(formCard, infoCard);
        return layout;
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

        TableColumn<MedicionViewModel, String> colSensor = new TableColumn<>("Sensor");
        colSensor.setCellValueFactory(new PropertyValueFactory<>("sensorId"));

        TableColumn<MedicionViewModel, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<MedicionViewModel, String> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        TableColumn<MedicionViewModel, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tableMonitoreo.getColumns().addAll(colLote, colSensor, colTipo, colValor, colEstado);
        tableMonitoreo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableCard.getChildren().add(tableMonitoreo);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        layout.getChildren().addAll(btnSimular, tableCard);
        return layout;
    }

    // --- Alertas View ---
    private VBox createAlertasView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        VBox alertsCard = createCard("⚠️ Historial de Alertas");
        tableAlertas = new TableView<>();

        TableColumn<Alerta, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getFechaHora().toString().replace("T", " ")));

        TableColumn<Alerta, String> colNivel = new TableColumn<>("Nivel");
        colNivel.setCellValueFactory(new PropertyValueFactory<>("nivel"));

        TableColumn<Alerta, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(new PropertyValueFactory<>("loteId"));

        TableColumn<Alerta, String> colMensaje = new TableColumn<>("Mensaje");
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));

        tableAlertas.getColumns().addAll(colFecha, colNivel, colLote, colMensaje);
        tableAlertas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        alertsCard.getChildren().add(tableAlertas);
        VBox.setVgrow(alertsCard, Priority.ALWAYS);

        VBox recomCard = createCard("💡 Recomendaciones Inteligentes");
        txtRecomendaciones = new TextArea();
        txtRecomendaciones.setEditable(false);
        txtRecomendaciones.setWrapText(true);

        Button btnRefresh = createStyledButton("🔄 Actualizar");
        btnRefresh.setOnAction(e -> actualizarAlertas());

        recomCard.getChildren().addAll(txtRecomendaciones, btnRefresh);

        layout.getChildren().addAll(alertsCard, recomCard);
        return layout;
    }

    // --- Helpers ---
    private VBox createCard(String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 5;");

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

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
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-border-color: "
                + PRIMARY_COLOR + "; -fx-border-radius: 3; -fx-background-radius: 3;");
        btn.setCursor(javafx.scene.Cursor.HAND);
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
                data.add(new MedicionViewModel(lote.getNombre(), sensor.getId(), sensor.getTipo(), valorStr, estado));
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
            for (Lote lote : data.lotes)
                gestorLotes.registrarLote(lote);
            for (Alerta alerta : data.alertas)
                alertaService.getHistorialAlertas().add(alerta);
            actualizarTablaLotes();
            actualizarCombos();
            actualizarAlertas();
            System.out.println("Datos cargados automáticamente desde TOON");
        } catch (Exception e) {
            System.out.println("Iniciando con datos vacíos (TOON no encontrado o error)");
        }
    }

    private void guardarDatosAutomaticamente() {
        try {
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
        private final SimpleStringProperty valor;
        private final SimpleStringProperty estado;

        public MedicionViewModel(String lote, String sensorId, String tipo, String valor, String estado) {
            this.lote = new SimpleStringProperty(lote);
            this.sensorId = new SimpleStringProperty(sensorId);
            this.tipo = new SimpleStringProperty(tipo);
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
    }
}
