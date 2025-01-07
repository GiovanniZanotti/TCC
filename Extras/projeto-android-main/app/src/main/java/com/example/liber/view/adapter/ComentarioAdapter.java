package com.example.liber.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liber.R;
import com.example.liber.model.Comentario;
import com.example.liber.model.Usuario;
import com.example.liber.utils.DrawableUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>{

    private List<Comentario> comentarios;
    private List<Usuario> usuariosDosComentarios;
    private Context applicationContext;

    public ComentarioAdapter(List<Comentario> comentarios, List<Usuario> usuariosDosComentarios, Context applicationContext) {
        this.comentarios = comentarios;
        this.usuariosDosComentarios = usuariosDosComentarios;
        this.applicationContext = applicationContext;
    }

    // Responsável por inflar o layout XML no adapter
    @NotNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.comentario_item, parent, false);

        return new ComentarioViewHolder(view);
    }

    // Responsável por ligar um determinado item do recycler view com o layout
    @Override
    public void onBindViewHolder(@NonNull @NotNull ComentarioAdapter.ComentarioViewHolder holder, int position) {

        Comentario comentario = comentarios.get(position);
        holder.bind(comentario);
    }

    // Responsável por definir o tamanho da recycler view
    @Override
    public int getItemCount() {
        return this.comentarios.size();
    }

    class ComentarioViewHolder extends RecyclerView.ViewHolder {

        TextView txtNome;
        TextView txtComentario;
        TextView tvIcon;

        public ComentarioViewHolder(@NonNull View itemView){
            super(itemView);
            txtNome = itemView.findViewById(R.id.txt_nome_usuario);
            txtComentario = itemView.findViewById(R.id.txt_comentario_usuario);
            tvIcon = itemView.findViewById(R.id.tv_icon);
        }

        // Vincula os dados do item específico da recycler view com a view
        public void bind(Comentario comentario){
            for(Usuario usuario: usuariosDosComentarios){
                if(usuario.getId() == comentario.getIdUsuario()){
                    txtNome.setText(usuario.getNome());
                    int hash = usuario.getNome().hashCode();
                    tvIcon.setText(String.valueOf(usuario.getNome().charAt(0)));
                    tvIcon.setBackground(DrawableUtils.oval(Color.rgb(hash, hash / 2, 0), tvIcon));
                }
            }

            txtComentario.setText(comentario.getTexto());
        }
    }
}
