package com.example.liber.controller;

import com.example.liber.model.Comentario;
import com.example.liber.model.Publicacao;
import com.example.liber.model.Servico;
import com.example.liber.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// Interface que contém todos os métodos para realizar a comunicação com a API
public interface TownTechApi {

    @GET("usuario/login/{email}/{senha}")
    Call<Usuario> getLogin(@Path("email") String email, @Path("senha") String senha);

    @POST("usuario/cadastrar")
    Call<Usuario> getCadastro(@Body Usuario usuario);

    @GET("usuario")
    Call<List<Usuario>> getAllUsuarios();

    @GET("usuario/{id}")
    Call<Usuario> getUsuarioById(@Path("id") int id);

    @GET("usuario/search/{nome}")
    Call<List<Usuario>> getBySearch(@Path("nome") String nome);

    @GET("seguidor/get-seguindo/{usuarioId}")
    Call<List<Usuario>> getSeguindo(@Path("usuarioId") int id);

    @GET("seguidor/get-seguidores/{usuarioId}")
    Call<List<Usuario>> getSeguidores(@Path("usuarioId") int id);

    @GET("seguidor/get-seguidores-by-search/{usuarioId}/{nome}")
    Call<List<Usuario>> getSeguidorBySearch(@Path("usuarioId") int id, @Path("nome") String nome);

    @GET("servico/get-servicos")
    Call<List<Servico>> getServicos();

    @GET("servico/get-servicos-by-search/{nome}")
    Call<List<Servico>> getServicosBySearch(@Path("nome") String nome);

    @GET("seguidor/get-seguindo-by-search/{usuarioId}/{nome}")
    Call<List<Usuario>> getSeguindoBySearch(@Path("usuarioId") int id, @Path("nome") String nome);

    @GET("seguidor/get-seguidores-count/{usuarioId}")
    Call<Integer> getSeguidoresCount(@Path("usuarioId") int usuarioId);

    @POST("seguidor/seguir/{usuarioId}/{seguidorId}")
    Call<Boolean> seguir(@Path("usuarioId") int usuarioId, @Path("seguidorId") int seguidorId);

    @DELETE("seguidor/deixarDeSeguir/{usuarioId}/{seguidorId}")
    Call<Boolean> deixarDeSeguir(@Path("usuarioId") int usuarioId, @Path("seguidorId") int seguidorId);

    @GET("livro")
    Call<List<Publicacao>> getAllLivros();

    @GET("livro/get-biblioteca/{usuarioId}")
    Call<List<Publicacao>> getAllLivros(@Path("usuarioId") int id);
    
    @GET("livro/get-biblioteca/{usuarioId}")
    Call<List<Publicacao>> getBiblioteca(@Path("usuarioId") int usuarioId);

    @GET("livro/get-livro-titulo/{tituloLivro}")
    Call<List<Publicacao>> getLivrosTitulo(@Path("tituloLivro") String tituloLivro);

    @GET("biblioteca/getLivro/{usuarioId}/{livroId}")
    Call<Boolean> getLivroBiblioteca(@Path("usuarioId") int usuarioId, @Path("livroId") int livroId);

    @POST("biblioteca/adicionarLivro/{usuarioId}/{livroId}")
    Call<Boolean> adicionarLivroBiblioteca(@Path("usuarioId") int usuarioId, @Path("livroId") int livroId);

    @POST("biblioteca/removerLivro/{usuarioId}/{livroId}")
    Call<Boolean> removerLivroBiblioteca(@Path("usuarioId") int usuarioId, @Path("livroId") int livroId);

    @GET("comentario/all-from-publicacao/{publicacaoId}")
    Call<List<Comentario>> getComentarios(@Path("publicacaoId") int publicacaoId);

    @GET("comentario/getUsuariosDosComentarios/{publicacaoId}")
    Call<List<Usuario>> getUsuariosDosComentarios(@Path("publicacaoId") int publicacaoId);

    @POST("comentario/inserir")
    Call<Boolean> inserirComentario(@Body Comentario comentario);

    @GET("publicacao/get-publicacoes")
    Call<List<Publicacao>> getPublicacoes();

    @POST("publicacao/publicar")
    Call<Boolean> publicar(@Body Publicacao publicacao);

    @GET("publicacao/get-publicacao-by-search/{titulo}")
    Call<List<Publicacao>> getPublicacaoBySearch(@Path("titulo") String titulo);
}
