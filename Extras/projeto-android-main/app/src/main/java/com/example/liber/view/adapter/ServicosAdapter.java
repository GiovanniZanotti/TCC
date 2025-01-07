package com.example.liber.view.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liber.R;
import com.example.liber.controller.PesquisarClickInterface;
import com.example.liber.model.Servico;
import com.example.liber.model.Usuario;
import com.example.liber.utils.DrawableUtils;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServicosAdapter extends RecyclerView.Adapter<ServicosAdapter.ServicosViewHolder> {

    private List<Servico> servicos;
    // Interface que irá detectar os clicks do adapter
    private final PesquisarClickInterface pesquisarClickInterface;

    public ServicosAdapter(List<Servico> servicos, PesquisarClickInterface pesquisarClickInterface) {
        this.servicos = servicos;
        this.pesquisarClickInterface = pesquisarClickInterface;
    }

    // Responsável por inflar o layout XML no adapter
    @NonNull
    @NotNull
    @Override
    public ServicosViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.servico_item, parent, false);
        return new ServicosViewHolder(view);
    }

    // Responsável por ligar um determinado item do recycler view com o layout
    @Override
    public void onBindViewHolder(@NonNull @NotNull ServicosAdapter.ServicosViewHolder holder, int position) {
        Servico servico = servicos.get(position);
        holder.bind(servico);
    }

    // Responsável por definir o tamanho da recycler view
    @Override
    public int getItemCount() {
        return servicos.size();
    }

    class ServicosViewHolder extends RecyclerView.ViewHolder {

        TextView tvNome;
        CardView cardView;

        public ServicosViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tv_nome);
            cardView = itemView.findViewById(R.id.card_view);
        }

        // Vincula os dados do item específico da recycler view com a view
        void bind(Servico servico) {
            tvNome.setText(servico.nome);
            clickListener(servico);
        }

        private void clickListener(Servico servico) {
            // Abre o perfil do usuário
            cardView.setOnClickListener(v -> {
                pesquisarClickInterface.onCardClick(servico);
            });
        }
    }
}

