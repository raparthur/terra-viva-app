package br.curitiba.terraviva.terra_viva_app.model;

import java.util.Date;

public class Usuario {
    private String email;
    private String nome;
    private Date nasc;
    private String cpf;
    private String senha;
    private String rua;
    private String num;
    private String compl;
    private String bairro;
    private String cidade;
    private String uf;

    public Usuario(String email, String nome, Date nasc, String cpf, String senha, String rua, String num, String compl, String bairro, String cidade, String uf) {
        this.email = email;
        this.nome = nome;
        this.nasc = nasc;
        this.cpf = cpf;
        this.senha = senha;
        this.rua = rua;
        this.num = num;
        this.compl = compl;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }

    public Usuario() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getNasc() {
        return nasc;
    }

    public void setNasc(Date nasc) {
        this.nasc = nasc;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCompl() {
        return compl;
    }

    public void setCompl(String compl) {
        this.compl = compl;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
