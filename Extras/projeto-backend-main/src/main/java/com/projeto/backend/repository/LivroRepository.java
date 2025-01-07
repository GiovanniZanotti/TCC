package com.projeto.backend.repository;

import com.projeto.backend.database.ConexaoDB;
import com.projeto.backend.exceptions.BusinessException;
import com.projeto.backend.model.Livro;
import com.projeto.backend.util.MessageUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivroRepository {

    public List<Livro> findAll() {
        ConexaoDB conexao = new ConexaoDB();
        List<Livro> livros = new ArrayList<>();
        Livro livro;

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM livro");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                livro = getLivro(rs);
                livros.add(livro);
            }

            return livros;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public Livro getById(int id) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM livro WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return getLivro(rs);
            } else {
                return null;
            }

        } catch(Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public List<Livro> getBiblioteca(int id) {
        ConexaoDB conexao = new ConexaoDB();
        List<Livro> livros = new ArrayList<>();
        Livro livro;

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT livro.*, usuario_livro.paginasLidas FROM livro " +
                                                             "JOIN usuario_livro ON usuario_livro.idLivro = livro.id " +
                                                             "JOIN usuario ON usuario.id = usuario_livro.idUsuario " +
                                                             "WHERE usuario.id = ? ORDER BY livro.titulo");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                livro = getLivro(rs);
                livro.setPaginasLidas(rs.getInt("paginasLidas"));

                livros.add(livro);
            }

            return livros;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public Boolean cadastrar(Livro livro) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO livro VALUES (default, ?, ?, ?, ?, ?)");
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getNomeAutor());
            ps.setString(3, livro.getDescricao());
            ps.setInt(4, livro.getCurtidas());
            ps.setInt(5, livro.getPaginas());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public void curtirLivro(int id) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("CALL curtir_livro(?)");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public Boolean deletar(int id) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM livro WHERE id = ?");
            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    private Livro getLivro(ResultSet rs) throws SQLException {
        Livro livro;
        livro = new Livro();
        livro.setId(rs.getInt("id"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setNomeAutor(rs.getString("autor"));
        livro.setDescricao(rs.getString("descricao"));
        livro.setCurtidas(rs.getInt("curtida"));
        livro.setPaginas(rs.getInt("paginas"));
        return livro;
    }
	
	public List<Livro> getLivroTitulo(String titulo) {
        ConexaoDB conexao = new ConexaoDB();
        List<Livro> livros = new ArrayList<>();
        Livro livro;

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM livro WHERE titulo LIKE CONCAT('%', ?, '%')");
            ps.setString(1, titulo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                livro = getLivro(rs);
                livros.add(livro);
            }

            return livros;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }
}
