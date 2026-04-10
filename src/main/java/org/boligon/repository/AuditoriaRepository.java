package org.boligon.repository;

import org.boligon.configbanco.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AuditoriaRepository {

    public void registrar(Long usuarioId, String tipoAcao, String detalhe) {
        String sql = """
                INSERT INTO auditoria (usuario_id, tipo_acao, detalhe, data_registro)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (usuarioId != null) {
                statement.setLong(1, usuarioId);
            } else {
                statement.setObject(1, null);
            }
            statement.setString(2, tipoAcao);
            statement.setString(3, truncar(detalhe, 500));
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar auditoria.", e);
        }
    }

    private static String truncar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= max) {
            return texto;
        }
        return texto.substring(0, max);
    }
}
