package org.boligon.configbanco;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InicioBanco {

    public void inicializarBanco() {
        try (Connection connection = ConexaoBanco.getConnection();
             Statement statement = connection.createStatement()) {

            criarTabelaUsuarios(statement);
            criarTabelaSolicitacoes(statement);
            criarTabelaHistoricoStatus(statement);
            criarTabelaComentarios(statement);
            criarTabelaAuditoria(statement);
            garantirColunaAnexoSolicitacoes(statement);
            inserirUsuarioGestorPadrao(statement);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar o banco de dados.", e);
        }
    }

    private void criarTabelaUsuarios(Statement statement) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(150) NOT NULL,
                email VARCHAR(150) NOT NULL UNIQUE,
                senha VARCHAR(100) NOT NULL,
                perfil VARCHAR(30) NOT NULL,
                ativo BOOLEAN NOT NULL
            );
            """;

        statement.execute(sql);
    }

    private void criarTabelaSolicitacoes(Statement statement) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS solicitacoes (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                protocolo VARCHAR(30) NOT NULL UNIQUE,
                categoria VARCHAR(50) NOT NULL,
                descricao VARCHAR(1000) NOT NULL,
                anexo VARCHAR(255),
                localizacao VARCHAR(255) NOT NULL,
                bairro VARCHAR(100) NOT NULL,
                prioridade VARCHAR(20) NOT NULL,
                status VARCHAR(30) NOT NULL,
                anonima BOOLEAN NOT NULL,
                usuario_id BIGINT,
                data_criacao TIMESTAMP NOT NULL,
                prazo_sla TIMESTAMP NOT NULL,
                justificativa_atraso VARCHAR(500),
                CONSTRAINT fk_solicitacao_usuario
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
            );
            """;

        statement.execute(sql);
    }

    private void criarTabelaHistoricoStatus(Statement statement) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS historico_status (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                solicitacao_id BIGINT NOT NULL,
                status_anterior VARCHAR(30),
                status_novo VARCHAR(30) NOT NULL,
                comentario VARCHAR(500) NOT NULL,
                responsavel_id BIGINT NOT NULL,
                data_movimentacao TIMESTAMP NOT NULL,
                CONSTRAINT fk_historico_solicitacao
                    FOREIGN KEY (solicitacao_id) REFERENCES solicitacoes(id),
                CONSTRAINT fk_historico_responsavel
                    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id)
            );
            """;

        statement.execute(sql);
    }

    private void criarTabelaComentarios(Statement statement) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS comentarios (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                solicitacao_id BIGINT NOT NULL,
                usuario_id BIGINT,
                mensagem VARCHAR(500) NOT NULL,
                data_comentario TIMESTAMP NOT NULL,
                anonimo BOOLEAN NOT NULL,
                CONSTRAINT fk_comentario_solicitacao
                    FOREIGN KEY (solicitacao_id) REFERENCES solicitacoes(id),
                CONSTRAINT fk_comentario_usuario
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
            );
            """;

        statement.execute(sql);
    }

    private void criarTabelaAuditoria(Statement statement) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS auditoria (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                usuario_id BIGINT,
                tipo_acao VARCHAR(80) NOT NULL,
                detalhe VARCHAR(500) NOT NULL,
                data_registro TIMESTAMP NOT NULL,
                CONSTRAINT fk_auditoria_usuario
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
            );
            """;

        statement.execute(sql);
    }

    private void garantirColunaAnexoSolicitacoes(Statement statement) throws SQLException {
        statement.execute("ALTER TABLE solicitacoes ADD COLUMN IF NOT EXISTS anexo VARCHAR(255)");
    }

    private void inserirUsuarioGestorPadrao(Statement statement) throws SQLException {
        String sql = """
            MERGE INTO usuarios (id, nome, email, senha, perfil, ativo)
            KEY(id)
            VALUES (
                1,
                'Administrador Teste',
                'admin@admin.com',
                '123',
                'GESTOR',
                TRUE
            );
            """;

        statement.execute(sql);
    }
}