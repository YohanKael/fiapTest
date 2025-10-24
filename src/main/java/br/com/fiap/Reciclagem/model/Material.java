package br.com.fiap.Reciclagem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Removidas anotações JPA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    private Long idMaterial;
    private String nomeMaterial;
}
