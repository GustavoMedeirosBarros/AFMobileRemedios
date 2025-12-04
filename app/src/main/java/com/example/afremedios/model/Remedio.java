package com.example.afremedios.model;

public class Remedio {
    private String id;
    private String nome;
    private String descricao;
    private String horario;
    private boolean tomado;

    public Remedio() {
    }

    public Remedio(String id, String nome, String descricao, String horario, boolean tomado) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.horario = horario;
        this.tomado = tomado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public boolean isTomado() {
        return tomado;
    }

    public void setTomado(boolean tomado) {
        this.tomado = tomado;
    }
}
