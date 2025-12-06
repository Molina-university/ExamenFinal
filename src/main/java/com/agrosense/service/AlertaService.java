package com.agrosense.service;

import com.agrosense.model.Alerta;
import com.agrosense.model.Medicion;
import java.util.ArrayList;
import java.util.List;

public class AlertaService {
    private List<Alerta> historialAlertas;

    public AlertaService() {
        this.historialAlertas = new ArrayList<>();
    }

    public void verificarMedicion(Medicion medicion, String loteId) {
        double valor = medicion.getValor();
        String tipo = medicion.getTipoSensor();

        if (tipo.equalsIgnoreCase("HUMEDAD")) {
            if (valor < 30.0) {
                generarAlerta("Humedad crítica baja (" + String.format("%.2f", valor) + "%)", Alerta.Nivel.CRITICAL,
                        loteId);
            } else if (valor < 50.0) {
                generarAlerta("Humedad baja (" + String.format("%.2f", valor) + "%)", Alerta.Nivel.WARNING, loteId);
            }
        } else if (tipo.equalsIgnoreCase("TEMPERATURA")) {
            if (valor > 35.0) {
                generarAlerta("Temperatura crítica alta (" + String.format("%.2f", valor) + "°C)",
                        Alerta.Nivel.CRITICAL, loteId);
            } else if (valor > 30.0) {
                generarAlerta("Temperatura alta (" + String.format("%.2f", valor) + "°C)", Alerta.Nivel.WARNING,
                        loteId);
            }
        }
    }

    private void generarAlerta(String mensaje, Alerta.Nivel nivel, String loteId) {
        Alerta alerta = new Alerta(mensaje, nivel, loteId);
        historialAlertas.add(alerta);
        System.out.println(">>> ALERTA GENERADA: " + alerta);
    }

    public List<Alerta> getHistorialAlertas() {
        return historialAlertas;
    }

    public List<Alerta> getAlertasPorLote(String loteId) {
        List<Alerta> result = new ArrayList<>();
        for (Alerta a : historialAlertas) {
            if (a.getLoteId().equals(loteId)) {
                result.add(a);
            }
        }
        return result;
    }
}
