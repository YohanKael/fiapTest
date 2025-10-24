package br.com.fiap.Reciclagem.service;

import br.com.fiap.Reciclagem.model.Alerta;
import br.com.fiap.Reciclagem.model.PontoColeta;
import br.com.fiap.Reciclagem.model.Recipiente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RecipienteService {

    // Repositórios/Services MOCK necessários para lógica de alerta
    @Autowired
    private AlertaService alertaService;

    @Autowired
    private PontoColetaService pontoColetaService;

    // Simulação de armazenamento em memória para Recipientes
    private final Map<Long, Recipiente> storage = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    // --- Métodos CRUD ---

    public Recipiente salvar(Recipiente recipiente) {
        if (isCapacidadeInvalida(recipiente)) {
            throw new IllegalArgumentException("Capacidade máxima inválida ou ausente. Deve estar entre 0 e 100.");
        }
        if (recipiente.getIdRecipiente() == null) {
            recipiente.setIdRecipiente(idCounter.incrementAndGet());
        }

        storage.put(recipiente.getIdRecipiente(), recipiente);
        verificarVolumeEEmitirAlerta(recipiente);
        return recipiente;
    }

    public Optional<Recipiente> buscarPorId(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Recipiente> buscarTodos() {
        return new ArrayList<>(storage.values());
    }

    public Recipiente atualizar(Long id, Recipiente recipiente) {
        if (!storage.containsKey(id)) {
            return null; // Não encontrado
        }
        if (isCapacidadeInvalida(recipiente)) {
            throw new IllegalArgumentException("Capacidade máxima inválida ou ausente. Deve estar entre 0 e 100.");
        }
        recipiente.setIdRecipiente(id);
        storage.put(id, recipiente);
        verificarVolumeEEmitirAlerta(recipiente);
        return recipiente;
    }

    public boolean deletar(Long id) {
        return storage.remove(id) != null;
    }

    // --- Lógica de Negócio (Alertas) ---

    private boolean isCapacidadeInvalida(Recipiente recipiente) {
        // Validação da regra de negócio: capacidade entre 0 e 100
        return recipiente.getCapacidadeMax() == null || recipiente.getCapacidadeMax() <= 0 || recipiente.getCapacidadeMax() > 100;
    }

    private void verificarVolumeEEmitirAlerta(Recipiente recipiente) {
        if (recipiente.getCapacidadeMax() == null || recipiente.getCapacidadeMax() <= 0) return;

        double porcentagem = (recipiente.getVolumeAtual() / recipiente.getCapacidadeMax()) * 100;

        if (porcentagem > 70) {
            Long idPonto = recipiente.getPontoColeta() != null ? recipiente.getPontoColeta().getIdPonto() : null;

            // Busca o PontoColeta no Service Mock
            Optional<PontoColeta> pontoColetaOpt = pontoColetaService.buscarPorId(idPonto);

            if (pontoColetaOpt.isPresent()) {
                PontoColeta pontoColeta = pontoColetaOpt.get();
                Alerta alerta = Alerta.builder()
                        .idPonto(pontoColeta)
                        .mensagem("Recipiente " + recipiente.getIdRecipiente() +
                                " atingiu " + Math.round(porcentagem) + "% da capacidade no ponto: " +
                                pontoColeta.getNome())
                        .ultimaAtualizacao(LocalDate.now())
                        .build();

                alertaService.salvar(alerta); // Persiste o alerta no Service Mock de Alerta
            }
        }
    }

    public List<Alerta> buscarAlertas() {
        return alertaService.buscarTodos();
    }

    /**
     * Método auxiliar para limpar o MockDatabase nos testes BDD.
     */
    public void limparBase() {
        storage.clear();
        idCounter.set(0);
        // Não limpamos Alertas e Pontos aqui, pois faremos isso em um Contexto de Teste separado.
    }
}
