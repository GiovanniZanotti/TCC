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

public class SeguidoresFragment extends PesquisarFragment implements PesquisarClickInterface {

    private static final String TAG = "SeguidoresFragment";

    private FragmentManager fragmentManager;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.loadSeguidoresRecyclerView(SeguidoresFragment.this);
        swipeListener();
        clickListener();
    }

    // Faz o vínculo dos ítens da view
    @Override
    protected void bindOnView(View view) {
        super.bindOnView(view);
        tvTitle.setText("Meus seguidores:");
        tvVazio.setText("Nenhum seguidor, por enquanto :)");
        fragmentManager = getParentFragmentManager();
    }

    // Detecta o movimento de swipe e recarrega a recycler view
    private void swipeListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            super.loadSeguidoresRecyclerView(SeguidoresFragment.this);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Detecta o click do botão de pesquisar
    private void clickListener() {
        btnSearch.setOnClickListener(v -> {
            if (etSearch.getText().toString().equals("")) {
                super.loadSeguidoresRecyclerView(SeguidoresFragment.this);
            } else {
                super.getSeguidorBySearch(etSearch.getText().toString().trim(), SeguidoresFragment.this);
            }
        });
    }

    // Abre a tela com os detalhes da publicação, quando houver o click
    @Override
    public void onCardClick(Usuario seguidor) {
        PerfilDialogFragment dialog = new PerfilDialogFragment(seguidor, SeguidoresFragment.this);
        dialog.show(fragmentManager, "Dialog");
    }

    @Override
    public void onCardClick(Servico servico) {

    }

    @Override
    public void onSeguirClick(int seguidorId) {}

    @Override
    public void onSeguindoClick(int seguidorId) {}
}
