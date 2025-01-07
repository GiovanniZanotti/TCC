package com.projeto.backend.service;

import com.projeto.backend.model.Usuario;
import com.projeto.backend.repository.SeguidorRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeguidorService {

    private SeguidorRepository repository;
    
    public boolean seguir(int idUsuario, int idUsuarioASeguir) {
    	repository = new SeguidorRepository();
    	return repository.seguir(idUsuario, idUsuarioASeguir);
    }
    
    public boolean deixarDeSeguir(int idUsuario, int idUsuarioDesseguir) {
    	repository = new SeguidorRepository();
    	return repository.deixarDeSeguir(idUsuario, idUsuarioDesseguir);
    }
    
    public List<Usuario> verSeguindo(int idUsuario){
    	repository = new SeguidorRepository();
    	return repository.verSeguindo(idUsuario);
    }
    
    public List<Usuario> verSeguidores(int idUsuario){
    	repository = new SeguidorRepository();
    	return repository.verSeguidores(idUsuario);
    }

    public int getSeguidoresCount(int usuarioId) {
        repository = new SeguidorRepository();
        return repository.getSeguidoresCount(usuarioId);
    }

    public List<Usuario> getBySearch(int usuarioId, String nome) {
        repository = new SeguidorRepository();
        return repository.getBySearch(usuarioId, nome);
    }
    
    public List<Usuario> getSeguindoBySearch(int usuarioId, String nome) {
    	repository = new SeguidorRepository();
        return repository.getSeguindoBySearch(usuarioId, nome);
    }
}
