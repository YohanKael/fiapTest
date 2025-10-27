package br.com.fiap.Reciclagem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Este modelo replica a estrutura da entidade principal para uso no DTO de testes.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoColeta {

    private Long idPonto;
    private String nome;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
