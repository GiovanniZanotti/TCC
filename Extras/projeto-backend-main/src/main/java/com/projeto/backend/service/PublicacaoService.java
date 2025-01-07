package com.projeto.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projeto.backend.model.Publicacao;
import com.projeto.backend.model.Servico;
import com.projeto.backend.repository.PublicacaoRepository;
import com.projeto.backend.repository.ServicoRepository;

@Service
public class PublicacaoService {
	
	private PublicacaoRepository repository;
    
    public List<Publicacao> verPublicacoes(){
    	repository = new PublicacaoRepository();
    	return repository.verPublicacoes();
    }
    
    public boolean publicar(Publicacao publicacao) {
        repository = new PublicacaoRepository();
        
        return repository.publicar(publicacao);
    }
    
    public List<Publicacao> getBySearch(String titulo) {
        repository = new PublicacaoRepository();
        return repository.getBySearch(titulo);
    }
}

