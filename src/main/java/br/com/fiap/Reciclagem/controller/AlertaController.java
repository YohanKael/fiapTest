package br.com.fiap.Reciclagem.controller;

import br.com.fiap.Reciclagem.model.Alerta;
import br.com.fiap.Reciclagem.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/alertas")
public class AlertaController {

    @Autowired
    private AlertaService service;

    // GET /alertas : Lista todos (200 OK)
    @GetMapping
    public ResponseEntity<List<Alerta>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    // POST /alertas : Cadastra novo (201 Created com Location Header)
    @PostMapping
    public ResponseEntity<Alerta> cadastrar(@RequestBody Alerta alerta) {
        alerta.setIdAlerta(null); // ID será gerado pelo Service Mock
        Alerta novoAlerta = service.salvar(alerta);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoAlerta.getIdAlerta())
                .toUri();

        return ResponseEntity.created(location).body(novoAlerta);
    }

    // GET /alertas/{id} : Busca por ID (200 OK ou 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<Alerta> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /alertas/{id} : Atualiza (200 OK ou 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<Alerta> atualizar(@PathVariable Long id, @RequestBody Alerta alerta) {
        Alerta atualizado = service.atualizar(id, alerta);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /alertas/{id} : Deleta (204 No Content ou 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}