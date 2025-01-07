package com.example.liber.view.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liber.R;
import com.example.liber.view.adapter.ComentarioAdapter;
import com.example.liber.controller.ComentarioClickInterface;
import com.example.liber.controller.TownTechApi;
import com.example.liber.model.Comentario;
import com.example.liber.model.Publicacao;
import com.example.liber.model.Usuario;
import com.example.liber.service.RetrofitService;
import com.example.liber.utils.Task;
import com.example.liber.view.adapter.ImagemAdapter;
import com.example.liber.view.fragment.ComentarDialogFragment;
import com.google.gson.Gson;

import java.io.File;
import java.text.Normalizer;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicacaoActivity extends AppCompatActivity implements ComentarioClickInterface, ImagemAdapter.OnImagemClickListener{

    ImageView imgLivro;
    TextView txtTitulo;
    TextView txtAutor;
    TextView tvVazio;
    TextView txvTextoComentario;
    Button btnAddBiblioteca;
    Button btnComentar;
    TextView txtTextoDescricao;
    TextView txtDescricao;
    RecyclerView recycleViewComentatios;

    private RecyclerView rvImagens;
    private ImagemAdapter imagemAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Gson gson;
    private Publicacao publicacao;
    private Usuario usuario;
    ComentarioAdapter comentarioAdapter;

    private TownTechApi service = RetrofitService.createService(TownTechApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacao);

        gson = new Gson();

        // Pega os dados do livro
        publicacao =  gson.fromJson(getIntent().getExtras().getString ("livro"), Publicacao.class);

        // // Pega os dados do usuário logado da tela de login
        usuario = MainActivity.getLoggedUsuario();

        imgLivro = findViewById(R.id.img_livro);
        txtTitulo = findViewById(R.id.titulo_textview);
        txtAutor = findViewById(R.id.autor_textview);
        tvVazio = findViewById(R.id.tv_vazio);
        txvTextoComentario = findViewById(R.id.txv_texto_comentario);
        btnComentar = findViewById(R.id.btn_comentar);
        btnAddBiblioteca = findViewById(R.id.btn_addBiblioteca);
        txtTextoDescricao = findViewById(R.id.txt_texto_descricao);
        txtDescricao = findViewById(R.id.txt_Descricao);
        recycleViewComentatios = findViewById(R.id.recycleViewComentatios);

        rvImagens = findViewById(R.id.rv_act_imagens);

        imagemAdapter = new ImagemAdapter(this);
        rvImagens.setAdapter(imagemAdapter);

        txtDescricao.setMovementMethod(new ScrollingMovementMethod());

        // Exibe os dados do livro na tela
        txtTitulo.setText(publicacao.getTitulo());
        txtAutor.setText(publicacao.getNomeAutor());
        txtDescricao.setText(publicacao.getDescricao());

        if(publicacao.id == 5){

            Uri uri1 = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/calçada.png"));
            Uri uri2 = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/buraco.jpeg"));

            imagemAdapter.addImagem(uri1);
            imagemAdapter.addImagem(uri2);
        }

        // Pega a imagem do livro de acordo com o nome do livro
        String src = this.publicacao.getTitulo().toLowerCase().replace(" ", "_");
        String s = Normalizer.normalize(src, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        int drawableId = this.getResources().getIdentifier(s, "drawable", getApplicationContext().getPackageName());
        imgLivro.setImageResource(drawableId);

        if(getLivro(this.usuario.getId(), this.publicacao.getId())){
            btnAddBiblioteca.setText("Remover da Biblioteca");
        }

        btnAddBiblioteca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Adiciona o livro na biblioteca
                if (btnAddBiblioteca.getText().toString().equals("Adicionar na Biblioteca")){
                    if (adicionarLivro(usuario.getId(), publicacao.getId())){
                        Toast.makeText(getApplicationContext(), "Livro adicionado na sua biblioteca", Toast.LENGTH_SHORT).show();
                        btnAddBiblioteca.setText("Remover da Biblioteca");
                    }
                } else {
                    // Remove o livro da biblioteca
                    if (removerLivro(usuario.getId(), publicacao.getId())){
                        Toast.makeText(getApplicationContext(), "Livro removido da sua biblioteca", Toast.LENGTH_SHORT).show();
                        btnAddBiblioteca.setText("Adicionar na Biblioteca");
                    }
                }

            }
        });

        // Exibe a tela para postar um comentário
        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComentarDialogFragment dialog = new ComentarDialogFragment(PublicacaoActivity.this);
                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });

        loadRecyclerView();
    }

    public static Uri getUriFromPath(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    // Carrega os comentários da publicação, e os usuários que postaram esses comentários
    private void loadRecyclerView() {
        List<Comentario> comentarios = getComentarios(this.publicacao.getId());
        List<Usuario> usuariosDosComentarios = getUsuariosDosComentarios(this.publicacao.getId());
        comentarioAdapter = new ComentarioAdapter(comentarios, usuariosDosComentarios, getApplicationContext());
        recycleViewComentatios.setAdapter(comentarioAdapter);
    }

    private boolean getLivro(int usuarioId, int livroId) {
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<Boolean> callAsync = service.getLivroBiblioteca(usuarioId, livroId);
        Task task = new Task();

        try {
            Response<Boolean> response = task.execute(callAsync).get();
            return response.body();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Adiciona a publicação na biblioteca
    private boolean adicionarLivro(int usuarioId, int livroId) {
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<Boolean> callAsync = service.adicionarLivroBiblioteca(usuarioId, livroId);
        Task task = new Task();

        try {
            Response<Boolean> response = task.execute(callAsync).get();

            return response.body();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Remove a publicação da boblioteca
    private boolean removerLivro(int usuarioId, int livroId) {
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<Boolean> callAsync = service.removerLivroBiblioteca(usuarioId, livroId);
        Task task = new Task();

        try {
            Response<Boolean> response = task.execute(callAsync).get();

            return response.body();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Busca os comentários da publicação
    private List<Comentario> getComentarios(int livroId) {
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<List<Comentario>> callAsync = service.getComentarios(livroId);
        Task task = new Task();

        try {
            Response<List<Comentario>> response = task.execute(callAsync).get();

            checkVazio(response.body());
            return response.body();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Busca os usuários dos comentários
    private List<Usuario> getUsuariosDosComentarios(int publicacaoId) {
        TownTechApi service = RetrofitService.createService(TownTechApi.class);
        Call<List<Usuario>> callAsync = service.getUsuariosDosComentarios(publicacaoId);
        Task task = new Task();

        try {
            Response<List<Usuario>> response = task.execute(callAsync).get();

            return response.body();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Posta um comentário
    @Override
    public void onComentarClick(String texto) {
        Comentario comentario = new Comentario();
        comentario.setTexto(texto);
        comentario.setIdUsuario(MainActivity.getLoggedUsuario().getId());
        comentario.setIdLivro(publicacao.getId());

        Call<Boolean> callAsync = service.inserirComentario(comentario);

        callAsync.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response != null && response.body()) {
                    loadRecyclerView();
                } else {
                    Toast.makeText(getApplicationContext(), "Não foi possível adicionar um comentário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mostra uma mensagem caso a API retorne uma lista vazia
    private void checkVazio(List<Comentario> comentarios) {
        if (comentarios.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
        } else {
            tvVazio.setVisibility(View.GONE);
        }
    }

    @Override
    public void onImagemRemovida(int position) {

    }
}
