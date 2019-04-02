package br.curitiba.terraviva.terra_viva_app.model;

public class Categoria{
    private int id;
    private String nome;
    private String img;

    public Categoria(int id, String nome, String img) {
        this.id = id;
        this.nome = nome;
        this.img = img;
    }

    public Categoria() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
