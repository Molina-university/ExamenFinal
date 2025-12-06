package com.agrosense.service;

import com.agrosense.model.Alerta;
import com.agrosense.model.Recomendacion;
import java.util.ArrayList;
import java.util.List;

public class RecomendacionService {

    public List<Recomendacion> generarRecomendaciones(List<Alerta> alertas) {
        List<Recomendacion> recomendaciones = new ArrayList<>();

        for (Alerta alerta : alertas) {
            if (alerta.getMensaje().contains("Humedad")) {
                if (alerta.getNivel() == Alerta.Nivel.CRITICAL) {
                    recomendaciones.add(new Recomendacion(
                            "Suelo extremadamente seco detectado.",
                            "Activar sistema de riego de emergencia inmediatamente.",
                            alerta.getLoteId()));
                } else if (alerta.getNivel() == Alerta.Nivel.WARNING) {
                    recomendaciones.add(new Recomendacion(
                            "Niveles de humedad descendiendo.",
                            "Programar riego para las próximas horas.",
                            alerta.getLoteId()));
                }
            } else if (alerta.getMensaje().contains("Temperatura")) {
                if (alerta.getNivel() == Alerta.Nivel.CRITICAL) {
                    recomendaciones.add(new Recomendacion(
                            "Calor excesivo puede dañar el cultivo.",
                            "Verificar sombras o aumentar frecuencia de riego para enfriar.",
                            alerta.getLoteId()));
                }
            }
        }
        return recomendaciones;
    }
}
