package br.curitiba.terraviva.terra_viva_app.model;

public class Subcateg {
    private int id;
    private String nome;
    private int cat;

    public Subcateg() {
    }

    public Subcateg(int id, String nome, int cat) {
        this.id = id;
        this.nome = nome;
        this.cat = cat;
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

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }
}
