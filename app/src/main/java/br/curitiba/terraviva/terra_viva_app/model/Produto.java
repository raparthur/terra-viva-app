package br.curitiba.terraviva.terra_viva_app.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Produto implements Comparable,Serializable {
    private int id;
    private String nome;
    private String curta;
    private String longa;
    private int categ;
    private float valor;
    private String img;
    private boolean destaque;
    private int estoque;
    private float peso;
    private int altura;
    private int largura;
    private int comprimento;

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }

    public int getComprimento() {
        return comprimento;
    }

    public void setComprimento(int comprimento) {
        this.comprimento = comprimento;
    }

    public Produto() {
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


    @Override
    public int compareTo(@NonNull Object o) {
        if(o instanceof Produto){
            if(this.valor >((Produto) o).getValor())
                return 1;
            if(this.valor < ((Produto) o).getValor())
                return -1;
        }
        return 0;
    }
}
