package com.projeto.backend.service;

import com.projeto.backend.model.Livro;
import com.projeto.backend.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    private LivroRepository repository;

    public List<Livro> findAll() {
        repository = new LivroRepository();
        return repository.findAll();
    }

    public Livro getById(int id) {
        repository = new LivroRepository();
        return repository.getById(id);
    }

    public List<Livro> getBiblioteca(int id) {
        repository = new LivroRepository();
        return repository.getBiblioteca(id);
    }

    public Boolean cadastrar(Livro livro) {
        repository = new LivroRepository();
        return repository.cadastrar(livro);
    }

    public void curtirLivro(int id) {
        repository = new LivroRepository();
        repository.curtirLivro(id);
    }

    public Boolean deletar(int id) {
        repository = new LivroRepository();
        return repository.deletar(id);
    }

	public List<Livro> getLivroTitulo(String titulo) {
		repository = new LivroRepository();
        return repository.getLivroTitulo(titulo);
	}
}
