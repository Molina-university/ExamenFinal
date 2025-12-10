package com.agrosense.service;

import com.agrosense.model.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

public class ToonPersistenceService {

    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = DATA_DIR + "/agrosense_data.toon";

    public void exportarDatos(GestorLotes gestorLotes, AlertaService alertaService) throws IOException {
        StringBuilder sb = new StringBuilder();

        // Export Lotes
        for (Lote lote : gestorLotes.obtenerTodos()) {
            sb.append("LOTE {\n");
            sb.append("  ID: ").append(lote.getId()).append("\n");
            sb.append("  NOMBRE: \"").append(lote.getNombre()).append("\"\n");
            sb.append("  CULTIVO: \"").append(lote.getTipoCultivo()).append("\"\n");
            sb.append("  AREA: ").append(lote.getArea()).append("\n");

            // Nuevos campos agrícolas
            if (lote.getFechaSiembra() != null) {
                sb.append("  FECHA_SIEMBRA: ").append(lote.getFechaSiembra()).append("\n");
            }
            if (lote.getEtapaCrecimiento() != null) {
                sb.append("  ETAPA: \"").append(lote.getEtapaCrecimiento()).append("\"\n");
            }
            if (lote.getNotas() != null && !lote.getNotas().isEmpty()) {
                sb.append("  NOTAS: \"").append(lote.getNotas()).append("\"\n");
            }

            if (!lote.getSensores().isEmpty()) {
                sb.append("  SENSORES {\n");
                for (Sensor sensor : lote.getSensores()) {
                    sb.append("    SENSOR {\n");
                    sb.append("      ID: ").append(sensor.getId()).append("\n");
                    sb.append("      TIPO: ").append(sensor.getTipo()).append("\n");
                    sb.append("      UBICACION: \"").append(sensor.getUbicacion()).append("\"\n");
                    sb.append("    }\n");
                }
                sb.append("  }\n");
            }
            sb.append("}\n");
        }

        // Export Alertas
        for (Alerta alerta : alertaService.getHistorialAlertas()) {
            sb.append("ALERTA {\n");
            sb.append("  NIVEL: ").append(alerta.getNivel()).append("\n");
            sb.append("  MENSAJE: \"").append(alerta.getMensaje()).append("\"\n");
            sb.append("  FECHA: ").append(alerta.getFechaHora()).append("\n");
            sb.append("  LOTE: ").append(alerta.getLoteId()).append("\n");
            sb.append("}\n");
        }

        Files.createDirectories(Paths.get(DATA_DIR));
        Files.writeString(Paths.get(DATA_FILE), sb.toString());
    }

    public AgroSenseData importarDatos() throws IOException {
        if (!Files.exists(Paths.get(DATA_FILE))) {
            throw new FileNotFoundException("No se encontró el archivo TOON");
        }

        String content = Files.readString(Paths.get(DATA_FILE));
        AgroSenseData data = new AgroSenseData();
        data.lotes = new ArrayList<>();
        data.alertas = new ArrayList<>();

        // Parse Lotes - manual parsing to handle nested blocks
        int pos = 0;
        while ((pos = content.indexOf("LOTE {", pos)) != -1) {
            pos += 6; // skip "LOTE {"

            // Find matching closing brace by counting
            int braceCount = 1;
            int endPos = pos;
            while (braceCount > 0 && endPos < content.length()) {
                if (content.charAt(endPos) == '{')
                    braceCount++;
                else if (content.charAt(endPos) == '}')
                    braceCount--;
                endPos++;
            }

            if (braceCount != 0) {
                System.err.println("[ERROR] Malformed LOTE block");
                break;
            }

            String loteBlock = content.substring(pos, endPos - 1);
            String id = extractValue(loteBlock, "ID");
            String nombre = extractValue(loteBlock, "NOMBRE");
            String cultivo = extractValue(loteBlock, "CULTIVO");
            double area = Double.parseDouble(extractValue(loteBlock, "AREA"));

            Lote lote = new Lote(id, nombre, cultivo, area);

            // Parsear nuevos campos agrícolas
            String fechaSiembraStr = extractValue(loteBlock, "FECHA_SIEMBRA");
            if (!fechaSiembraStr.isEmpty()) {
                lote.setFechaSiembra(LocalDate.parse(fechaSiembraStr));
            }

            String etapa = extractValue(loteBlock, "ETAPA");
            if (!etapa.isEmpty()) {
                lote.setEtapaCrecimiento(etapa);
            }

            String notas = extractValue(loteBlock, "NOTAS");
            if (!notas.isEmpty()) {
                lote.setNotas(notas);
            }

            // Parse Sensors within Lote
            Pattern sensorPattern = Pattern.compile("SENSOR \\{(.*?)\\}", Pattern.DOTALL);
            Matcher sensorMatcher = sensorPattern.matcher(loteBlock);

            int sensorCount = 0;
            while (sensorMatcher.find()) {
                String sensorBlock = sensorMatcher.group(1);
                String sId = extractValue(sensorBlock, "ID");
                String sTipo = extractValue(sensorBlock, "TIPO");
                String sUbicacion = extractValue(sensorBlock, "UBICACION");

                System.out.println("[PARSER] Encontrado sensor: ID=" + sId + ", TIPO=" + sTipo + ", LOC=" + sUbicacion);

                if ("HUMEDAD".equals(sTipo)) {
                    lote.agregarSensor(new SensorHumedad(sId, sUbicacion));
                } else {
                    lote.agregarSensor(new SensorTemperatura(sId, sUbicacion));
                }
                sensorCount++;
            }
            System.out.println("[PARSER] Lote " + id + " cargó " + sensorCount + " sensores");
            data.lotes.add(lote);

            pos = endPos;
        }

        // Parse Alertas
        Pattern alertaPattern = Pattern.compile("ALERTA \\{(.*?)\\}", Pattern.DOTALL);
        Matcher alertaMatcher = alertaPattern.matcher(content);

        while (alertaMatcher.find()) {
            String alertaBlock = alertaMatcher.group(1);
            String nivelStr = extractValue(alertaBlock, "NIVEL");
            String mensaje = extractValue(alertaBlock, "MENSAJE");
            String fechaStr = extractValue(alertaBlock, "FECHA");
            String loteId = extractValue(alertaBlock, "LOTE");

            Alerta.Nivel nivel = Alerta.Nivel.valueOf(nivelStr);
            LocalDateTime fecha = LocalDateTime.parse(fechaStr);

            data.alertas.add(new Alerta(mensaje, nivel, loteId, fecha));
        }

        return data;
    }

    private String extractValue(String block, String key) {
        Pattern p = Pattern.compile(key + ":\\s*\"?(.*?)\"?\\s*(\\n|$|\\r)");
        Matcher m = p.matcher(block);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "";
    }

    // Data container class
    public static class AgroSenseData {
        public List<Lote> lotes;
        public List<Alerta> alertas;
    }
}
