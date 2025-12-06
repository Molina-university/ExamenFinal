package com.agrosense.service;

import com.agrosense.model.Lote;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorLotes {
    private List<Lote> lotes;

    public GestorLotes() {
        this.lotes = new ArrayList<>();
    }

    public void registrarLote(Lote lote) {
        if (buscarPorId(lote.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un lote con ID: " + lote.getId());
        }
        lotes.add(lote);
    }

    public void actualizarLote(Lote lote) {
        Optional<Lote> existente = buscarPorId(lote.getId());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un lote con ID: " + lote.getId());
        }
        lotes.remove(existente.get());
        lotes.add(lote);
    }

    public boolean eliminarLote(String id) {
        return lotes.removeIf(l -> l.getId().equals(id));
    }

    public List<Lote> obtenerTodos() {
        return lotes;
    }

    public Optional<Lote> buscarPorId(String id) {
        return lotes.stream().filter(l -> l.getId().equals(id)).findFirst();
    }

    public void limpiar() {
        lotes.clear();
    }
}
