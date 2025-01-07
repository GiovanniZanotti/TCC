package com.projeto.backend.repository;

import com.projeto.backend.database.ConexaoDB;
import com.projeto.backend.exceptions.BusinessException;
import com.projeto.backend.exceptions.SeguidorAlreadyExistsException;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.util.MessageUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeguidorRepository {

    public boolean seguir(int idUsuario, int idUsuarioASeguir) {
    	ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("insert into seguidor(idUsuario, idSeguidor) values(?, ?);");
            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuarioASeguir);

            return ps.executeUpdate() == 1;
            
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SeguidorAlreadyExistsException();
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }
    
    public boolean deixarDeSeguir(int idUsuario, int idUsuarioDesseguir) {
    	ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("delete from seguidor where idUsuario = ? and idSeguidor = ?;");
            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuarioDesseguir);

            return ps.executeUpdate() == 1;
            
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }
    
    public List<Usuario> verSeguindo(int idUsuario) {
    	ConexaoDB conexao = new ConexaoDB();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from usuario where id in "
							            			   + "(select seguidor.idSeguidor from seguidor "
								            		   + "inner join usuario on seguidor.idusuario = usuario.id "
								            		   + "where usuario.id = ?);");

            return getListUsuarios(idUsuario, usuarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public List<Usuario> verSeguidores(int idUsuario) {
    	ConexaoDB conexao = new ConexaoDB();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from usuario where id in "
										        	   + "(select seguidor.idUsuario from seguidor "
										        	   + "inner join usuario on seguidor.idSeguidor = usuario.id "
										        	   + "where usuario.id = ?);");

            return getListUsuarios(idUsuario, usuarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public int getSeguidoresCount(int usuarioId) {
        ConexaoDB conexao = new ConexaoDB();
        int qtd = 0;

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) qtd FROM seguidor WHERE idSeguidor = ?");
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                qtd = rs.getInt("qtd");
            }

            return qtd;
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public List<Usuario> getBySearch(int usuarioId, String nome) {
        ConexaoDB conexao = new ConexaoDB();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from usuario where id in "
                    + "(select seguidor.idUsuario from seguidor "
                    + "inner join usuario on seguidor.idSeguidor = usuario.id "
                    + "where usuario.id = ?) and nome like CONCAT('%', ?, '%');");
            ps.setInt(1, usuarioId);
            ps.setString(2, nome);


            return getListUsuarios(usuarioId, usuarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }
    
    public List<Usuario> getSeguindoBySearch(int usuarioId, String nome) {
        ConexaoDB conexao = new ConexaoDB();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from usuario where id in "
            		+ "(select seguidor.idSeguidor from seguidor "
            		+ "inner join usuario on seguidor.idUsuario = usuario.id "
        			+ "where usuario.id = ?) and nome like CONCAT('%', ?, '%');");
            ps.setInt(1, usuarioId);
            ps.setString(2, nome);

            return getListUsuarios(usuarioId, usuarios, ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    private List<Usuario> getListUsuarios(int idUsuario, List<Usuario> usuarios, PreparedStatement ps) throws SQLException {
        Usuario usuario;
        ps.setInt(1, idUsuario);
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
