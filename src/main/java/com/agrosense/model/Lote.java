package com.agrosense.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Lote {
    private String id;
    private String nombre;
    private String tipoCultivo;
    private double area; // en hectáreas o m2
    private List<Sensor> sensores;

    // Nuevos campos para gestión agrícola
    private LocalDate fechaSiembra;
    private String etapaCrecimiento; // "GERMINACION", "PLANTULA", "VEGETATIVO", "FLORACION", "FRUCTIFICACION"
    private String notas;

    public Lote(String id, String nombre, String tipoCultivo, double area) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del lote no puede estar vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del lote no puede estar vacío");
        }
        if (area <= 0) {
            throw new IllegalArgumentException("El área debe ser mayor a 0");
        }

        this.id = id;
        this.nombre = nombre;
        this.tipoCultivo = tipoCultivo;
        this.area = area;
        this.sensores = new ArrayList<>();
        this.etapaCrecimiento = "GERMINACION"; // Etapa inicial por defecto
        this.notas = "";
    }

    public void agregarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoCultivo() {
        return tipoCultivo;
    }

    public double getArea() {
        return area;
    }

    // Getters y Setters para gestión agrícola
    public LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public void setFechaSiembra(LocalDate fechaSiembra) {
        this.fechaSiembra = fechaSiembra;
    }

    public String getEtapaCrecimiento() {
        return etapaCrecimiento;
    }

    public void setEtapaCrecimiento(String etapaCrecimiento) {
        this.etapaCrecimiento = etapaCrecimiento;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas != null ? notas : "";
    }

    /**
     * Calcula los días transcurridos desde la siembra
     * 
     * @return días desde la siembra, o -1 si no se ha establecido fecha
     */
    public long calcularDiasDesdeSiembra() {
        if (fechaSiembra == null) {
            return -1;
        }
        return ChronoUnit.DAYS.between(fechaSiembra, LocalDate.now());
    }

    /**
     * Obtiene un resumen del estado general del lote
     * 
     * @return String con el estado del lote
     */
    public String getEstadoGeneral() {
        long dias = calcularDiasDesdeSiembra();
        if (dias < 0) {
            return "Sin información de siembra";
        }
        return String.format("%s - Día %d - %s", tipoCultivo, dias, etapaCrecimiento);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lote{");
        sb.append("id='").append(id).append('\'');
        sb.append(", nombre='").append(nombre).append('\'');
        sb.append(", tipoCultivo='").append(tipoCultivo).append('\'');
        sb.append(", area=").append(area);
        sb.append(", sensores=").append(sensores.size());

        if (fechaSiembra != null) {
            sb.append(", diasDesdeSiembra=").append(calcularDiasDesdeSiembra());
            sb.append(", etapa='").append(etapaCrecimiento).append('\'');
        }

        sb.append('}');
        return sb.toString();
    }
}
