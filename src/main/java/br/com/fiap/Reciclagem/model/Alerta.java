package br.com.fiap.Reciclagem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Removidas anotações JPA: @Entity, @Table, @ManyToOne, @JoinColumn
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    private Long idAlerta;

    // Relacionamento agora é tratado como simples POJO
    private PontoColeta idPonto;

    private String mensagem;

    private LocalDate ultimaAtualizacao;
}