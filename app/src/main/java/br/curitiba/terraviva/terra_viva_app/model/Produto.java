package br.curitiba.terraviva.terra_viva_app.model;

public class Produto {
    private int id;
    private String nome;
    private String curta;
    private String longa;
    private int categ;
    private float valor;
    private String img;
    private boolean destaque;
    private int estoque;

    public Produto() {
    }

    public Produto(int id, String nome, String curta, String longa, int categ, float valor, String img, boolean destaque, int estoque) {
        this.id = id;
        this.nome = nome;
        this.curta = curta;
        this.longa = longa;
        this.categ = categ;
        this.valor = valor;
        this.img = img;
        this.destaque = destaque;
        this.estoque = estoque;
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

    public String getCurta() {
        return curta;
    }

    public void setCurta(String curta) {
        this.curta = curta;
    }

    public String getLonga() {
        return longa;
    }

    public void setLonga(String longa) {
        this.longa = longa;
    }

    public int getCateg() {
        return categ;
    }

    public void setCateg(int categ) {
        this.categ = categ;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isDestaque() {
        return destaque;
    }

    public void setDestaque(boolean destaque) {
        this.destaque = destaque;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
}
