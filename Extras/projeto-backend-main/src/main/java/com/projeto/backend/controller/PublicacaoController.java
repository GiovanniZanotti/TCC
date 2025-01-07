package com.projeto.backend.controller;

import com.projeto.backend.model.Publicacao;
import com.projeto.backend.model.Servico;
import com.projeto.backend.service.PublicacaoService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping(value = "/publicacao")
public class PublicacaoController {

    private final PublicacaoService service;

    public PublicacaoController(PublicacaoService service) {
        this.service = service;
    }

    @GetMapping(value = "/get-publicacoes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Publicacao>> getPublicacoes() {
        return ResponseEntity.ok(service.verPublicacoes());
    }
    
    @PostMapping(value = "/publicar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> publicar(@Valid @RequestBody Publicacao publicacao) {
        return ResponseEntity.ok(service.publicar(publicacao));
    }
    
    @GetMapping(value = "/get-publicacao-by-search/{titulo}")
    public ResponseEntity<List<Publicacao>> getBySearch(@PathVariable String titulo) {
        return ResponseEntity.ok(service.getBySearch(titulo));
    }
}

