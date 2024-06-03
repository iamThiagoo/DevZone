package br.com.devzone.classes;

public class Category {
    private String nome;
    private String urlImagem;

    public Category(String nome, String urlImagem) {
        this.nome = nome;
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public String getUrlImagem() {
        return urlImagem;
    }
}
