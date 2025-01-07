package com.projeto.backend.controller;

import com.projeto.backend.model.Usuario;
import com.projeto.backend.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Usuario>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/login/{email}/{senha}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> login(@PathVariable String email, @PathVariable String senha) {
        return ResponseEntity.ok(service.login(email, senha));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping(value = "/search/{nome}")
    public ResponseEntity<List<Usuario>> getBySearch(@PathVariable String nome) {
        return ResponseEntity.ok(service.getBySearch(nome));
    }

    @GetMapping(value = "/get-seguidores/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Usuario>> getSeguidores(@PathVariable int id) {
        return ResponseEntity.ok(service.getSeguidores(id));
    }

    @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(service.cadastrar(usuario));
    }

    @DeleteMapping(value = "/deletar/{id}")
    public ResponseEntity<Boolean> deletar(@PathVariable int id) {
        return new ResponseEntity<>(service.deletar(id), HttpStatus.OK);
    }
}
