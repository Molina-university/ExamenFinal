package com.agrosense.service;

import com.agrosense.model.*;
import java.time.LocalDate;

/**
 * Servicio para cargar datos de demostración pre-establecidos con valores
 * realistas
 * basados en investigación de condiciones óptimas de cultivo.
 */
public class DatosDemoService {

    /**
     * Carga datos de demostración realistas en el sistema
     * 
     * @param gestorLotes   gestor de lotes donde se cargarán los datos
     * @param alertaService servicio de alertas para generar alertas iniciales
     */
    public void cargarDatosDemo(GestorLotes gestorLotes, AlertaService alertaService) {
        // Limpiar datos existentes
        gestorLotes.limpiar();
        alertaService.getHistorialAlertas().clear();

        // Lote 1: Tomate - Condiciones normales
        Lote loteTomate = new Lote("L001", "Cultivo de Tomate Norte", "Tomate", 2.5);
        loteTomate.setFechaSiembra(LocalDate.now().minusDays(45));
        loteTomate.setEtapaCrecimiento("VEGETATIVO");
        loteTomate.setNotas("Riego por goteo instalado. Fertilización programada cada 15 días.");

        SensorHumedad sensorH1 = new SensorHumedad("H001", "Zona Norte");
        SensorTemperatura sensorT1 = new SensorTemperatura("T001", "Zona Norte");
        loteTomate.agregarSensor(sensorH1);
        loteTomate.agregarSensor(sensorT1);

        gestorLotes.registrarLote(loteTomate);

        // Simular lectura normal para tomate (55% humedad, 22°C)
        Medicion medH1 = new Medicion(55.0, "H001", "HUMEDAD");
        Medicion medT1 = new Medicion(22.0, "T001", "TEMPERATURA");
        alertaService.verificarMedicion(medH1, "L001");
        alertaService.verificarMedicion(medT1, "L001");

        // Lote 2: Lechuga - Humedad CRÍTICA baja
        Lote loteLechuga = new Lote("L002", "Lechuga Hidropónica", "Lechuga", 1.8);
        loteLechuga.setFechaSiembra(LocalDate.now().minusDays(25));
        loteLechuga.setEtapaCrecimiento("PLANTULA");
        loteLechuga.setNotas("Sistema hidropónico. Requiere monitoreo constante de pH.");

        SensorHumedad sensorH2 = new SensorHumedad("H002", "Invernadero A");
        SensorTemperatura sensorT2 = new SensorTemperatura("T002", "Invernadero A");
        loteLechuga.agregarSensor(sensorH2);
        loteLechuga.agregarSensor(sensorT2);

        gestorLotes.registrarLote(loteLechuga);

        // Simular lectura CRÍTICA para lechuga (28% humedad - muy baja, 19°C normal)
        Medicion medH2 = new Medicion(28.0, "H002", "HUMEDAD");
        Medicion medT2 = new Medicion(19.0, "T002", "TEMPERATURA");
        alertaService.verificarMedicion(medH2, "L002");
        alertaService.verificarMedicion(medT2, "L002");

        // Lote 3: Fresa - Temperatura CRÍTICA alta
        Lote loteFresa = new Lote("L003", "Fresas Premium", "Fresa", 3.2);
        loteFresa.setFechaSiembra(LocalDate.now().minusDays(60));
        loteFresa.setEtapaCrecimiento("FLORACION");
        loteFresa.setNotas("Variedad de fresa de día neutro. Cosecha continua esperada.");

        SensorHumedad sensorH3 = new SensorHumedad("H003", "Sector Sur");
        SensorTemperatura sensorT3 = new SensorTemperatura("T003", "Sector Sur");
        loteFresa.agregarSensor(sensorH3);
        loteFresa.agregarSensor(sensorT3);

        gestorLotes.registrarLote(loteFresa);

        // Simular lectura CRÍTICA para fresa (65% humedad normal, 36°C - muy alta)
        Medicion medH3 = new Medicion(65.0, "H003", "HUMEDAD");
        Medicion medT3 = new Medicion(36.0, "T003", "TEMPERATURA");
        alertaService.verificarMedicion(medH3, "L003");
        alertaService.verificarMedicion(medT3, "L003");

        // Lote 4: Maíz - Condiciones óptimas
        Lote loteMaiz = new Lote("L004", "Maíz Amarillo Duro", "Maíz", 5.0);
        loteMaiz.setFechaSiembra(LocalDate.now().minusDays(70));
        loteMaiz.setEtapaCrecimiento("FLORACION");
        loteMaiz.setNotas("Etapa crítica de floración. Mantener humedad constante.");

        SensorHumedad sensorH4 = new SensorHumedad("H004", "Parcela Este");
        SensorTemperatura sensorT4 = new SensorTemperatura("T004", "Parcela Este");
        loteMaiz.agregarSensor(sensorH4);
        loteMaiz.agregarSensor(sensorT4);

        gestorLotes.registrarLote(loteMaiz);

        // Simular lectura normal para maíz (52% humedad, 28°C)
        Medicion medH4 = new Medicion(52.0, "H004", "HUMEDAD");
        Medicion medT4 = new Medicion(28.0, "T004", "TEMPERATURA");
        alertaService.verificarMedicion(medH4, "L004");
        alertaService.verificarMedicion(medT4, "L004");

        System.out.println("✓ Datos de demostración cargados exitosamente:");
        System.out.println("  - 4 lotes con diferentes cultivos");
        System.out.println("  - 8 sensores (4 humedad, 4 temperatura)");
        System.out.println("  - " + alertaService.getHistorialAlertas().size() + " alertas generadas");
    }

    /**
     * Obtiene información sobre los rangos óptimos de un cultivo
     * 
     * @param tipoCultivo nombre del cultivo
     * @return String con información de rangos óptimos
     */
    public String obtenerRangosOptimos(String tipoCultivo) {
        return switch (tipoCultivo.toLowerCase()) {
            case "tomate" -> "Temperatura: 16-21°C | Humedad: 40-80% | Crítico: <30% o >35°C";
            case "lechuga" -> "Temperatura: 16-20°C | Humedad: 60-80% | Sensible al calor >24°C";
            case "fresa" -> "Temperatura: 10-27°C | Humedad: >50% | Requiere riego constante";
            case "maíz" -> "Temperatura: 10-32°C | Humedad: >50% | Crítico durante floración";
            default -> "Rangos no disponibles para este cultivo";
        };
    }
}
