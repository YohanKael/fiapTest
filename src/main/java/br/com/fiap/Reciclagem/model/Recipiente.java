package br.com.fiap.Reciclagem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Removidas anotações JPA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipiente {

    private Long idRecipiente;
    private PontoColeta pontoColeta;
    private Material material;
    private Double capacidadeMax;
    private Double volumeAtual;
    private LocalDate ultimaAtualizacao;
}
