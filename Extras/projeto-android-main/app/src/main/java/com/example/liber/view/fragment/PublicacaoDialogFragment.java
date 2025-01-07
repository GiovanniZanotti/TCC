package com.example.liber.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.liber.R;
import com.example.liber.model.Servico;
import com.example.liber.view.adapter.ImagemAdapter;
import com.example.liber.view.adapter.PublicacaoAdapter;
import com.example.liber.controller.TownTechApi;
import com.example.liber.controller.PublicacaoClickInterface;
import com.example.liber.controller.PesquisarClickInterface;
import com.example.liber.model.Publicacao;
import com.example.liber.service.RetrofitService;
import com.example.liber.view.activity.PublicacaoActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.liber.utils.GsonUtils.getErrorMessageFromJson;

import android.location.Address;
import android.location.Geocoder;

public class PublicacaoDialogFragment extends DialogFragment implements PublicacaoClickInterface, ImagemAdapter.OnImagemClickListener {

    private static final String TAG = "PublicacaoDialogFragment";
    private final TownTechApi service = RetrofitService.createService(TownTechApi.class);
    private RecyclerView recyclerView;
    private PublicacaoAdapter publicacaoAdapter;
    //private ProgressBar progressBar;
    private final Servico servico;
    private final PesquisarClickInterface pesquisarClickInterface;
    private SwipeRefreshLayout swipeRefreshLayout;

    private EditText etTitulo;
    private EditText etDescricao;
    private EditText etLocalizacao;
    private MaterialButton btnPublicar;
    private ProgressBar progressBar;
    private ImageView etImagem;
    private ImageView img_mapa;
    private RecyclerView rvImagens;
    private ImagemAdapter imagemAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_LOCATION_REQUEST = 2;

    // Adicione estas variáveis para armazenar o estado
    private String savedTitulo;
    private String savedDescricao;
    private List<Uri> savedImagens;

    private ImageView btnBuscarEndereco;

    public PublicacaoDialogFragment(Servico servico, PesquisarClickInterface pesquisarClickInterface) {
        this.pesquisarClickInterface = pesquisarClickInterface;
        this.servico = servico;
    }

    public PublicacaoDialogFragment(Servico servico) {
        this.servico = servico;
        pesquisarClickInterface = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publicacao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindOnView(view);
        clickListener();
        //swipeListener();
        //bindUsuario();
        //getSeguidoresCount();
        //loadRecyclerView();

        if( checkStoragePermissions()){
            requestStoragePermissions();
        }
    }

    // Define o tamanho da janela do perfil após a inicialização
    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    // Faz o vínculo dos ítens da view
    private void bindOnView(View view) {

        etTitulo = view.findViewById(R.id.et_titulo);
        etDescricao = view.findViewById(R.id.et_descricao);
        etLocalizacao = view.findViewById(R.id.et_localizacao);
        btnPublicar = view.findViewById(R.id.btn_publicar);
        progressBar = view.findViewById(R.id.progressBar);
        etImagem = view.findViewById(R.id.et_imagem);
        img_mapa = view.findViewById(R.id.img_mapa);

        rvImagens = view.findViewById(R.id.rv_imagens);

        imagemAdapter = new ImagemAdapter(this);
        rvImagens.setAdapter(imagemAdapter);

        btnBuscarEndereco = view.findViewById(R.id.btn_buscar_endereco);
    }

    // Detecta o movimento de swipe e recarrega a recycler view
    private void swipeListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadRecyclerView();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Exibe os dados do usuário
    private void bindUsuario() {
        /*try {
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
        }*/
    }

    // Pega a quantidade de seguidores do usuário da API e exibe na tela
    private void getSeguidoresCount() {
        /*Call<Integer> callAsync = service.getSeguidoresCount(usuario.getId());

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
        });*/
    }

    // Carrega a biblioteca do usuário
    private void loadRecyclerView() {
        /*Call<List<Publicacao>> callAsync = service.getAllLivros(usuario.getId());

        callAsync.enqueue(new Callback<List<Publicacao>>() {
            @Override
            public void onResponse(Call<List<Publicacao>> call, Response<List<Publicacao>> response) {
                if (response.isSuccessful()) {
                    publicacaoAdapter = new PublicacaoAdapter(response.body(), PerfilDialogFragment.this, getContext());
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
        });*/
    }

    // Mostra uma mensagem caso a API retorne uma lista vazia
    /*private void checkVazio(List<Publicacao> publicacaos) {
        if (publicacaos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
        } else {
            tvVazio.setVisibility(View.GONE);
        }
    }*/

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

