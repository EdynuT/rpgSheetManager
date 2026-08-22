package src.model;

public class Contato {
    private Integer id;
    private String nome;
    private String email;

    public Contato(Integer id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Contato(String nome, String email) {
        this(null, nome, email);
    }
    
    public Integer getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nome + " (" + email + ")";
    }
}
