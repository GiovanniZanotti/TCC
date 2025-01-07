package com.example.liber.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.liber.R;
import com.example.liber.model.Servico;
import com.example.liber.view.adapter.PublicacaoAdapter;
import com.example.liber.view.adapter.SeguidorAdapter;
import com.example.liber.view.adapter.ServicosAdapter;
import com.example.liber.view.adapter.UsuarioAdapter;
import com.example.liber.controller.TownTechApi;
import com.example.liber.model.Publicacao;
import com.example.liber.model.Usuario;
import com.example.liber.service.RetrofitService;
import com.example.liber.view.activity.MainActivity;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.liber.utils.GsonUtils.getErrorMessageFromJson;

public class PesquisarFragment extends Fragment {

    private static final String TAG = "PesquisarFragment";

    protected TextView tvTitle, tvVazio;
    protected EditText etSearch;
    protected MaterialButton btnSearch;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ProgressBar progressBar;

    private UsuarioAdapter usuarioAdapter;
    private SeguidorAdapter seguidorAdapter;

    private ServicosAdapter servicoAdapter;
    private PublicacaoAdapter publicacaoAdapter;

    protected Spinner spinnerOrdenacao;
    private static final String[] OPCOES_ORDENACAO = {"Ordem Normal", "Mais Curtidas"};

    protected TownTechApi service = RetrofitService.createService(TownTechApi.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pesquisar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindOnView(view);
    }

