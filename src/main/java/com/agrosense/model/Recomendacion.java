package com.agrosense.model;

public class Recomendacion {
    private String mensaje;
    private String accionSugerida;
    private String loteId;

    public Recomendacion(String mensaje, String accionSugerida, String loteId) {
        this.mensaje = mensaje;
        this.accionSugerida = accionSugerida;
        this.loteId = loteId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getAccionSugerida() {
        return accionSugerida;
    }

    public String getLoteId() {
        return loteId;
    }

    @Override
    public String toString() {
        return "Recomendacion para Lote " + loteId + ":\n" +
               " - " + mensaje + "\n" +
               " -> Acción: " + accionSugerida;
    }
}
