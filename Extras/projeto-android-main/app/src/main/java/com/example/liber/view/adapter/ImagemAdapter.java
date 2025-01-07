package com.example.liber.view.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liber.R;

import java.util.ArrayList;
import java.util.List;

public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.ImagemViewHolder> {
    private List<Uri> imagens;
    private OnImagemClickListener listener;

    public ImagemAdapter(OnImagemClickListener listener) {
        this.imagens = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagem, parent, false);
        return new ImagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemViewHolder holder, int position) {
        Uri imagem = imagens.get(position);
        holder.ivImagem.setImageURI(imagem);
        holder.ivRemover.setOnClickListener(v -> {
            imagens.remove(position);
            notifyDataSetChanged();
            listener.onImagemRemovida(position);
        });
    }

    @Override
    public int getItemCount() {
        return imagens.size();
    }

    public void addImagem(Uri uri) {
        imagens.add(uri);
        notifyDataSetChanged();
    }

    public List<Uri> getImagens() {
        return imagens;
    }

    static class ImagemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagem;
        ImageView ivRemover;

        public ImagemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagem = itemView.findViewById(R.id.iv_imagem);
            ivRemover = itemView.findViewById(R.id.iv_remover);
        }
    }

    public interface OnImagemClickListener {
        void onImagemRemovida(int position);
    }
}
