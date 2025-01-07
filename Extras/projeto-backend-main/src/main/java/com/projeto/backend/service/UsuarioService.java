package com.projeto.backend.service;

import com.projeto.backend.model.Usuario;
import com.projeto.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private UsuarioRepository repository;

    public List<Usuario> findAll() {
        repository = new UsuarioRepository();
        return repository.findAll();
    }

    public Usuario login(String email, String senha) {
        repository = new UsuarioRepository();
        return repository.login(email, senha);
    }

    public Usuario getById(int id) {
        repository = new UsuarioRepository();
        return repository.getById(id);
    }

    public List<Usuario> getSeguidores(int id) {
        repository = new UsuarioRepository();
        return repository.getSeguidores(id);
    }

    public Usuario cadastrar(Usuario usuario) {
        repository = new UsuarioRepository();
        repository.cadastrar(usuario, java.sql.Date.valueOf(LocalDate.now()).toString());
        return repository.login(usuario.getEmail(), usuario.getSenha());
    }

    public Boolean deletar(int id) {
        repository = new UsuarioRepository();
        return repository.deletar(id);
    }

    public List<Usuario> getBySearch(String nome) {
        repository = new UsuarioRepository();
        return repository.getBySearch(nome);
    }
}
