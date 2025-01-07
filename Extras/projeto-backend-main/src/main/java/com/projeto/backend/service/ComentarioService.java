package com.projeto.backend.service;

import com.projeto.backend.model.Comentario;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.repository.ComentarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    private ComentarioRepository repository;

    public Comentario getById(int id) {
        repository = new ComentarioRepository();
        return repository.getById(id);
    }

    public List<Comentario> getComentariosUsuario(int id) {
        repository = new ComentarioRepository();
        return repository.getComentariosUsuario(id);
    }

    public List<Comentario> getComentariosPublicacao(int id) {
        repository = new ComentarioRepository();
        return repository.getComentariosPublicacao(id);
    }

    public Boolean inserirComentario(Comentario comentario) {
        repository = new ComentarioRepository();
        return repository.inserirComentario(comentario);
    }

    public Boolean excluirComentario(int id) {
        repository = new ComentarioRepository();
        return repository.excluirComentario(id);
    }
    
    public List<Usuario> getUsuariosDosComentarios(int livroId){
    	repository = new ComentarioRepository();
        return repository.getUsuariosDosComentarios(livroId);
    }
}
