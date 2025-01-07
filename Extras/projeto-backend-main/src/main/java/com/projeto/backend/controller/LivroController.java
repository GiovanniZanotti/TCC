package com.projeto.backend.controller;

import com.projeto.backend.model.Livro;
import com.projeto.backend.service.LivroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/livro")
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Livro>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/get-livro/{livroId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Livro> getById(@PathVariable int livroId) {
        return ResponseEntity.ok(service.getById(livroId));
    }

    @GetMapping(value = "/get-biblioteca/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Livro>> getBiblioteca(@PathVariable int usuarioId) {
        return ResponseEntity.ok(service.getBiblioteca(usuarioId));
    }

    @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> cadastrar(@Valid @RequestBody Livro livro) {
        return new ResponseEntity<>(service.cadastrar(livro), HttpStatus.OK);
    }

    @PostMapping(value = "/curtir-livro/{livroId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void CurtirLivro(@PathVariable int livroId) {
        service.curtirLivro(livroId);
    }

    @DeleteMapping(value = "/deletar/{livroId}")
    public ResponseEntity<Boolean> deletar(@PathVariable int livroId) {
        return new ResponseEntity<>(service.deletar(livroId), HttpStatus.OK);
    }
    
    @GetMapping(value = "/get-livro-titulo/{tituloLivro}")
    public ResponseEntity<List<Livro>> getLivroTitulo(@PathVariable String tituloLivro) {
        return ResponseEntity.ok(service.getLivroTitulo(tituloLivro));
    }
}
