package com.agrosense.service;

import com.agrosense.model.*;
import java.util.*;

/**
 * Servicio para análisis estadístico de datos de sensores
 */
public class EstadisticasService {

    private Map<String, List<Double>> historicoHumedad;
    private Map<String, List<Double>> historicoTemperatura;

    public EstadisticasService() {
        this.historicoHumedad = new HashMap<>();
        this.historicoTemperatura = new HashMap<>();
    }

    /**
     * Registra una medición en el histórico
     */
    public void registrarMedicion(String loteId, String tipoSensor, double valor) {
        if (tipoSensor.equalsIgnoreCase("HUMEDAD")) {
            historicoHumedad.computeIfAbsent(loteId, k -> new ArrayList<>()).add(valor);
        } else if (tipoSensor.equalsIgnoreCase("TEMPERATURA")) {
            historicoTemperatura.computeIfAbsent(loteId, k -> new ArrayList<>()).add(valor);
        }

        // Mantener solo las últimas 100 mediciones por lote
        limitarHistorico(historicoHumedad.get(loteId));
        limitarHistorico(historicoTemperatura.get(loteId));
    }

    private void limitarHistorico(List<Double> lista) {
        if (lista != null && lista.size() > 100) {
            lista.remove(0);
        }
    }

    /**
     * Calcula el promedio de humedad de un lote
     */
    public double calcularPromedioHumedad(String loteId) {
        List<Double> valores = historicoHumedad.get(loteId);
        if (valores == null || valores.isEmpty()) {
            return 0.0;
        }
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * Calcula el promedio de temperatura de un lote
     */
    public double calcularPromedioTemperatura(String loteId) {
        List<Double> valores = historicoTemperatura.get(loteId);
        if (valores == null || valores.isEmpty()) {
            return 0.0;
        }
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * Obtiene la tendencia de un sensor (SUBIENDO, BAJANDO, ESTABLE)
     */
    public String obtenerTendencia(String loteId, String tipoSensor) {
        List<Double> valores;

        if (tipoSensor.equalsIgnoreCase("HUMEDAD")) {
            valores = historicoHumedad.get(loteId);
        } else if (tipoSensor.equalsIgnoreCase("TEMPERATURA")) {
            valores = historicoTemperatura.get(loteId);
        } else {
            return "DESCONOCIDO";
        }

        if (valores == null || valores.size() < 3) {
            return "INSUFICIENTE";
        }

        // Comparar últimas 3 mediciones
        int size = valores.size();
        double ultima = valores.get(size - 1);
        double penultima = valores.get(size - 2);
        double antepenultima = valores.get(size - 3);

        double promReciente = (ultima + penultima) / 2;
        double promAnterior = (penultima + antepenultima) / 2;

        double diferencia = promReciente - promAnterior;

        if (Math.abs(diferencia) < 2.0) {
            return "ESTABLE";
        } else if (diferencia > 0) {
            return "SUBIENDO";
        } else {
            return "BAJANDO";
        }
    }

    /**
     * Genera un resumen general de todos los lotes
     */
    public String generarResumenGeneral(GestorLotes gestorLotes) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== RESUMEN GENERAL DEL SISTEMA ===\n\n");

        List<Lote> lotes = gestorLotes.obtenerTodos();
        resumen.append("Total de lotes: ").append(lotes.size()).append("\n");

        int totalSensores = lotes.stream()
                .mapToInt(l -> l.getSensores().size())
                .sum();
        resumen.append("Total de sensores: ").append(totalSensores).append("\n\n");

        for (Lote lote : lotes) {
            resumen.append("Lote ").append(lote.getId()).append(" - ").append(lote.getNombre()).append("\n");
            resumen.append("  Cultivo: ").append(lote.getTipoCultivo()).append("\n");

            if (lote.getFechaSiembra() != null) {
                resumen.append("  Días desde siembra: ").append(lote.calcularDiasDesdeSiembra()).append("\n");
                resumen.append("  Etapa: ").append(lote.getEtapaCrecimiento()).append("\n");
            }

            double promHum = calcularPromedioHumedad(lote.getId());
            double promTemp = calcularPromedioTemperatura(lote.getId());

            if (promHum > 0) {
                resumen.append("  Humedad promedio: ").append(String.format("%.1f%%", promHum));
                resumen.append(" (").append(obtenerTendencia(lote.getId(), "HUMEDAD")).append(")\n");
            }

            if (promTemp > 0) {
                resumen.append("  Temperatura promedio: ").append(String.format("%.1f°C", promTemp));
                resumen.append(" (").append(obtenerTendencia(lote.getId(), "TEMPERATURA")).append(")\n");
            }

            resumen.append("\n");
        }

        return resumen.toString();
    }

    /**
     * Obtiene el histórico de humedad de un lote
     */
    public List<Double> getHistoricoHumedad(String loteId) {
        return new ArrayList<>(historicoHumedad.getOrDefault(loteId, new ArrayList<>()));
    }

    /**
     * Obtiene el histórico de temperatura de un lote
     */
    public List<Double> getHistoricoTemperatura(String loteId) {
        return new ArrayList<>(historicoTemperatura.getOrDefault(loteId, new ArrayList<>()));
    }

    /**
     * Limpia todo el histórico
     */
    public void limpiarHistorico() {
        historicoHumedad.clear();
        historicoTemperatura.clear();
    }
}
