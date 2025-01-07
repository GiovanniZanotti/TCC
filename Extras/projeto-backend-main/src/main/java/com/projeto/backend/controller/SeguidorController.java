package com.projeto.backend.controller;

import com.projeto.backend.model.Usuario;
import com.projeto.backend.service.SeguidorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/seguidor")
public class SeguidorController {

    private final SeguidorService service;

    public SeguidorController(SeguidorService service) {
        this.service = service;
    }

    @GetMapping(value = "/get-seguindo/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Usuario>> getSeguindo(@PathVariable int usuarioId) {
        return ResponseEntity.ok(service.verSeguindo(usuarioId));
    }

    @GetMapping(value = "/get-seguidores/{usuarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Usuario>> getSeguidores(@PathVariable int usuarioId) {
        return ResponseEntity.ok(service.verSeguidores(usuarioId));
    }

    @GetMapping(value = "/get-seguidores-count/{usuarioId}")
    public ResponseEntity<Integer> getSeguidoresCount(@PathVariable int usuarioId) {
        return ResponseEntity.ok(service.getSeguidoresCount(usuarioId));
    }

    @GetMapping(value = "/get-seguidores-by-search/{usuarioId}/{nome}")
    public ResponseEntity<List<Usuario>> getBySearch(@PathVariable int usuarioId, @PathVariable String nome) {
        return ResponseEntity.ok(service.getBySearch(usuarioId, nome));
    }
	
	@PostMapping(value = "/seguir/{usuarioId}/{seguidorId}")
    public ResponseEntity<Boolean> seguir(@PathVariable int usuarioId, @PathVariable int seguidorId) {
        return new ResponseEntity<>(service.seguir(usuarioId, seguidorId), HttpStatus.OK);
    }
	
	@DeleteMapping (value = "/deixarDeSeguir/{usuarioId}/{seguidorId}")
    public ResponseEntity<Boolean> deixarDeSeguir(@PathVariable int usuarioId, @PathVariable int seguidorId) {
        return new ResponseEntity<>(service.deixarDeSeguir(usuarioId, seguidorId), HttpStatus.OK);
    }
	
	@GetMapping (value = "/get-seguindo-by-search/{usuarioId}/{nome}")
	public ResponseEntity<List<Usuario>> getSeguindoBySearch(@PathVariable int usuarioId, @PathVariable String nome) {
		return ResponseEntity.ok(service.getSeguindoBySearch(usuarioId, nome));
	}
}
