package org.boligon.repository;

import org.boligon.configbanco.ConexaoBanco;
import org.boligon.entity.Solicitacao;
import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoRepository {

    public void salvar(Solicitacao solicitacao) {
        String sql = """
                INSERT INTO solicitacoes (
                    protocolo,
                    categoria,
                    descricao,
                    localizacao,
                    bairro,
                    prioridade,
                    status,
                    anonima,
                    usuario_id,
                    data_criacao,
                    prazo_sla,
                    justificativa_atraso
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, solicitacao.getProtocolo());
            statement.setString(2, solicitacao.getCategoria().name());
            statement.setString(3, solicitacao.getDescricao());
            statement.setString(4, solicitacao.getLocalizacao());
            statement.setString(5, solicitacao.getBairro());
            statement.setString(6, solicitacao.getPrioridade().name());
            statement.setString(7, solicitacao.getStatus().name());
            statement.setBoolean(8, solicitacao.isAnonima());

            if (solicitacao.getUsuarioId() != null) {
                statement.setLong(9, solicitacao.getUsuarioId());
            } else {
                statement.setNull(9, Types.BIGINT);
            }

            statement.setTimestamp(10, Timestamp.valueOf(solicitacao.exibirDataCriacaoFormatada()));
            statement.setTimestamp(11, Timestamp.valueOf(solicitacao.exibirPrazoSlaFormatada()));
            statement.setString(12, solicitacao.getJustificativaAtraso());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar solicitação.", e);
        }
    }

    public Solicitacao buscarPorProtocolo(String protocolo) {
        String sql = """
                SELECT id, protocolo, categoria, descricao, localizacao, bairro,
                       prioridade, status, anonima, usuario_id, data_criacao,
                       prazo_sla, justificativa_atraso
                FROM solicitacoes
                WHERE protocolo = ?
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, protocolo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearSolicitacao(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar solicitação por protocolo.", e);
        }
    }

    public Solicitacao buscarPorId(Long id) {
        String sql = """
                SELECT id, protocolo, categoria, descricao, localizacao, bairro,
                       prioridade, status, anonima, usuario_id, data_criacao,
                       prazo_sla, justificativa_atraso
                FROM solicitacoes
                WHERE id = ?
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearSolicitacao(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar solicitação por id.", e);
        }
    }

    public List<Solicitacao> listarPorUsuarioId(Long usuarioId) {
        String sql = """
                SELECT id, protocolo, categoria, descricao, localizacao, bairro,
                       prioridade, status, anonima, usuario_id, data_criacao,
                       prazo_sla, justificativa_atraso
                FROM solicitacoes
                WHERE usuario_id = ?
                ORDER BY data_criacao DESC
                """;

        List<Solicitacao> solicitacoes = new ArrayList<>();

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, usuarioId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    solicitacoes.add(mapearSolicitacao(resultSet));
                }
            }

            return solicitacoes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar solicitações do usuário.", e);
        }
    }

    public List<Solicitacao> listarTodas() {
        String sql = """
                SELECT id, protocolo, categoria, descricao, localizacao, bairro,
                       prioridade, status, anonima, usuario_id, data_criacao,
                       prazo_sla, justificativa_atraso
                FROM solicitacoes
                ORDER BY data_criacao DESC
                """;

        List<Solicitacao> solicitacoes = new ArrayList<>();

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                solicitacoes.add(mapearSolicitacao(resultSet));
            }

            return solicitacoes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as solicitações.", e);
        }
    }

    public void atualizarStatus(Long solicitacaoId, StatusSolicitacao novoStatus, String justificativaAtraso) {
        String sql = """
                UPDATE solicitacoes
                SET status = ?, justificativa_atraso = ?
                WHERE id = ?
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, novoStatus.name());
            statement.setString(2, justificativaAtraso);
            statement.setLong(3, solicitacaoId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da solicitação.", e);
        }
    }

    public List<Solicitacao> listarPorPrioridade(Prioridade prioridade) {
        String sql = """
                SELECT id, protocolo, categoria, descricao, localizacao, bairro,
                       prioridade, status, anonima, usuario_id, data_criacao,
                       prazo_sla, justificativa_atraso
                FROM solicitacoes
                WHERE prioridade = ?
                ORDER BY data_criacao DESC
                """;

        List<Solicitacao> solicitacoes = new ArrayList<>();

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, prioridade.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    solicitacoes.add(mapearSolicitacao(resultSet));
                }
            }

            return solicitacoes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar solicitações por prioridade.", e);
        }
    }

    public List<Solicitacao> listarPorBairro(String bairro) {
        String sql = """
                SELECT id, protocolo, categoria, descricao, localizacao, bairro,
                       prioridade, status, anonima, usuario_id, data_criacao,
                       prazo_sla, justificativa_atraso
                FROM solicitacoes
                WHERE bairro = ?
                ORDER BY data_criacao DESC
                """;

        List<Solicitacao> solicitacoes = new ArrayList<>();

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, bairro);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    solicitacoes.add(mapearSolicitacao(resultSet));
                }
            }

            return solicitacoes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar solicitações por bairro.", e);
        }
    }

    public List<Solicitacao> listarPorStatus(StatusSolicitacao status) {
        String sql = """
                SELECT id, protocolo, categoria, descricao, localizacao, bairro,
                       prioridade, status, anonima, usuario_id, data_criacao,
                       prazo_sla, justificativa_atraso
                FROM solicitacoes
                WHERE status = ?
                ORDER BY data_criacao DESC
                """;

        List<Solicitacao> solicitacoes = new ArrayList<>();

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    solicitacoes.add(mapearSolicitacao(resultSet));
                }
            }

            return solicitacoes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar solicitações por status.", e);
        }
    }

    private Solicitacao mapearSolicitacao(ResultSet resultSet) throws SQLException {
        Solicitacao solicitacao = new Solicitacao();

        solicitacao.setId(resultSet.getLong("id"));
        solicitacao.setProtocolo(resultSet.getString("protocolo"));
        solicitacao.setCategoria(Categoria.valueOf(resultSet.getString("categoria")));
        solicitacao.setDescricao(resultSet.getString("descricao"));
        solicitacao.setLocalizacao(resultSet.getString("localizacao"));
        solicitacao.setBairro(resultSet.getString("bairro"));
        solicitacao.setPrioridade(Prioridade.valueOf(resultSet.getString("prioridade")));
        solicitacao.setStatus(StatusSolicitacao.valueOf(resultSet.getString("status")));
        solicitacao.setAnonima(resultSet.getBoolean("anonima"));

        long usuarioId = resultSet.getLong("usuario_id");
        if (resultSet.wasNull()) {
            solicitacao.setUsuarioId(null);
        } else {
            solicitacao.setUsuarioId(usuarioId);
        }

        Timestamp dataCriacao = resultSet.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            solicitacao.setDataCriacao(dataCriacao.toLocalDateTime());
        }

        Timestamp prazoSla = resultSet.getTimestamp("prazo_sla");
        if (prazoSla != null) {
            solicitacao.setPrazoSla(prazoSla.toLocalDateTime());
        }

        solicitacao.setJustificativaAtraso(resultSet.getString("justificativa_atraso"));

        return solicitacao;
    }
}