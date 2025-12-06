package com.agrosense.ui;

import com.agrosense.model.*;
import com.agrosense.service.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private GestorLotes gestorLotes;
    private SensorService sensorService;
    private AlertaService alertaService;
    private RecomendacionService recomendacionService;
    private Scanner scanner;

    public ConsoleUI() {
        this.gestorLotes = new GestorLotes();
        this.sensorService = new SensorService();
        this.alertaService = new AlertaService();
        this.recomendacionService = new RecomendacionService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion = 0;
        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }
            procesarOpcion(opcion);
        } while (opcion != 6);
    }

    private void mostrarMenu() {
        System.out.println("\n=== AGROSENSE - SISTEMA DE MONITOREO ===");
        System.out.println("1. Registrar Lote");
        System.out.println("2. Agregar Sensor a Lote");
        System.out.println("3. Simular Monitoreo (Leer Sensores)");
        System.out.println("4. Ver Alertas");
        System.out.println("5. Ver Recomendaciones");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                registrarLote();
                break;
            case 2:
                agregarSensor();
                break;
            case 3:
                simularMonitoreo();
                break;
            case 4:
                verAlertas();
                break;
            case 5:
                verRecomendaciones();
                break;
            case 6:
                System.out.println("Saliendo del sistema...");
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private void registrarLote() {
        System.out.println("\n--- Registrar Nuevo Lote ---");
        System.out.print("ID del Lote: ");
        String id = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Tipo de Cultivo: ");
        String cultivo = scanner.nextLine();
        System.out.print("Área (hectáreas): ");
        double area = Double.parseDouble(scanner.nextLine());

        Lote lote = new Lote(id, nombre, cultivo, area);
        gestorLotes.registrarLote(lote);
        System.out.println("Lote registrado exitosamente.");
    }

    private void agregarSensor() {
        System.out.println("\n--- Agregar Sensor ---");
        System.out.print("ID del Lote al que asignar: ");
        String loteId = scanner.nextLine();

        var loteOpt = gestorLotes.buscarPorId(loteId);
        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            System.out.print("ID del Sensor: ");
            String sensorId = scanner.nextLine();
            System.out.print("Tipo (HUMEDAD/TEMPERATURA): ");
            String tipo = scanner.nextLine().toUpperCase();
            System.out.print("Ubicación en el lote: ");
            String ubicacion = scanner.nextLine();

            Sensor sensor = null;
            if (tipo.equals("HUMEDAD")) {
                sensor = new SensorHumedad(sensorId, ubicacion);
            } else if (tipo.equals("TEMPERATURA")) {
                sensor = new SensorTemperatura(sensorId, ubicacion);
            } else {
                System.out.println("Tipo de sensor no válido.");
                return;
            }

            lote.agregarSensor(sensor);
            System.out.println("Sensor agregado al lote " + lote.getNombre());
        } else {
            System.out.println("Lote no encontrado.");
        }
    }

    private void simularMonitoreo() {
        System.out.println("\n--- Simulando Monitoreo ---");
        List<Lote> lotes = gestorLotes.obtenerTodos();
        if (lotes.isEmpty()) {
            System.out.println("No hay lotes registrados.");
            return;
        }

        for (Lote lote : lotes) {
            System.out.println("Monitoreando Lote: " + lote.getNombre());
            for (Sensor sensor : lote.getSensores()) {
                // Usamos el servicio para leer (que usa su propia lógica o la del sensor)
                double valor = sensorService.leerSensor(sensor);
                Medicion medicion = new Medicion(valor, sensor.getId(), sensor.getTipo());

                System.out.println(" -> Sensor " + sensor.getTipo() + " (" + sensor.getId() + "): "
                        + String.format("%.2f", valor));

                // Verificar alertas
                alertaService.verificarMedicion(medicion, lote.getId());
            }
        }
        System.out.println("Monitoreo finalizado.");
    }

    private void verAlertas() {
        System.out.println("\n--- Historial de Alertas ---");
        List<Alerta> alertas = alertaService.getHistorialAlertas();
        if (alertas.isEmpty()) {
            System.out.println("No hay alertas registradas.");
        } else {
            for (Alerta alerta : alertas) {
                System.out.println(alerta);
            }
        }
    }

    private void verRecomendaciones() {
        System.out.println("\n--- Recomendaciones ---");
        List<Alerta> alertas = alertaService.getHistorialAlertas();
        List<Recomendacion> recomendaciones = recomendacionService.generarRecomendaciones(alertas);

        if (recomendaciones.isEmpty()) {
            System.out.println("No hay recomendaciones pendientes.");
        } else {
            for (Recomendacion rec : recomendaciones) {
                System.out.println(rec);
                System.out.println("-------------------------");
            }
        }
    }

    public static void main(String[] args) {
        new ConsoleUI().iniciar();
    }
}
