package com.agrosense.model;

import java.util.Random;

public class SensorHumedad extends Sensor {

    public SensorHumedad(String id, String ubicacion) {
        super(id, "HUMEDAD", ubicacion);
    }

    @Override
    public double leerDato() {
        // Simulación interna si se usa directamente
        return new Random().nextDouble() * 100;
    }
}
