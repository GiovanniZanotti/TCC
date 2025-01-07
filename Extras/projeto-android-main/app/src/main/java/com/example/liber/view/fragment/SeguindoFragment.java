package com.example.liber.view.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.liber.controller.PesquisarClickInterface;
import com.example.liber.model.Servico;
import com.example.liber.model.Usuario;
import com.example.liber.view.activity.MainActivity;

import org.jetbrains.annotations.NotNull;

public class SeguindoFragment extends PesquisarFragment implements PesquisarClickInterface {

    private static final String TAG = "SeguindoFragment";

    private FragmentManager fragmentManager;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.loadSeguindoRecyclerView(MainActivity.getLoggedUsuario().getId(), SeguindoFragment.this);
        swipeListener();
        clickListener();
    }

    // Faz o vínculo dos ítens da view
    @Override
    protected void bindOnView(View view) {
        super.bindOnView(view);
        tvTitle.setText("Quem eu sigo:");
        tvVazio.setText("Você não está seguindo ninguem!");
        fragmentManager = getParentFragmentManager();
    }

    // Detecta o movimento de swipe e recarrega a recycler view
    private void swipeListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            super.loadSeguindoRecyclerView(MainActivity.getLoggedUsuario().getId(), SeguindoFragment.this);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Detecta o click do botão de pesquisar
    private void clickListener() {
        btnSearch.setOnClickListener(v -> {
            String nome = etSearch.getText().toString();
            if (nome.equals("")) {
                super.loadSeguindoRecyclerView(MainActivity.getLoggedUsuario().getId(), SeguindoFragment.this);
            } else {
                super.getSeguindoBySearch(MainActivity.getLoggedUsuario().getId(), nome, SeguindoFragment.this);
            }
        });
    }

    // Abre a tela com os detalhes da publicação, quando houver o click
    @Override
    public void onCardClick(Usuario seguidor) {
        PerfilDialogFragment dialog = new PerfilDialogFragment(seguidor, SeguindoFragment.this);
        dialog.show(fragmentManager, "Dialog");
    }

    @Override
    public void onCardClick(Servico servico) {

    }

    @Override
    public void onSeguindoClick(int seguidorId) {
        super.deixarDeSeguir(seguidorId);
    }

    @Override
    public void onSeguirClick(int seguidorId) {}
}
