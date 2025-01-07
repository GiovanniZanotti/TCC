package com.example.liber.model;

public class Publicacao {

    public int id;
    public String titulo;
    public String nomeAutor;
    public String descricao;
    public String localizacao;
    public int curtidas;
    public int idServico;
    public String servico;
    public int paginas;
    public int paginasLidas;
    public boolean isCurtido = false;
//    private List<Comentario> comentarios;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(int curtidas) {
        this.curtidas = curtidas;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

//    public List<Comentario> getComentarios() {
//        return comentarios;
//    }
//
//    public void setComentarios(List<Comentario> comentarios) {
//        this.comentarios = comentarios;
//    }

    public int getPaginasLidas() {
        return paginasLidas;
    }

    public void setPaginasLidas(int paginasLidas) {
        this.paginasLidas = paginasLidas;
    }
}
