package src.service;

import java.sql.SQLException;
import src.dao.ContatoDAO;
import src.model.Contato;

public class ContatoService {
    private ContatoDAO dao;

    public ContatoService() {
        this.dao = new ContatoDAO();
    }

    public void cadastrarContato(String nome, String email) throws SQLException {
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("Aviso: O nome não pode ser vazio");
        } 
        
        if (email == null || !email.contains("@")) {
            System.out.println("Aviso: O email inválido");
        }

        Contato novoContato = new Contato(nome, email);
        dao.salvar(novoContato);
        System.out.println("Contato cadastrado com sucesso!");
    }
}
