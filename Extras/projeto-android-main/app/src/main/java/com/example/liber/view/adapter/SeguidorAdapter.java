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
import com.example.liber.model.Usuario;
import com.example.liber.utils.DrawableUtils;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeguidorAdapter extends RecyclerView.Adapter<SeguidorAdapter.SeguidorViewHolder> {

    private List<Usuario> seguidores;
    // Interface que irá detectar os clicks do adapter
    private final PesquisarClickInterface pesquisarClickInterface;
    private Boolean ativarBtn = false;

    public void setAtivarBtn(Boolean ativarBtn) {
        this.ativarBtn = ativarBtn;
    }

    public SeguidorAdapter(List<Usuario> seguidores, PesquisarClickInterface pesquisarClickInterface) {
        this.seguidores = seguidores;
        this.pesquisarClickInterface = pesquisarClickInterface;
    }

    // Responsável por inflar o layout XML no adapter
    @NonNull
    @NotNull
    @Override
    public SeguidorViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seguidor_item, parent, false);
        return new SeguidorViewHolder(view);
    }

    // Responsável por ligar um determinado item do recycler view com o layout
    @Override
    public void onBindViewHolder(@NonNull @NotNull SeguidorAdapter.SeguidorViewHolder holder, int position) {
        Usuario seguidor = seguidores.get(position);
        holder.bind(seguidor);
    }

    // Responsável por definir o tamanho da recycler view
    @Override
    public int getItemCount() {
        return seguidores.size();
    }

    class SeguidorViewHolder extends RecyclerView.ViewHolder {

        TextView tvIcon, tvNome, tvEmail;
        CardView cardView;
        MaterialButton btnDeixarDeSeguir;

        public SeguidorViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_icon);
            tvNome = itemView.findViewById(R.id.tv_nome);
            tvEmail = itemView.findViewById(R.id.tv_email);
            cardView = itemView.findViewById(R.id.card_view);
            btnDeixarDeSeguir = itemView.findViewById(R.id.btn_deixar_de_seguir);
            // Mostra o botão de deixar de seguir, se necessário
            if (ativarBtn) {
                btnDeixarDeSeguir.setVisibility(View.VISIBLE);
            }
        }

        // Vincula os dados do item específico da recycler view com a view
        void bind(Usuario seguidor) {
            int hash = seguidor.getNome().hashCode();
            tvIcon.setText(String.valueOf(seguidor.getNome().charAt(0)));
            tvIcon.setBackground(DrawableUtils.oval(Color.rgb(hash, hash / 2, 0), tvIcon));
            tvNome.setText(seguidor.getNome());
            tvEmail.setText(seguidor.getEmail());
            clickListener(seguidor);
        }

        private void clickListener(Usuario seguidor) {
            // Abre o perfil do usuário
            cardView.setOnClickListener(v -> {
                pesquisarClickInterface.onCardClick(seguidor);
            });

            // Remove o usuário da lista de seguidores
            btnDeixarDeSeguir.setOnClickListener(v -> {
                for (int i = 0; i < seguidores.size(); i++) {
                    if (seguidores.get(i).getId() == seguidor.getId()) {
                        seguidores.remove(i);
                        break;
                    }
                }
                pesquisarClickInterface.onSeguindoClick(seguidor.getId());
            });
        }
    }
}
