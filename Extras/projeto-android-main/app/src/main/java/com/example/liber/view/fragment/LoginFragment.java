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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.liber.utils.GsonUtils.getErrorMessageFromJson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginFragment extends Fragment {

    private EditText etEmail;
    private EditText etSenha;
    private MaterialButton btnLogin;
    private TextView tvCadastre;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @org.jetbrains.annotations.NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindOnView(view);
        clickListener();
    }

    // Faz o bind dos itens da view
    private void bindOnView(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etSenha = view.findViewById(R.id.et_senha);
        btnLogin = view.findViewById(R.id.btn_login);
        tvCadastre = view.findViewById(R.id.tv_cadastre);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void clickListener() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim().toLowerCase();
            //String senha = getMd5(etSenha.getText().toString());
            String senha = etSenha.getText().toString().trim().toLowerCase();

            if (email.equals("") || senha.equals("")) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            getLogin(email, senha);
        });

        tvCadastre.setOnClickListener(v -> cadastrarFragment());
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

    // Método chamado quando o botão de login é pressionado
    private void getLogin(String email, String senha) {
        startLoading();
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<Usuario> callAsync = service.getLogin(email, senha);

        callAsync.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                // É executado se o login for feito com sucesso
                if (response.isSuccessful()) {
                    // Recebe os dados do usuário que fez o login da API e abre a tela principal
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    String usuarioJson;
                    Gson gson = new Gson();
                    usuarioJson = gson.toJson(response.body(), Usuario.class);
                    intent.putExtra("usuario", usuarioJson);
                    startActivity(intent);
                    stopLoading();
                    clearFields();
                } else if (response.errorBody() != null) {
                    // Mostra a mensagem de erro caso ocorra um exceção na API (ex. Usuário inexistente)
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
        btnLogin.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        etEmail.setEnabled(false);
        etSenha.setEnabled(false);
    }

    private void stopLoading() {
        btnLogin.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        etEmail.setEnabled(true);
        etSenha.setEnabled(true);
    }

    private void clearFields() {
        etEmail.setText("");
        etSenha.setText("");
    }
}
