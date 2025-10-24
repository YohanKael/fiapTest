package br.com.fiap.Reciclagem.controller;

import br.com.fiap.Reciclagem.model.Material;
import br.com.fiap.Reciclagem.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    @Autowired
    private MaterialService service;

    // GET /materiais : Lista todos (200 OK)
    @GetMapping
    public ResponseEntity<List<Material>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    // POST /materiais : Cadastra novo (201 Created com Location Header)
    @PostMapping
    public ResponseEntity<Material> cadastrar(@RequestBody Material material) {
        material.setIdMaterial(null); // ID será gerado pelo Service Mock
        Material novoMaterial = service.salvar(material);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoMaterial.getIdMaterial())
                .toUri();

        return ResponseEntity.created(location).body(novoMaterial);
    }

    // GET /materiais/{id} : Busca por ID (200 OK ou 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<Material> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /materiais/{id} : Atualiza (200 OK ou 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<Material> atualizar(@PathVariable Long id, @RequestBody Material material) {
        Material updatedMaterial = service.atualizar(id, material);
        if (updatedMaterial != null) {
            return ResponseEntity.ok(updatedMaterial);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /materiais/{id} : Deleta (204 No Content ou 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
