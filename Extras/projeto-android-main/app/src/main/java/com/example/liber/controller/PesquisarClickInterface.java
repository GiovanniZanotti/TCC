package com.example.liber.controller;

import com.example.liber.model.Servico;
import com.example.liber.model.Usuario;

public interface PesquisarClickInterface {
    void onSeguirClick(int seguidorId);
    void onSeguindoClick(int seguidorId);
    void onCardClick(Usuario usuario);
    void onCardClick(Servico servico);
}
