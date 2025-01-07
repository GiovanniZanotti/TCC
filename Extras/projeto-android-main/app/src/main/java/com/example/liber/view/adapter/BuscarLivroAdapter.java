package com.example.liber.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liber.R;
import com.example.liber.model.Publicacao;
import com.example.liber.view.activity.PublicacaoActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuscarLivroAdapter extends RecyclerView.Adapter<BuscarLivroAdapter.LivroViewHolder>{

    private List<Publicacao> publicacaos;
    private Context applicationContext;

    public BuscarLivroAdapter(List<Publicacao> publicacaos, Context applicationContext) {
        this.publicacaos = publicacaos;
        this.applicationContext = applicationContext;
    }

    // Responsável por inflar o layout XML no adapter
    @NotNull
    @Override
    public LivroViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.livro_item, parent, false);

        return new LivroViewHolder(view);
    }

    // Responsável por ligar um determinado item do recycler view com o layout
    @Override
    public void onBindViewHolder(@NonNull @NotNull BuscarLivroAdapter.LivroViewHolder holder, int position) {

        Publicacao publicacao = publicacaos.get(position);
        holder.bind(publicacao);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( applicationContext, PublicacaoActivity.class);
                Gson gson = new Gson();
                String livroGson = gson.toJson(publicacao, Publicacao.class);
                intent.putExtra("livro", livroGson);
                applicationContext.startActivity(intent);
            }
        });
    }

    // Responsável por definir o tamanho da recycler view
    @Override
    public int getItemCount() {
        return this.publicacaos.size();
    }

    class LivroViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo;
        TextView txtAutor;

        public LivroViewHolder(@NonNull View itemView){
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.titulo_textview);
            txtAutor = itemView.findViewById(R.id.autor_textview);
        }

        // Vincula os dados do item específico da recycler view com a view
        public void bind(Publicacao publicacao){
            txtTitulo.setText(publicacao.getTitulo());
            txtAutor.setText(publicacao.getNomeAutor());
        }
    }
}
