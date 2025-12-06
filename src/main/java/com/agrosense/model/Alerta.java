package com.agrosense.model;

import java.time.LocalDateTime;

public class Alerta {
    public enum Nivel {
        INFO, WARNING, CRITICAL
    }

    private String mensaje;
    private Nivel nivel;
    private LocalDateTime fechaHora;
    private String loteId;

    public Alerta(String mensaje, Nivel nivel, String loteId) {
        this(mensaje, nivel, loteId, LocalDateTime.now());
    }

    public Alerta(String mensaje, Nivel nivel, String loteId, LocalDateTime fechaHora) {
        this.mensaje = mensaje;
        this.nivel = nivel;
        this.loteId = loteId;
        this.fechaHora = fechaHora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getLoteId() {
        return loteId;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Lote: %s - %s", nivel, fechaHora, loteId, mensaje);
    }
}
