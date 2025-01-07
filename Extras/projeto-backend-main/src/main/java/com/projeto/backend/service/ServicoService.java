package com.projeto.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projeto.backend.model.Servico;
import com.projeto.backend.repository.ServicoRepository;

@Service
public class ServicoService {
	
	private ServicoRepository repository;
    
    public List<Servico> verServicos(){
    	repository = new ServicoRepository();
    	return repository.verServicos();
    }

    public List<Servico> getBySearch(String nome) {
        repository = new ServicoRepository();
        return repository.getBySearch(nome);
    }
}
