package com.projeto.backend.repository;

import com.projeto.backend.database.ConexaoDB;
import com.projeto.backend.exceptions.BusinessException;
import com.projeto.backend.exceptions.NotFoundException;
import com.projeto.backend.exceptions.UserAlreadyExistsException;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.util.MessageUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    public List<Usuario> findAll() {
        ConexaoDB conexao = new ConexaoDB();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM usuario");
            return getListUsuario(usuarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }
    
    public Usuario getUsuario(Usuario usuario) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM usuario where email = ?");
            ps.setString(1, usuario.getEmail());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuario.setId(rs.getInt("id"));
            }

            return usuario;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null; //TODO Throw Exception
        } finally {
            conexao.closeConnection();
        }
    }

    public Usuario login(String email, String senha) {
        ConexaoDB conexao = new ConexaoDB();
        Usuario usuario = new Usuario();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, nome, email, idCidade FROM usuario WHERE email = ? AND senha = BINARY ?");
            ps.setString(1, email);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return getUsuario(usuario, rs);
            } else {
                throw new NotFoundException();
            }

        } catch (NotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public List<Usuario> getSeguidores(int id) {
        ConexaoDB conexao = new ConexaoDB();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, nome, email, admin, data_cadastro FROM usuario " +
                                                             "WHERE id IN (SELECT idSeguidor FROM seguidor WHERE idUsuario = ?)");
            ps.setInt(1, id);
            return getListUsuario(usuarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public Usuario getById(int id) {
        ConexaoDB conexao = new ConexaoDB();
        Usuario usuario = new Usuario();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, nome, email, admin, data_cadastro FROM usuario WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return getUsuario(usuario, rs);
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public void cadastrar(Usuario usuario, String dataCadastro) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO usuario VALUES (default, ?, ?, ?, ?, ?)");
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setBoolean(4, false);
            ps.setString(5, dataCadastro);

            if (ps.executeUpdate() == 1) {
                usuario.setData_cadastro(dataCadastro);
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
    }

    public boolean deletar(int id) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM usuario WHERE id = ?");
            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        }
    }

    public List<Usuario> getBySearch(String nome) {
        ConexaoDB conexao = new ConexaoDB();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM usuario WHERE nome LIKE CONCAT('%', ?, '%')");
            ps.setString(1, nome);

            return getListUsuario(usuarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        }
    }

    private Usuario getUsuario(Usuario usuario, ResultSet rs) throws SQLException {
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));

        return usuario;
    }

    private List<Usuario> getListUsuario(List<Usuario> usuarios, PreparedStatement ps) throws SQLException {
        Usuario usuario;
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            usuario = new Usuario();
            usuario.setId(rs.getInt("id"));
            usuario.setNome(rs.getString("nome"));
            usuario.setEmail(rs.getString("email"));
            usuario.setAdmin(rs.getBoolean("admin"));
            usuario.setData_cadastro(rs.getString("data_cadastro"));

            usuarios.add(usuario);
        }

        return usuarios;
    }
}
