package br.edu.ufcspa.threads.modelo;

public class Modelo {

    private static double[][] estruturaCompartilhada = new double[3][];
    //ARRAY DE ARRAY

    public static double[][] getEstruturaCompartilhada() {
        return estruturaCompartilhada;
    }

    public static double[] getArrayDaEstruturaCompartilhada(int i) {
        return estruturaCompartilhada[i];
    }

    public static void setArrayDaEstruturaCompartilhada(int i, double[] valores) {
        estruturaCompartilhada[i] = valores;
    }
}
