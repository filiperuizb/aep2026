package org.boligon.repository;

import org.boligon.configbanco.ConexaoBanco;
import org.boligon.entity.Usuario;
import org.boligon.enums.PerfilUsuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioRepository {

    public void salvar(Usuario usuario) {
        String sql = """
                INSERT INTO usuarios (nome, email, senha, perfil, ativo)
               VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getPerfil().getValor());
            statement.setBoolean(5, usuario.getAtivo());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário.", e);
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = """
                SELECT id, nome, email, senha, perfil, ativo
                FROM usuarios
                WHERE email = ?
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearUsuario(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por email.", e);
        }
    }

    public Usuario buscarPorId(Long id) {
        String sql = """
                SELECT id, nome, email, senha, perfil, ativo
                FROM usuarios
                WHERE id = ?
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearUsuario(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por id.", e);
        }
    }

    public boolean existePorEmail(String email) {
        String sql = """
                SELECT 1
                FROM usuarios
                WHERE email = ?
                """;

        try (Connection connection = ConexaoBanco.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar se email já existe.", e);
        }
    }

    private Usuario mapearUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(resultSet.getLong("id"));
        usuario.setNome(resultSet.getString("nome"));
        usuario.setEmail(resultSet.getString("email"));
        usuario.setSenha(resultSet.getString("senha"));
        usuario.setPerfil(PerfilUsuario.valueOf(resultSet.getString("perfil")));
        usuario.setAtivo(resultSet.getBoolean("ativo"));
        return usuario;
    }
}