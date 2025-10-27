package br.com.fiap.Reciclagem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipiente {
    private Long idRecipiente;                 // ID retornado pela API
    private Long idPontoColeta;      // Relacionamento
    private Long idMaterial;         // Relacionamento
    private Double capacidadeMax;    // Máxima capacidade do recipiente
    private Double volumeAtual;      // Volume atual do recipiente
    private LocalDate ultimaAtualizacao; // Última atualização
}