package com.example.liber.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liber.R;
import com.example.liber.controller.TownTechApi;
import com.example.liber.model.Publicacao;
import com.example.liber.service.RetrofitService;
import com.example.liber.view.adapter.ImagemAdapter;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback; 
import retrofit2.Response;

import static com.example.liber.utils.GsonUtils.getErrorMessageFromJson;


public class PublicacaoFragment extends Fragment implements ImagemAdapter.OnImagemClickListener {

    private EditText etTitulo;
    private EditText etDescricao;
    private EditText etLocalizacao;
    private MaterialButton btnPublicar;
    private ProgressBar progressBar;
    private ImageView etImagem;
    private RecyclerView rvImagens;
    private ImagemAdapter imagemAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publicacao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @org.jetbrains.annotations.NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindOnView(view);
        clickListener();
    }

    // Faz o bind dos itens da view
    private void bindOnView(View view) {
        etTitulo = view.findViewById(R.id.et_titulo);
        etDescricao = view.findViewById(R.id.et_descricao);
        etLocalizacao = view.findViewById(R.id.et_localizacao);
        btnPublicar = view.findViewById(R.id.btn_publicar);
        progressBar = view.findViewById(R.id.progressBar);
        etImagem = view.findViewById(R.id.et_imagem);
        rvImagens = view.findViewById(R.id.rv_imagens);
        
        imagemAdapter = new ImagemAdapter(this);
        rvImagens.setAdapter(imagemAdapter);
    }

    private void clickListener() {
        // É executado quando o botão de login é pressionado
        btnPublicar.setOnClickListener(v -> {

            Publicacao publicacao = new Publicacao();

            publicacao.titulo = etTitulo.getText().toString().trim().toLowerCase();
            publicacao.descricao = etDescricao.getText().toString();
            publicacao.localizacao = etLocalizacao.getText().toString();

            if (publicacao.titulo.equals("") || publicacao.descricao.equals("")) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            publicar(publicacao);
        });

        etImagem.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_VIEW);
            startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imagemAdapter.addImagem(imageUri);
        }
    }

    @Override
    public void onImagemRemovida(int position) {

    }

    // Método chamado quando o botão de login é pressionado
    private void publicar(Publicacao publicacao) {
        startLoading();
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<Boolean> callAsync = service.publicar(publicacao);

        callAsync.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                // É executado se o login for feito com sucesso
                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), "Publicado", Toast.LENGTH_LONG).show();
                    // Recebe os dados do usuário que fez o login da API e abre a tela principal
                    /*Intent intent = new Intent(getContext(), MainActivity.class);
                    String usuarioJson;
                    Gson gson = new Gson();
                    usuarioJson = gson.toJson(response.body(), Usuario.class);
                    intent.putExtra("usuario", usuarioJson);
                    startActivity(intent);*/
                    stopLoading();
                    clearFields();
                } else if (response.errorBody() != null) {
                    // Mostra a mensagem de erro caso ocorra um exceção na API (ex. Usuário inexistente)
                    Toast.makeText(getContext(), getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_LONG).show();
                    stopLoading();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG).show();
                stopLoading();
            }
        });
    }

    // Método chamado quando o botão cadastre-se é pressionado
    private void cadastrarFragment() {
        // Substitui o fragment de login pelo fragment de cadastro
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right
                )
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .replace(R.id.login_fragment, CadastrarFragment.class, null)
                .commit();
    }

    private void startLoading() {
        btnPublicar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        etTitulo.setEnabled(false);
        etDescricao.setEnabled(false);
        etLocalizacao.setEnabled(false);
    }

    private void stopLoading() {
        btnPublicar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        etTitulo.setEnabled(true);
        etDescricao.setEnabled(true);
        etLocalizacao.setEnabled(true);
    }

    private void clearFields() {
        etTitulo.setText("");
        etDescricao.setText("");
        etLocalizacao.setText("");
        imagemAdapter = new ImagemAdapter(this);
        rvImagens.setAdapter(imagemAdapter);
    }
}