    // Faz o vínculo dos ítens da view
    protected void bindOnView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvVazio = view.findViewById(R.id.tv_vazio);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view_main);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        spinnerOrdenacao = view.findViewById(R.id.spinner_ordenacao);
        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            OPCOES_ORDENACAO
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrdenacao.setAdapter(adapter);
        
        spinnerOrdenacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (publicacaoAdapter != null) {
                    List<Publicacao> publicacoes = publicacaoAdapter.publicacaos;
                    if (position == 1) { // Mais Curtidas
                        Collections.sort(publicacoes, (p1, p2) ->
                            Integer.compare(p2.getCurtidas(), p1.getCurtidas()));
                    } else { // Ordem Normal
                        Collections.sort(publicacoes, (p1, p2) -> 
                            Long.compare(p2.getId(), p1.getId()));
                    }
                    publicacaoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Carrega a lista de usuários da API
    protected void loadUsuariosRecyclerView(UsuariosFragment usuariosFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Usuario>> callAsync = service.getAllUsuarios();

        callAsync.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    loadSeguindoRecyclerView(MainActivity.getLoggedUsuario().getId(), response.body(), usuariosFragment);
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Carrega a lista de seguidores da API
    protected void loadSeguindoRecyclerView(int id, List<Usuario> usuarios, UsuariosFragment usuariosFragment) {
        Call<List<Usuario>> callAsync = service.getSeguindo(id);

        callAsync.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    usuarioAdapter = new UsuarioAdapter(usuarios, response.body(), usuariosFragment);
                    recyclerView.setAdapter(usuarioAdapter);
                    progressBar.setVisibility(View.GONE);
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Segue um usuário
    protected void seguir(int seguidorId) {
        Call<Boolean> callAsync = service.seguir(MainActivity.getLoggedUsuario().getId(), seguidorId);

        callAsync.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if (response.body()) {
                        Toast.makeText(getContext(), "Seguindo", Toast.LENGTH_SHORT).show();
                        usuarioAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Erro", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                imprimirToastErro();
            }
        });
    }

    // Deixa de seguir um usuário
    protected void deixarDeSeguir(int seguidorId) {
        Call<Boolean> callAsync = service.deixarDeSeguir(MainActivity.getLoggedUsuario().getId(), seguidorId);

        callAsync.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if (response.body()) {
                        Toast.makeText(getContext(), "Deixou de seguir", Toast.LENGTH_SHORT).show();
                        seguidorAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Erro", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                imprimirToastErro();
            }
        });
    }

    // Busca os usuários pelo nome
    protected void getUsuarioBySearch(String nome, UsuariosFragment usuariosFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Usuario>> callAsync = service.getBySearch(nome);

        callAsync.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    loadSeguindoRecyclerView(MainActivity.getLoggedUsuario().getId(), response.body(), usuariosFragment);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                imprimirToastErro();
            }
        });
    }

    // Carrega a recycler view dos serviços
    protected void loadServicosRecyclerView(ServicosFragment servicosFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Servico>> callAsync = service.getServicos();

        callAsync.enqueue(new Callback<List<Servico>>() {
            @Override
            public void onResponse(Call<List<Servico>> call, Response<List<Servico>> response) {
                if (response.isSuccessful()) {
                    servicoAdapter = new ServicosAdapter(response.body(), servicosFragment);
                    recyclerView.setAdapter(servicoAdapter);

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(gridLayoutManager);

                    progressBar.setVisibility(View.GONE);
                    checkVazioServico(response.body());
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Servico>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    protected void getServicoBySearch(String nome, ServicosFragment servicosFragment) {
        Call<List<Servico>> callAsync = service.getServicosBySearch(nome);

        callAsync.enqueue(new Callback<List<Servico>>() {
            @Override
            public void onResponse(Call<List<Servico>> call, Response<List<Servico>> response) {
                servicoAdapter = new ServicosAdapter(response.body(), servicosFragment);
                recyclerView.setAdapter(servicoAdapter);
                progressBar.setVisibility(View.GONE);
                checkVazioServico(response.body());
            }

            @Override
            public void onFailure(Call<List<Servico>> call, Throwable t) {
                imprimirToastErro();
            }
        });
    }

    // Carrega a recycler view dos seguidores
    protected void loadSeguidoresRecyclerView(SeguidoresFragment seguidoresFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Usuario>> callAsync = service.getSeguidores(MainActivity.getLoggedUsuario().getId());

        callAsync.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    seguidorAdapter = new SeguidorAdapter(response.body(), seguidoresFragment);
                    recyclerView.setAdapter(seguidorAdapter);
                    progressBar.setVisibility(View.GONE);
                    checkVazio(response.body());
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Carrega a recycler view das publicações
    protected void loadPublicacaoRecyclerView(PublicacoesFragment publicacoesFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Publicacao>> callAsync = service.getPublicacoes();

        Call<List<Servico>> callAsyncServico = service.getServicos();

        callAsyncServico.enqueue(new Callback<List<Servico>>() {
            @Override
            public void onResponse(Call<List<Servico>> call, Response<List<Servico>> response) {
                if (response.isSuccessful()) {

                    List<Servico> servicos = response.body();

                    callAsync.enqueue(new Callback<List<Publicacao>>() {
                        @Override
                        public void onResponse(Call<List<Publicacao>> call, Response<List<Publicacao>> response) {
                            if (response.isSuccessful()) {
                                publicacaoAdapter = new PublicacaoAdapter(response.body(), servicos, publicacoesFragment, getContext());
                                //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                                //recyclerView.setLayoutManager(gridLayoutManager);
                                recyclerView.setAdapter(publicacaoAdapter);
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Publicacao>> call, Throwable t) {
                            imprimirToastErro();
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Servico>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Busca os seguidores pelo nome
    protected void getSeguidorBySearch(String nome, SeguidoresFragment seguidoresFragment) {
        Call<List<Usuario>> callAsync = service.getSeguidorBySearch(MainActivity.getLoggedUsuario().getId(), nome);

        callAsync.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                seguidorAdapter = new SeguidorAdapter(response.body(), seguidoresFragment);
                recyclerView.setAdapter(seguidorAdapter);
                progressBar.setVisibility(View.GONE);
                checkVazio(response.body());
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                imprimirToastErro();
            }
        });
    }

    // Busca as publicações pelo nome
    protected void getLivroBySearch(String titulo, PublicacoesFragment publicacoesFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Publicacao>> callAsync = service.getLivrosTitulo(titulo);

        callAsync.enqueue(new Callback<List<Publicacao>>() {
            @Override
            public void onResponse(Call<List<Publicacao>> call, Response<List<Publicacao>> response) {
                if (response.isSuccessful()) {
                    publicacaoAdapter = new PublicacaoAdapter(response.body(), publicacoesFragment, getContext());
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(publicacaoAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Publicacao>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    protected void getPublicacaoBySearch(String titulo, PublicacoesFragment publicacoesFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Publicacao>> callAsync = service.getPublicacaoBySearch(titulo);

        callAsync.enqueue(new Callback<List<Publicacao>>() {
            @Override
            public void onResponse(Call<List<Publicacao>> call, Response<List<Publicacao>> response) {
                if (response.isSuccessful()) {
                    publicacaoAdapter = new PublicacaoAdapter(response.body(), publicacoesFragment, getContext());
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL , false);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(publicacaoAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Publicacao>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Carrega a recycler view dos usuários seguindo
    protected void loadSeguindoRecyclerView(int id, SeguindoFragment seguindoFragment ) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Usuario>> callAsync = service.getSeguindo(id);

        callAsync.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    seguidorAdapter = new SeguidorAdapter(response.body(), seguindoFragment);
                    seguidorAdapter.setAtivarBtn(true);
                    recyclerView.setAdapter(seguidorAdapter);
                    progressBar.setVisibility(View.GONE);
                    checkVazio(response.body());
                } else if (response.errorBody() != null) {
                    Toast.makeText(getContext(), "Erro: " + getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Busca os usuários seguindo pelo nome
    protected void getSeguindoBySearch(int usuarioId, String nome, SeguindoFragment seguindoFragment) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Usuario>> callAsync = service.getSeguindoBySearch(usuarioId, nome);

        callAsync.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    seguidorAdapter = new SeguidorAdapter(response.body(), seguindoFragment);
                    seguidorAdapter.setAtivarBtn(true);
                    recyclerView.setAdapter(seguidorAdapter);
                    progressBar.setVisibility(View.GONE);
                    checkVazio(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                imprimirToastErro();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Mostra uma mensagem caso a API retorne uma lista vazia
    private void checkVazio(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
        } else {
            tvVazio.setVisibility(View.GONE);
        }
    }

    private void checkVazioServico(List<Servico> servicos) {
        if (servicos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
        } else {
            tvVazio.setVisibility(View.GONE);
        }
    }

    private void imprimirToastErro() {
        Toast.makeText(getContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_SHORT).show();
    }
}
