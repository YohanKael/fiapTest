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
public class Alerta {

    private Long idAlerta;

    private PontoColeta idPonto;

    private String mensagem;

    private LocalDate ultimaAtualizacao;
}
