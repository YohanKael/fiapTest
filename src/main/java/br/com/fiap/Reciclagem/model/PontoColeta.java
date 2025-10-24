package br.com.fiap.Reciclagem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Removidas anotações JPA: @Entity, @Table, @Id, @GeneratedValue, @Column
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
