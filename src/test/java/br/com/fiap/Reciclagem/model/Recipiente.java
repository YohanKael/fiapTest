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
    private Long idRecipiente;
    private Long idPontoColeta;
    private Long idMaterial;
    private Double capacidadeMax;
    private Double volumeAtual;
    private LocalDate ultimaAtualizacao;
}