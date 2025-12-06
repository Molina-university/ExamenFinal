package com.agrosense.model;

import java.util.Random;

public class SensorTemperatura extends Sensor {

    public SensorTemperatura(String id, String ubicacion) {
        super(id, "TEMPERATURA", ubicacion);
    }

    @Override
    public double leerDato() {
        // Simulación interna si se usa directamente
        return 10 + (new Random().nextDouble() * 30);
    }
}
