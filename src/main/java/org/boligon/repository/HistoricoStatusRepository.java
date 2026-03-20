package org.boligon.repository;

import org.boligon.entity.HistoricoStatus;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.configbanco.ConexaoBanco;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoricoStatusRepository {

    public void salvar(HistoricoStatus historicoStatus) {

        String sql = """
                INSERT INTO historico_status (
                    solicitacao_id,
                    status_anterior,
                    status_novo,
                    comentario,
                    responsavel_id,
                    data_movimentacao
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, historicoStatus.getSolicitacaoId());

            if (historicoStatus.getStatusAnterior() != null) {
                statement.setString(2, historicoStatus.getStatusAnterior().name());
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            statement.setString(3, historicoStatus.getStatusNovo().name());
            statement.setString(4, historicoStatus.getComentario());
            statement.setLong(5, historicoStatus.getResponsavelId());

            statement.setTimestamp(6, Timestamp.valueOf(historicoStatus.getDataMovimentacao()));

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar histórico de status.", e);
        }
    }

    public List<HistoricoStatus> listarPorSolicitacaoId(Long solicitacaoId) {

        String sql = """
                SELECT *
                FROM historico_status
                WHERE solicitacao_id = ?
                ORDER BY data_movimentacao DESC
                """;

        List<HistoricoStatus> lista = new ArrayList<>();

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, solicitacaoId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                lista.add(mapearHistoricoStatus(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar histórico de status.", e);
        }

        return lista;
    }

    private HistoricoStatus mapearHistoricoStatus(ResultSet rs) throws SQLException {

        HistoricoStatus historico = new HistoricoStatus();

        historico.setId(rs.getLong("id"));
        historico.setSolicitacaoId(rs.getLong("solicitacao_id"));

        String statusAnterior = rs.getString("status_anterior");
        if (statusAnterior != null) {
            historico.setStatusAnterior(StatusSolicitacao.valueOf(statusAnterior));
        }

        historico.setStatusNovo(StatusSolicitacao.valueOf(rs.getString("status_novo")));
        historico.setComentario(rs.getString("comentario"));
        historico.setResponsavelId(rs.getLong("responsavel_id"));

        Timestamp timestamp = rs.getTimestamp("data_movimentacao");
        if (timestamp != null) {
            historico.setDataMovimentacao(timestamp.toLocalDateTime());
        }

        return historico;
    }
}