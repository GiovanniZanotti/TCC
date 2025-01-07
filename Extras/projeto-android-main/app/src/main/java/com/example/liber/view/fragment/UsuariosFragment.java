package com.example.liber.view.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.liber.controller.PesquisarClickInterface;
import com.example.liber.model.Servico;
import com.example.liber.model.Usuario;

import org.jetbrains.annotations.NotNull;

public class UsuariosFragment extends PesquisarFragment implements PesquisarClickInterface {

    private static final String TAG = "UsuariosFragment";

    private FragmentManager fragmentManager;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.loadUsuariosRecyclerView(UsuariosFragment.this);
        swipeListener();
        clickListener();
    }

    // Faz o vínculo dos ítens da view
    @Override
    protected void bindOnView(View view) {
        super.bindOnView(view);
        tvTitle.setText("Todos os usuários:");
        fragmentManager = getParentFragmentManager();
    }

    // Detecta o movimento de swipe e recarrega a recycler view
    private void swipeListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            super.loadUsuariosRecyclerView(UsuariosFragment.this);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Detecta o click do botão de pesquisar
    private void clickListener() {
        btnSearch.setOnClickListener(v -> {
            if (etSearch.getText().toString().equals("")) {
                super.loadUsuariosRecyclerView(UsuariosFragment.this);
            } else {
                super.getUsuarioBySearch(etSearch.getText().toString().trim(), UsuariosFragment.this);
            }
        });
    }

    // Abre a tela com os detalhes da publicação, quando houver o click
    @Override
    public void onCardClick(Usuario usuario) {
        PerfilDialogFragment dialog = new PerfilDialogFragment(usuario, UsuariosFragment.this);
        dialog.show(fragmentManager, "Dialog");
    }

    @java.lang.Override
    public void onCardClick(Servico servico) {

    }

    @Override
    public void onSeguirClick(int seguidorId) {
        super.seguir(seguidorId);
    }

    @Override
    public void onSeguindoClick(int seguidorId) {
        super.deixarDeSeguir(seguidorId);
    }
}