    private void clickListener() {
        btnPublicar.setOnClickListener(v -> {

            Publicacao publicacao = new Publicacao();

            publicacao.titulo = etTitulo.getText().toString().trim().toLowerCase();
            publicacao.descricao = etDescricao.getText().toString();
            publicacao.localizacao = etLocalizacao.getText().toString();
            publicacao.idServico = servico.id;

            if (publicacao.titulo.equals("") || publicacao.descricao.equals("")) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            publicar(publicacao);
        });

        etImagem.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Selecione as imagens"), PICK_IMAGE_REQUEST);
        });

        img_mapa.setOnClickListener(v -> {
            // Salvar o estado atual
            savedTitulo = etTitulo.getText().toString();
            savedDescricao = etDescricao.getText().toString();
            savedImagens = imagemAdapter.getImagens();
            
            Bundle bundle = new Bundle();
            bundle.putBoolean("isLocationPicker", true);
            bundle.putString("savedTitulo", savedTitulo);
            bundle.putString("savedDescricao", savedDescricao);
            
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle);
            mapFragment.setLocationPickerCallback((latitude, longitude) -> {
                // Usar requireActivity().runOnUiThread para garantir que estamos na thread principal
                requireActivity().runOnUiThread(() -> {
                    // Usar o FragmentManager da Activity
                    PublicacaoDialogFragment publicacaoDialog = new PublicacaoDialogFragment(servico, pesquisarClickInterface);
                    publicacaoDialog.show(requireActivity().getSupportFragmentManager(), TAG);
                    
                    // Restaurar os dados salvos
                    publicacaoDialog.restaurarEstado(savedTitulo, savedDescricao, savedImagens, latitude + "," + longitude);
                });
                return null;
            });
            
            // Esconder o DialogFragment antes de abrir o mapa
            dismiss();
            
            getParentFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, mapFragment)
                .addToBackStack(null)
                .commit();
        });

        btnBuscarEndereco.setOnClickListener(v -> {
            String endereco = etLocalizacao.getText().toString().trim();
            if (!endereco.isEmpty()) {
                buscarCoordenadas(endereco);
            } else {
                Toast.makeText(getContext(), "Digite um endereço", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Adicione este método para definir a localização
    public void setLocalizacao(String localizacao) {
        if (etLocalizacao != null) {
            etLocalizacao.setText(localizacao);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();


                    imagemAdapter.addImagem(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imagemAdapter.addImagem(imageUri);
            }
        }
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

    public static final int BITE_SIZE = 4 * 1024;

    public static byte[] compressImage(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[BITE_SIZE];

        while(!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp,0, size);
        }

        outputStream.close();

        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] data) throws DataFormatException, IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[BITE_SIZE];

        while (!inflater.finished()) {
            int count = inflater.inflate(tmp);
            outputStream.write(tmp, 0, count);
        }

        outputStream.close();

        return outputStream.toByteArray();
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
    }

    @Override
    public void onImagemRemovida(int position) {

    }

    // Método para restaurar o estado
    public void restaurarEstado(String titulo, String descricao, List<Uri> imagens, String localizacao) {
        // Aguardar a view ser criada
        View view = getView();
        if (view != null) {
            etTitulo.setText(titulo);
            etDescricao.setText(descricao);
            etLocalizacao.setText(localizacao);
            
            // Restaurar imagens
            if (imagens != null) {
                for (Uri uri : imagens) {
                    imagemAdapter.addImagem(uri);
                }
            }
        } else {
            // Se a view ainda não foi criada, salvar os dados para restaurar depois
            savedTitulo = titulo;
            savedDescricao = descricao;
            savedImagens = imagens;
            
            // Sobrescrever onViewCreated para restaurar os dados quando a view for criada
            View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    restaurarEstado(savedTitulo, savedDescricao, savedImagens, localizacao);
                    v.removeOnAttachStateChangeListener(this);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {}
            };
            
            getDialog().getWindow().getDecorView().addOnAttachStateChangeListener(listener);
        }
    }

    private void buscarCoordenadas(String endereco) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), new Locale("pt", "BR"));
            List<Address> enderecos = geocoder.getFromLocationName(endereco, 1);

            if (enderecos != null && !enderecos.isEmpty()) {
                Address location = enderecos.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                
                // Formatar o endereço completo
                String enderecoCompleto = String.format(
                    "%s, %s - %s, %s",
                    location.getThoroughfare(), // Rua
                    location.getSubThoroughfare(), // Número
                    location.getSubLocality(), // Bairro
                    location.getLocality() // Cidade
                );
                
                // Atualizar o campo com o endereço formatado e as coordenadas
                String textoFinal = enderecoCompleto;
                
                etLocalizacao.setText(endereco);

                Toast.makeText(getContext(), "Endereço encontrado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Erro ao buscar endereço", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao buscar endereço", Toast.LENGTH_LONG).show();
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 123;

    private void requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33) e superior
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES
            }, PERMISSION_REQUEST_CODE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) e superior
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            // Android 10 (API 29) e inferior
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        }
    }

    // Verificar se tem permissão
    private boolean checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    // Tratar o resultado da solicitação de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, tente acessar as imagens novamente
                //acessarImagens();
            } else {
                Toast.makeText(getContext(), "Permissão negada para acessar as imagens", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para acessar as imagens
    /*private void acessarImagens() {
        if (checkStoragePermissions()) {
            File dcimDir = new File("/storage/emulated/0/DCIM");
            if (dcimDir.exists() && dcimDir.canRead()) {
                // Acesse suas imagens aqui
                File[] files = dcimDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        Uri uri = Uri.fromFile(file);
                        // Use o URI conforme necessário
                    }
                }
            } else {
                Toast.makeText(getContext(), "Não foi possível acessar o diretório DCIM", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestStoragePermissions();
        }
    }*/
}

