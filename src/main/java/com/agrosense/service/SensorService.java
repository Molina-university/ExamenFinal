package com.agrosense.service;

import com.agrosense.model.Sensor;
import java.util.Random;

public class SensorService {
    private Random random;

    public SensorService() {
        this.random = new Random();
    }

    // Simula una lectura para un sensor específico
    // En una implementación real, esto conectaría con el hardware
    public double leerSensor(Sensor sensor) {
        // Simulación básica:
        // Humedad: 0 - 100%
        // Temperatura: 10 - 40 °C

        if (sensor.getTipo().equalsIgnoreCase("HUMEDAD")) {
            return 10 + (90 * random.nextDouble()); // 10% a 100%
        } else if (sensor.getTipo().equalsIgnoreCase("TEMPERATURA")) {
            return 15 + (25 * random.nextDouble()); // 15°C a 40°C
        }
        return 0.0;
    }
}
