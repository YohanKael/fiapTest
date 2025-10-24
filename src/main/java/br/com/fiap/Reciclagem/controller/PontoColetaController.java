package br.com.fiap.Reciclagem.controller;

import br.com.fiap.Reciclagem.model.PontoColeta;
import br.com.fiap.Reciclagem.service.PontoColetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pontos-coleta")
public class PontoColetaController {

    @Autowired
    private PontoColetaService service;

    // GET /pontos-coleta : Lista todos (200 OK)
    @GetMapping
    public ResponseEntity<List<PontoColeta>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    // POST /pontos-coleta : Cadastra um novo ponto (201 Created com Location Header)
    @PostMapping
    public ResponseEntity<PontoColeta> cadastrar(@RequestBody PontoColeta pontoColeta) {
        pontoColeta.setIdPonto(null); // ID será gerado pelo Service Mock
        PontoColeta newPontoColeta = service.salvar(pontoColeta);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPontoColeta.getIdPonto())
                .toUri();

        return ResponseEntity.created(location).body(newPontoColeta);
    }

    // GET /pontos-coleta/{id} : Busca por ID (200 OK ou 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /pontos-coleta/{id} : Atualiza um recurso (200 OK ou 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<PontoColeta> atualizar(@PathVariable Long id, @RequestBody PontoColeta pontoColeta) {
        PontoColeta updatedPonto = service.atualizar(id, pontoColeta);
        if (updatedPonto != null) {
            return ResponseEntity.ok(updatedPonto);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /pontos-coleta/{id} : Deleta um recurso (204 No Content ou 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}