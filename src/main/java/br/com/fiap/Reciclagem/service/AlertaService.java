package br.com.fiap.Reciclagem.service;

import br.com.fiap.Reciclagem.model.Alerta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AlertaService {

    // Simulação de armazenamento em memória
    private final Map<Long, Alerta> storage = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Alerta salvar(Alerta alerta) {
        if (alerta.getIdAlerta() == null) {
            alerta.setIdAlerta(idCounter.incrementAndGet());
        }
        storage.put(alerta.getIdAlerta(), alerta);
        return alerta;
    }

    public List<Alerta> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Alerta> buscarPorId(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Alerta atualizar(Long id, Alerta alerta) {
        if (!storage.containsKey(id)) {
            return null;
        }
        alerta.setIdAlerta(id);
        storage.put(id, alerta);
        return alerta;
    }

    public boolean deletar(Long id) {
        return storage.remove(id) != null;
    }

    public void limparBase() {
        storage.clear();
        idCounter.set(0);
    }
}