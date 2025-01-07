package com.projeto.backend.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.backend.model.Usuario;
import com.projeto.backend.service.BibliotecaService;
import com.projeto.backend.service.UsuarioService;

@CrossOrigin
@RestController
@RequestMapping(value = "/biblioteca")
public class BibliotecaController {

    private final BibliotecaService service;

    public BibliotecaController(BibliotecaService service) {
        this.service = service;
    }

    @PostMapping(value = "/adicionarLivro/{usuarioId}/{livroId}")
    public ResponseEntity<Boolean> adicionarLivro(@PathVariable int usuarioId, @PathVariable int livroId) {
        return ResponseEntity.ok(service.adicionarLivro(usuarioId, livroId));
    }
    
    @PostMapping(value = "/removerLivro/{usuarioId}/{livroId}")
    public ResponseEntity<Boolean> removerLivro(@PathVariable int usuarioId, @PathVariable int livroId) {
        return ResponseEntity.ok(service.removerLivro(usuarioId, livroId));
    }

    @GetMapping(value = "/getLivro/{usuarioId}/{livroId}")
    public ResponseEntity<Boolean> cadastrar(@PathVariable int usuarioId, @PathVariable int livroId) {
        return ResponseEntity.ok(service.getLivro(usuarioId, livroId));
    }

}