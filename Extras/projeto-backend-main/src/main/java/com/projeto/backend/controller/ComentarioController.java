package com.projeto.backend.controller;

import com.projeto.backend.model.Comentario;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.service.ComentarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/comentario")
public class ComentarioController {

    private final ComentarioService service;

    public ComentarioController (ComentarioService service) {
        this.service = service;
    }

    @GetMapping(value = "/get-comentario/{comentarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comentario> getById(@PathVariable int comentarioId) {
        return ResponseEntity.ok(service.getById(comentarioId));
    }

    @GetMapping(value = "/all-from-usuario/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comentario>> getComentariosUsuario(@PathVariable int usuarioId) {
        return ResponseEntity.ok(service.getComentariosUsuario(usuarioId));
    }

    @GetMapping(value = "/all-from-publicacao/{publicacaoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comentario>> getComentariosLivro(@PathVariable int publicacaoId) {
        return ResponseEntity.ok(service.getComentariosPublicacao(publicacaoId));
    }

    @PostMapping(value = "/inserir", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> inserirComentario(@Valid @RequestBody Comentario comentario) {
        return new ResponseEntity<>(service.inserirComentario(comentario), HttpStatus.OK);
    }

    @DeleteMapping(value = "/deletar/{comentarioId}")
    public ResponseEntity<Boolean> excluirComentario(@PathVariable int comentarioId) {
        return new ResponseEntity<>(service.excluirComentario(comentarioId), HttpStatus.OK);
    }
    
    @GetMapping(value = "/getUsuariosDosComentarios/{livroId}")
    public ResponseEntity<List<Usuario>> getUsuariosDosComentarios(@PathVariable int livroId) {
        return ResponseEntity.ok(service.getUsuariosDosComentarios(livroId));
    }
    
    
}
