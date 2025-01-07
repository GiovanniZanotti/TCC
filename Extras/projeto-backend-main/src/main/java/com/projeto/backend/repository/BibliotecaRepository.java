package com.projeto.backend.repository;

import java.sql.Connection;
import java.sql.Date;
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
import com.projeto.backend.model.Livro;
import com.projeto.backend.model.Usuario;
import com.projeto.backend.util.MessageUtils;

public class BibliotecaRepository {

    public boolean adicionarLivro(int usuarioId, int livroId) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("insert into usuario_livro(idUsuario, idLivro) values (?, ?);");
            ps.setInt(1, usuarioId);
            ps.setInt(2, livroId);

            if (ps.executeUpdate() == 1) {
                return true;
            } 
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }catch (BusinessException e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
        
        return false;
    }

    public boolean removerLivro(int usuarioId, int livroId) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("delete from usuario_livro where idUsuario = ? and idLivro = ?;");
            ps.setInt(1, usuarioId);
            ps.setInt(2, livroId);

            if (ps.executeUpdate() == 1) {
                return true;
            } 
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }catch (BusinessException e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
        
        return false;
    }
    
    public boolean getLivro(int usuarioId, int livroId) {
        ConexaoDB conexao = new ConexaoDB();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from usuario_livro where idUsuario = ? and idLivro = ?;");
            ps.setInt(1, usuarioId);
            ps.setInt(2, livroId);
            
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            } 
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }catch (BusinessException e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
        
        return false;
    }
}
