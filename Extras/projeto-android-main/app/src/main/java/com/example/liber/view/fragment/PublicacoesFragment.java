package com.example.liber.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.liber.controller.PublicacaoClickInterface;
import com.example.liber.model.Publicacao;
import com.example.liber.view.activity.PublicacaoActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class PublicacoesFragment extends PesquisarFragment implements PublicacaoClickInterface {

    private static final String TAG = "PublicacoesFragment";

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.loadPublicacaoRecyclerView(PublicacoesFragment.this);
        swipeListener();
        clickListener();
    }

    // Faz o vínculo dos itens da view
    @Override
    protected void bindOnView(View view) {
        super.bindOnView(view);
        tvTitle.setText("Todas as publicações:");
        etSearch.setHint("Pesquisar pelo título");
    }

    // Detecta o movimento de swipe e recarrega a recycler view
    private void swipeListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            super.loadPublicacaoRecyclerView(PublicacoesFragment.this);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Detecta o click do botão de pesquisar
    private void clickListener() {
        btnSearch.setOnClickListener(v -> {
            if (etSearch.getText().toString().equals("")) {
                super.loadPublicacaoRecyclerView(PublicacoesFragment.this);
            } else {
                super.getPublicacaoBySearch(etSearch.getText().toString().trim(), PublicacoesFragment.this);
            }
        });
    }

    // Abre a tela com os detalhes da publicação, quando houver o click
    @Override
    public void onPublicacaoClick(Publicacao publicacao) {
        String livroTexto;
        Intent intent = new Intent(getContext(), PublicacaoActivity.class);
        Gson gson = new Gson();
        livroTexto = gson.toJson(publicacao, Publicacao.class);
        intent.putExtra("livro", livroTexto);
        startActivity(intent);
    }
}
