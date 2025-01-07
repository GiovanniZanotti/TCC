package com.projeto.backend.repository;

import com.projeto.backend.database.ConexaoDB;
import com.projeto.backend.exceptions.BusinessException;
import com.projeto.backend.model.Comentario;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.util.MessageUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComentarioRepository {

    public Comentario getById(int id) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM comentario WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return getComentario(rs);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }
    
    public List<Usuario> getUsuariosDosComentarios(int publicacaoId) {
        ConexaoDB conexao = new ConexaoDB();
        
        List<Usuario> usuarios = new ArrayList<Usuario>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from usuario "
				            		+ "inner join comentario on comentario.usuario_id = usuario.id "
				            		+ "where comentario.publicacao_id = ?;");
            ps.setInt(1, publicacaoId);
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	
            	Usuario usuario = new Usuario();
            	
            	usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
            	
                usuarios.add(usuario);
            } 
            
            return usuarios;
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (BusinessException e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
        
        return null;
    }

    public List<Comentario> getComentariosUsuario(int id) {
        ConexaoDB conexao = new ConexaoDB();
        List<Comentario> comentarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM comentario WHERE usuario_id = ?");
            return getListComentarios(id, comentarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public List<Comentario> getComentariosPublicacao(int id) {
        ConexaoDB conexao = new ConexaoDB();
        List<Comentario> comentarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM comentario WHERE publicacao_id = ?");
            return getListComentarios(id, comentarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public Boolean inserirComentario(Comentario comentario) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO comentario(comentario, usuario_id, publicacao_id) VALUES (?, ?, ?)");
            ps.setString(1, comentario.getTexto());
            ps.setInt(2, comentario.getIdUsuario());
            ps.setInt(3, comentario.getIdLivro());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public Boolean excluirComentario(int id) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM comentario WHERE id = ?");
            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    private List<Comentario> getListComentarios(int id, List<Comentario> comentarios, PreparedStatement ps) throws SQLException {
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Comentario comentario = getComentario(rs);
            comentarios.add(comentario);
        }

        return comentarios;
    }

    private Comentario getComentario(ResultSet rs) throws SQLException {
        Comentario comentario;
        comentario = new Comentario();
        comentario.setId(rs.getInt("id"));
        comentario.setTexto(rs.getString("comentario"));
        comentario.setIdUsuario(rs.getInt("usuario_id"));
        comentario.setIdLivro(rs.getInt("publicacao_id"));
        return comentario;
    }
}
