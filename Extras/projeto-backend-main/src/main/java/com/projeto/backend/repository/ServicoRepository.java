package com.projeto.backend.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.projeto.backend.database.ConexaoDB;
import com.projeto.backend.exceptions.BusinessException;
import com.projeto.backend.model.Servico;
import com.projeto.backend.util.MessageUtils;

public class ServicoRepository {

    public List<Servico> verServicos() {
    	ConexaoDB conexao = new ConexaoDB();
        List<Servico> servicos = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from servico;");

            return getListServicos(ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    public List<Servico> getBySearch(String nome) {
        ConexaoDB conexao = new ConexaoDB();
        List<Servico> usuarios = new ArrayList<>();

        try {
            Connection conn = conexao.openConnection();
            PreparedStatement ps = conn.prepareStatement("select * from servico where nome like CONCAT('%', ?, '%');");
            ps.setString(1, nome);


            return getListServicos(ps);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.DATABASE_ERROR);
        } finally {
            conexao.closeConnection();
        }
    }

    private List<Servico> getListServicos(PreparedStatement ps) throws SQLException {
    	List<Servico> servicos = new ArrayList<>();
        Servico servico;

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
        	servico = new Servico();
        	servico.id = rs.getInt("id");
        	servico.nome = rs.getString("nome");

        	servicos.add(servico);
        }

        return servicos;
    }
}
