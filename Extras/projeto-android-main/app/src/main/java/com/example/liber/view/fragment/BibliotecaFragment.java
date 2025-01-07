package com.example.liber.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.liber.R;
import com.example.liber.view.adapter.PublicacaoAdapter;
import com.example.liber.controller.TownTechApi;
import com.example.liber.controller.PublicacaoClickInterface;
import com.example.liber.model.Publicacao;
import com.example.liber.model.Usuario;
import com.example.liber.service.RetrofitService;
import com.example.liber.utils.DrawableUtils;
import com.example.liber.view.activity.PublicacaoActivity;
import com.example.liber.view.activity.MainActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.liber.utils.GsonUtils.getErrorMessageFromJson;

public class BibliotecaFragment extends Fragment implements PublicacaoClickInterface {

    private static final String TAG = "PerfilDialogFragment";

    private final TownTechApi service = RetrofitService.createService(TownTechApi.class);

    private TextView tvTitle, tvIcon, tvNome, tvEmail, tvDataCadastro, tvQtdSeguidores, tvVazio;
    private ImageView close;
    private RecyclerView recyclerView;
    private PublicacaoAdapter publicacaoAdapter;
    private ProgressBar progressBar;
    private Usuario usuario;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pega a referência do usuário que fez o login
        usuario = MainActivity.getLoggedUsuario();

        bindOnView(view);
        swipeListener();
        bindUsuario();
        getSeguidoresCount();
    }

    // Carrega a recycler view após o carregamento do fragment
    @Override
    public void onStart() {
        super.onStart();

        loadRecyclerView();
    }

    // Faz o vínculo dos ítens da view
    private void bindOnView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("Meu perfil");
        close = view.findViewById(R.id.close);
        close.setVisibility(View.GONE);
        tvIcon = view.findViewById(R.id.tv_icon);
        tvNome = view.findViewById(R.id.tv_nome);
        tvEmail = view.findViewById(R.id.tv_email);
        tvVazio = view.findViewById(R.id.tv_vazio);
        recyclerView = view.findViewById(R.id.recycler_view);
        tvDataCadastro = view.findViewById(R.id.tv_data_cadastro);
        tvQtdSeguidores = view.findViewById(R.id.tv_qtd_seguidores);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
    }

    // Detecta o movimento de swipe e recarrega a recycler view
    private void swipeListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadRecyclerView();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Exibe os dados do usuário que fez login na tela
    private void bindUsuario() {
        try {
            Date sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR")).parse(
                    usuario.getData_cadastro()
            );
            int hash = usuario.getNome().hashCode();
            tvIcon.setText(String.valueOf(usuario.getNome().charAt(0)));
            tvIcon.setBackground(DrawableUtils.oval(Color.rgb(hash, hash / 2, 0), tvIcon));
            tvNome.setText(usuario.getNome());
            tvEmail.setText(usuario.getEmail());
            tvDataCadastro.setText("Cadastrou-se em " + new SimpleDateFormat("d MMM yy", new Locale("pt", "BR")).format(sdf));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Pega a quantidade de seguidores do usuário da API e exibe na tela
    private void getSeguidoresCount() {
        Call<Integer> callAsync = service.getSeguidoresCount(usuario.getId());

        callAsync.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    if (response.body().toString().equals("1")) {
                        tvQtdSeguidores.setText(response.body().toString() + " seguidor");
                    } else {
                        tvQtdSeguidores.setText(response.body().toString() + " seguidores");
                    }
                } else {
                    tvQtdSeguidores.setText("? seguidores");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Carrega as publicações da biblioteca do usuário e exibe dentro de uma recycler view
    private void loadRecyclerView() {
        Call<List<Publicacao>> callAsync = service.getAllLivros(usuario.getId());

        callAsync.enqueue(new Callback<List<Publicacao>>() {
            @Override
            public void onResponse(Call<List<Publicacao>> call, Response<List<Publicacao>> response) {
                if (response.isSuccessful()) {
                    publicacaoAdapter = new PublicacaoAdapter(response.body(), BibliotecaFragment.this, getContext());
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(publicacaoAdapter);
                    progressBar.setVisibility(View.GONE);
                    checkVazio(response.body());
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Publicacao>> call, Throwable t) {
                Toast.makeText(getContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mostra uma mensagem caso a API retorne uma lista vazia
    private void checkVazio(List<Publicacao> publicacaos) {
        if (publicacaos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
        } else {
            tvVazio.setVisibility(View.GONE);
        }
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
