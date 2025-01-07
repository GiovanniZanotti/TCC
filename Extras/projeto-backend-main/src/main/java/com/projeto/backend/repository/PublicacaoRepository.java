package com.projeto.backend.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.projeto.backend.database.ConexaoDB;
import com.projeto.backend.exceptions.BusinessException;
import com.projeto.backend.exceptions.NotFoundException;
import com.projeto.backend.exceptions.UserAlreadyExistsException;
import com.projeto.backend.model.Publicacao;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.util.MessageUtils;

public class PublicacaoRepository {

    public List<Publicacao> verPublicacoes() {
    	ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from publicacao;");

            return getListPublicacoes(ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }
    
    public boolean publicar(Publicacao publicacao) {
        ConexaoDB conexao = new ConexaoDB();
        boolean publicacaoResult = false;

        try {
            Connection conn = conexao.openConnection();
            //PreparedStatement ps = conn.prepareStatement("INSERT INTO publicacao VALUES (default, ?, ?, ?)");
            PreparedStatement ps = conn.prepareStatement("INSERT INTO publicacao(titulo, descricao, localizacao, idServico, curtidas) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, publicacao.titulo);
            ps.setString(2, publicacao.descricao);
            ps.setString(3, publicacao.localizacao);
            ps.setInt(4, publicacao.idServico);
            ps.setInt(5, publicacao.curtidas);

            if (ps.executeUpdate() == 1) {
            	publicacaoResult = true;
            } else {
                throw new BusinessException(MessageUtils.DATABASE_ERROR);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UserAlreadyExistsException();
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
        
        return publicacaoResult;
    }

    public List<Publicacao> getBySearch(String titulo) {
        ConexaoDB conexao = new ConexaoDB();
        List<Publicacao> publicacaos = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from publicacao where titulo like CONCAT('%', ?, '%');");
            ps.setString(1, titulo);


            return getListPublicacoes(ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    private List<Publicacao> getListPublicacoes(PreparedStatement ps) throws SQLException {
    	List<Publicacao> publicacoes = new ArrayList<>();
    	Publicacao publicacao;

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
        	publicacao = new Publicacao();
        	publicacao.id = rs.getInt("id");
        	publicacao.titulo = rs.getString("titulo");
        	publicacao.descricao = rs.getString("descricao");
        	publicacao.localizacao = rs.getString("localizacao");
        	publicacao.curtidas = rs.getInt("curtidas");
        	publicacao.idServico = rs.getInt("idServico");
        	
        	publicacao.imagens = getImagens(publicacao.id);

        	publicacoes.add(publicacao);
        }

        return publicacoes;
    }
    
    private List<String> getImagens(int idPublicacao){
    	ConexaoDB conexao = new ConexaoDB();
    	List<String> imagens = new ArrayList<String>();
        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT dados FROM imagem WHERE idRequerimento = ?");
            ps.setInt(1, idPublicacao);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	imagens.add(rs.getString("dados"));
            }

        } catch (NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
        
		return imagens;
    }
}
