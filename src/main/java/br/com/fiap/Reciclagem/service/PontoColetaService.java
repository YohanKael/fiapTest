package br.com.fiap.Reciclagem.service;

import br.com.fiap.Reciclagem.model.PontoColeta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PontoColetaService {

    // Simulação de armazenamento em memória
    private final Map<Long, PontoColeta> storage = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    /**
     * Salva ou atualiza um Ponto de Coleta.
     * Se o ID for nulo, gera um novo ID.
     */
    public PontoColeta salvar(PontoColeta pontoColeta) {
        if (pontoColeta.getIdPonto() == null) {
            pontoColeta.setIdPonto(idCounter.incrementAndGet());
        }
        // Lógica de negócio/validação pode ir aqui
        storage.put(pontoColeta.getIdPonto(), pontoColeta);
        return pontoColeta;
    }

    /**
     * Busca todos os Pontos de Coleta.
     */
    public List<PontoColeta> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Busca um Ponto de Coleta por ID.
     */
    public Optional<PontoColeta> buscarPorId(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Atualiza um Ponto de Coleta existente.
     */
    public PontoColeta atualizar(Long id, PontoColeta pontoColeta) {
        if (!storage.containsKey(id)) {
            return null; // Não encontrado
        }
        pontoColeta.setIdPonto(id);
        storage.put(id, pontoColeta);
        return pontoColeta;
    }

    /**
     * Deleta um Ponto de Coleta.
     */
    public boolean deletar(Long id) {
        return storage.remove(id) != null;
    }

    /**
     * Metodo auxiliar para limpar o MockDatabase nos testes BDD.
     */
    public void limparBase() {
        storage.clear();
        idCounter.set(0);
    }
}