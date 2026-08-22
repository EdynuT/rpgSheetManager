package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import src.model.Contato;
import src.config.Banco;

public class ContatoDAO {
    public void salvar(Contato contato) throws SQLException {
        String sql = "INSERT INTO contato (nome, email) "
                    + "VALUES (?, ?)";

        try (Connection con = Banco.conectar(); 
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, contato.getNome());
            ps.setString(2, contato.getEmail());

            ps.executeUpdate();
        } 
        catch (SQLException e) {
            System.err.printf("Erro ao salvar contato: %s", e.getMessage());
        }
    }
}
