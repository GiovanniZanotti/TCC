package com.projeto.backend.controller;

import com.projeto.backend.model.Servico;
import com.projeto.backend.service.ServicoService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/servico")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @GetMapping(value = "/get-servicos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Servico>> getServicos() {
        return ResponseEntity.ok(service.verServicos());
    }

    @GetMapping(value = "/get-servicos-by-search/{nome}")
    public ResponseEntity<List<Servico>> getBySearch(@PathVariable String nome) {
        return ResponseEntity.ok(service.getBySearch(nome));
    }
}

