package com.projeto.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projeto.backend.model.Livro;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.repository.BibliotecaRepository;

@Service
public class BibliotecaService {

    private BibliotecaRepository repository;

    public boolean adicionarLivro(int usuarioId, int livroId) {
        repository = new BibliotecaRepository();
        return repository.adicionarLivro(usuarioId, livroId);
    }
    
    public boolean removerLivro(int usuarioId, int livroId) {
        repository = new BibliotecaRepository();
        return repository.removerLivro(usuarioId, livroId);
    }
    
    public boolean getLivro(int usuarioId, int livroId) {
        repository = new BibliotecaRepository();
        return repository.getLivro(usuarioId, livroId);
    }
    
}
