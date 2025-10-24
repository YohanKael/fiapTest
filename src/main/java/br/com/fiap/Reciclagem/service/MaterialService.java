package br.com.fiap.Reciclagem.service;

import br.com.fiap.Reciclagem.model.Material;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MaterialService {

    // Simulação de armazenamento em memória
    private final Map<Long, Material> storage = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Material salvar(Material material) {
        if (material.getIdMaterial() == null) {
            material.setIdMaterial(idCounter.incrementAndGet());
        }
        storage.put(material.getIdMaterial(), material);
        return material;
    }

    public List<Material> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Material> buscarPorId(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Material atualizar(Long id, Material material) {
        if (!storage.containsKey(id)) {
            return null;
        }
        material.setIdMaterial(id);
        storage.put(id, material);
        return material;
    }

    public boolean deletar(Long id) {
        return storage.remove(id) != null;
    }

    public void limparBase() {
        storage.clear();
        idCounter.set(0);
    }
}