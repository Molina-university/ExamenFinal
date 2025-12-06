package com.agrosense.model;

import java.time.LocalDateTime;

public class Medicion {
    private LocalDateTime fechaHora;
    private double valor;
    private String sensorId;
    private String tipoSensor;

    public Medicion(double valor, String sensorId, String tipoSensor) {
        this.fechaHora = LocalDateTime.now();
        this.valor = valor;
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public double getValor() {
        return valor;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    @Override
    public String toString() {
        return "Medicion{" +
                "fechaHora=" + fechaHora +
                ", valor=" + valor +
                ", sensorId='" + sensorId + '\'' +
                ", tipoSensor='" + tipoSensor + '\'' +
                '}';
    }
}
