package com.agrosense.model;

public abstract class Sensor {
    protected String id;
    protected String tipo; // HUMEDAD, TEMPERATURA
    protected String ubicacion;

    public Sensor(String id, String tipo, String ubicacion) {
        this.id = id;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    // Método abstracto para simular la lectura de datos
    public abstract double leerDato();
    
    @Override
    public String toString() {
        return "Sensor{" + "id='" + id + '\'' + ", tipo='" + tipo + '\'' + ", ubicacion='" + ubicacion + '\'' + '}';
    }
}
