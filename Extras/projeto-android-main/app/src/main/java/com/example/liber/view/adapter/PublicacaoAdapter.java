package com.example.liber.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liber.R;
import com.example.liber.controller.PublicacaoClickInterface;
import com.example.liber.model.Publicacao;
import com.example.liber.model.Servico;
import com.example.liber.view.fragment.PublicacoesFragment;

import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;
import java.util.List;

import retrofit2.Callback;

public class PublicacaoAdapter extends RecyclerView.Adapter<PublicacaoAdapter.PublicacaoViewHolder> {

    public final List<Publicacao> publicacaos;
    // Interface que irá detectar os clicks do adapter
    private final PublicacaoClickInterface publicacaoClickInterface;
    private final Context context;
    private List<Servico> servicos;
    public boolean estaCurtido;

    public PublicacaoAdapter(List<Publicacao> publicacaos, PublicacaoClickInterface publicacaoClickInterface, Context context) {
        this.publicacaos = publicacaos;
        this.context = context;
        this.publicacaoClickInterface = publicacaoClickInterface;
    }

    public List<Publicacao> getPublicacoes() {
        return publicacaos;
    }

    public PublicacaoAdapter(List<Publicacao> publicacaos, List<Servico> servicos, PublicacaoClickInterface publicacaoClickInterface, Context context) {
        this.publicacaos = publicacaos;
        this.servicos = servicos;

        for (Publicacao publicacao: this.publicacaos) {

            for (Servico servico: this.servicos) {

                if(publicacao.idServico == servico.id){
                    publicacao.servico = servico.nome;
                }
            }
        }

        this.context = context;
        this.publicacaoClickInterface = publicacaoClickInterface;
    }

    // Responsável por inflar o layout XML no adapter
    @NonNull
    @NotNull
    @Override
    public PublicacaoViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publicacao_recycler_item, parent, false);
        return new PublicacaoViewHolder(view);
    }

    // Responsável por ligar um determinado item do recycler view com o layout
    @Override
    public void onBindViewHolder(@NonNull @NotNull PublicacaoAdapter.PublicacaoViewHolder holder, int position) {
        Publicacao publicacao = publicacaos.get(position);
        holder.bind(publicacao);
    }

    // Responsável por definir o tamanho da recycler view
    @Override
    public int getItemCount() {
        return publicacaos.size();
    }

    class PublicacaoViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tvTitulo;
        TextView tvLocalizacao;
        TextView tvServico;
        TextView tvCurtidas;
        TextView tvCurtir;


        public PublicacaoViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_publicacao);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvLocalizacao = itemView.findViewById(R.id.tv_localizacao);
            tvServico = itemView.findViewById(R.id.tv_servico);
            tvCurtidas = itemView.findViewById(R.id.tv_Curtidas);
            tvCurtir = itemView.findViewById(R.id.tv_Curtir);
        }

        // Vincula os dados do item específico da recycler view com a view
        void bind(Publicacao publicacao) {
            tvTitulo.setText(publicacao.titulo);
            tvLocalizacao.setText(publicacao.localizacao);
            tvServico.setText(publicacao.servico);
            tvCurtidas.setText(String.valueOf(publicacao.curtidas));

            /*String src = publicacao.getTitulo().toLowerCase().replace(" ", "_");
            String s = Normalizer.normalize(src, Normalizer.Form.NFD);
            s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

            int drawableId = context.getResources().getIdentifier(s, "drawable", context.getPackageName());
            tvImagem.setImageResource(drawableId);*/

            clickListener(publicacao);
        }

        // Abre a tela do livro
        private void clickListener(Publicacao publicacao) {
            cardView.setOnClickListener(v -> {
                publicacaoClickInterface.onPublicacaoClick(publicacao);
            });

            tvCurtir.setOnClickListener(v -> {
                // Verifica o estado atual do drawable
                 // Adicione este boolean na classe Publicacao
                
                if (!estaCurtido) {
                    estaCurtido = true;
                    tvCurtir.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,  R.drawable.ic_baseline_hearth_24);
                    publicacao.curtidas++;
                } else {
                    estaCurtido = false;
                    tvCurtir.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_baseline_hearth_border_24);
                    publicacao.curtidas--;
                }
                
                publicacao.isCurtido = !estaCurtido;
                tvCurtidas.setText(String.valueOf(publicacao.curtidas));
                
                // Opcional: Notificar o adapter sobre a mudança
                //notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
