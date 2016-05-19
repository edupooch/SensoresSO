package br.edu.ufcspa.threads.sensores;

public class Sensor {

    private int id;
    private int tempo;

    private double[] valores;

    public Sensor(int id, int tempo) {
        this.id = id;
        this.tempo = tempo;
        valores = new double[6000 / tempo];
    }

    public int getTempo() {
        return tempo;
    }

    public int getId() {
        return id;
    }

    public double[] getValores() {
        return valores;
    }

    public void setValor(int indice, double valor) {
        this.valores[indice] = valor;
    }

}
