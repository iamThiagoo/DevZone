package br.com.devzone;

public class Course {
    private String nome;
    private int quantidade;
    private String caminhoImagem;

    public Course(String nome, int quantidade, String caminhoImagem) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.caminhoImagem = caminhoImagem;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }
}
