package com.example.liber.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.liber.R;
import com.example.liber.controller.TownTechApi;
import com.example.liber.model.Usuario;
import com.example.liber.service.RetrofitService;
import com.example.liber.view.activity.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.liber.utils.GsonUtils.getErrorMessageFromJson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class CadastrarFragment extends Fragment {

    private EditText etEmail;
    private EditText etSenha;
    private EditText etNome;
    private MaterialButton btnCadastrar;
    private TextView tvLogin;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastrar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindOnView(view);
        onClickListener();
    }

    // Faz o vínculo dos itens da view
    private void bindOnView(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etSenha = view.findViewById(R.id.et_senha);
        etNome = view.findViewById(R.id.et_nome);
        btnCadastrar = view.findViewById(R.id.btn_login);
        tvLogin = view.findViewById(R.id.tv_faca_login);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void onClickListener() {
        // É executado quando o botão cadastrar é pressionado
        btnCadastrar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim().toLowerCase();
            String senha = etSenha.getText().toString();
            String nome = etNome.getText().toString().trim();

            if (email.equals("") || senha.equals("") || nome.equals("")) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
                return;
            }

            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setSenha(getMd5(senha));
            usuario.setNome(nome);
            getCadastro(usuario);
        });

        // É executado quando o botão de voltar para a tela de login é pressionado
        tvLogin.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    public String getMd5(String input)
    {

            // Static getInstance method is called with hashing MD5
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
    }

    // Método chamado quando o botão cadastrar é pressionado
    private void getCadastro(Usuario usuario) {
        startLoading();
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<Usuario> callAsync = service.getCadastro(usuario);

        callAsync.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // É executado se o cadastro for feito com sucesso
                if (response.isSuccessful()) {
                    // Recebe os dados do usuário que fez o cadastro da API e abre a tela principal
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    String usuarioJson;
                    Gson gson = new Gson();
                    usuarioJson = gson.toJson(response.body(), Usuario.class);
                    intent.putExtra("usuario", usuarioJson);
                    startActivity(intent);
                    stopLoading();
                    clearFields();
                } else if (response.errorBody() != null) {
                    // Mostra a mensagem de erro caso ocorra um exceção na API (ex. Usuário já cadastrado)
                    Toast.makeText(getContext(), getErrorMessageFromJson(response.errorBody()), Toast.LENGTH_LONG).show();
                    stopLoading();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG).show();
                stopLoading();
            }
        });
    }

    private void startLoading() {
        btnCadastrar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        etEmail.setEnabled(false);
        etSenha.setEnabled(false);
        etNome.setEnabled(false);
    }

    private void stopLoading() {
        btnCadastrar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        etEmail.setEnabled(true);
        etSenha.setEnabled(true);
        etNome.setEnabled(true);
    }

    private void clearFields() {
        etEmail.setText("");
        etSenha.setText("");
        etNome.setText("");
    }
}
