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

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private final List<Usuario> usuarios;
    private List<Usuario> seguindo;
    // Interface que irá detectar os clicks do adapter
    private final PesquisarClickInterface pesquisarClickInterface;

    public UsuarioAdapter(List<Usuario> usuarios, List<Usuario> seguindo, PesquisarClickInterface pesquisarClickInterface) {
        this.usuarios = usuarios;
        this.seguindo = seguindo;
        this.pesquisarClickInterface = pesquisarClickInterface;
    }

    // Responsável por inflar o layout XML no adapter
    @NonNull
    @NotNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_item, parent, false);
        return new UsuarioViewHolder(view);
    }

    // Responsável por ligar um determinado item do recycler view com o layout
    @Override
    public void onBindViewHolder(@NonNull @NotNull UsuarioAdapter.UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario, seguindo);
    }

    // Responsável por definir o tamanho da recycler view
    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView tvIcon, tvNome, tvEmail;
        MaterialButton btnSeguir;
        CardView cardView;

        UsuarioViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_icon);
            tvNome = itemView.findViewById(R.id.tv_nome);
            tvEmail = itemView.findViewById(R.id.tv_email);
            btnSeguir = itemView.findViewById(R.id.btn_seguir);
            cardView = itemView.findViewById(R.id.card_view);
        }

        // Vincula os dados do item específico da recycler view com a view
        void bind(Usuario usuario, List<Usuario> seguindo) {
            for (Usuario seguidor : seguindo) {
                if (seguidor.getId() == usuario.getId()) {
                    usuario.setFollowed(true);
                }
            }
            int hash = usuario.getNome().hashCode();
            tvIcon.setText(String.valueOf(usuario.getNome().charAt(0)));
            tvIcon.setBackground(DrawableUtils.oval(Color.rgb(hash, hash / 2, 0), tvIcon));
            tvNome.setText(usuario.getNome());
            tvEmail.setText(usuario.getEmail());
            btnSeguir.setText(setBtnText(usuario.isFollowed()));
            clickListener(usuario);
        }
        
        private void clickListener(Usuario usuario) {
            // Detecta o click do botão de seguir
            btnSeguir.setOnClickListener(v -> {
                // Detecta se o usuário já está seguindo e remove da lista de seguidores
                if (usuario.isFollowed()) {
                    for (int i = 0; i < seguindo.size(); i++) {
                        if (seguindo.get(i).getId() == usuario.getId()) {
                            seguindo.remove(i);
                            break;
                        }
                    }
                    usuario.setFollowed(!usuario.isFollowed());
                    pesquisarClickInterface.onSeguindoClick(usuario.getId());
                }
                // Se não estiver seguindo, adiciona o usuário na lista
                else {
                    seguindo.add(usuario);
                    pesquisarClickInterface.onSeguirClick(usuario.getId());
                }
                btnSeguir.setText(setBtnText(usuario.isFollowed()));
            });

            // Abre o perfil do usuário
            cardView.setOnClickListener(v ->
                pesquisarClickInterface.onCardClick(usuario)
            );
        }

        // Define o texto do botão de seguir
        private String setBtnText(boolean followed) { return followed ? "SEGUINDO" : "SEGUIR"; }
    }
}
