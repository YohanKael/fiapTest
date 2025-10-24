package br.com.fiap.Reciclagem.controller;

import br.com.fiap.Reciclagem.model.Alerta;
import br.com.fiap.Reciclagem.model.Recipiente;
import br.com.fiap.Reciclagem.service.RecipienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/recipientes")
public class RecipienteController {

    @Autowired
    private RecipienteService service;

    // GET /recipientes : Lista todos (200 OK)
    @GetMapping
    public ResponseEntity<List<Recipiente>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    // POST /recipientes : Cadastra novo (201 Created)
    @PostMapping
    public ResponseEntity<Recipiente> cadastrar(@RequestBody Recipiente recipiente) {
        try {
            Recipiente novoRecipiente = service.salvar(recipiente);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(novoRecipiente.getIdRecipiente())
                    .toUri();

            return ResponseEntity.created(location).body(novoRecipiente);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request em caso de validação (ex: capacidade inválida)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // GET /recipientes/{id} : Busca por ID (200 OK ou 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<Recipiente> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /recipientes/{id} : Atualiza (200 OK ou 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<Recipiente> atualizar(@PathVariable Long id, @RequestBody Recipiente recipiente) {
        try {
            Recipiente atualizado = service.atualizar(id, recipiente);
            if (atualizado != null) {
                return ResponseEntity.ok(atualizado);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request em caso de validação
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // DELETE /recipientes/{id} : Deleta (204 No Content ou 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint para buscar todos os alertas gerados (Útil para testes!)
    @GetMapping("/alertas")
    public ResponseEntity<List<Alerta>> buscarAlertas() {
        return ResponseEntity.ok(service.buscarAlertas());
    }
}
