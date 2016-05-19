package br.edu.ufcspa.threads.controle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import br.edu.ufcspa.threads.R;
import br.edu.ufcspa.threads.modelo.Modelo;
import br.edu.ufcspa.threads.sensores.FrequenciaCardiaca;
import br.edu.ufcspa.threads.sensores.Sensor;
import br.edu.ufcspa.threads.sensores.SpO2;
import br.edu.ufcspa.threads.sensores.Temperatura;

public class MainActivity extends AppCompatActivity {

    private int controlaFc; //CONTROLAM A INSERÇÃO NO ARRAY DE VALORES
    private int controlaSp;
    private int controlaTemp;

    private double[] medias;
    private TextView[] texts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciaComponentes();

        Sensor fc = new FrequenciaCardiaca();
        Sensor sp = new SpO2();
        Sensor temp = new Temperatura();

        geraValores(fc);
        geraValores(sp);
        geraValores(temp);

        geraMedias();

    }


    private void iniciaComponentes() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        medias = new double[3];
        texts = new TextView[3];
        texts[0] = (TextView) findViewById(R.id.textSensorFc);
        texts[1] = (TextView) findViewById(R.id.textSensorSp);
        texts[2] = (TextView) findViewById(R.id.textSensorTemperatura);
    }

    private void geraValores(final Sensor sensor) { //PRODUTOR

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Random ran = new Random();
                        double valor = 0; //VALOR QUE SERÁ GERADO PARA O SENSOR

                        switch (sensor.getId()) { //VERIFICA QUAL O SENSOR
                            case 0://FC
                                valor = ran.nextInt(20) + 60; //GERA UM NÚMERO ALEATÓRIO DE 60-80 BPM
                                if (controlaFc == 6) controlaFc = 0;
                                sensor.setValor(controlaFc, valor);
                                controlaFc++;
                                break;
                            case 1://SPO2
                                valor = ran.nextInt(5) + 95; //GERA UM NÚMERO ALEATORIO DE 95-100
                                if (controlaSp == 3) controlaSp = 0;
                                sensor.setValor(controlaSp, valor);
                                controlaSp++;
                                break;
                            case 2://TEMPEATURA
                                valor = ran.nextDouble() + 36; //GERA UM NÚMERO ALEATORIO DE 36.0 ATÉ 37.0
                                if (controlaTemp == 2) controlaTemp = 0;
                                sensor.setValor(controlaTemp, valor);
                                controlaTemp++;
                                break;
                        }

                        synchronized (this) { //SEÇÃO CRÍTICA DO PRODUTOR
                            Modelo.setArrayDaEstruturaCompartilhada(sensor.getId(), sensor.getValores());
                            //INSERE O VETOR DE VALORES NA ESTRUTURA COMPARTILHADA
                        }
                        Thread.sleep(sensor.getTempo());
                    } catch (InterruptedException e) {
                        // O SLEEP LANÇA ESSA EXCEPTION QUANDO OUTRA THREAD TENTA INTERROMPER ESSA ENQUANTO O SLEEP ESTÁ ATIVO
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void geraMedias() { //CONSUMIDOR

        new Thread() {
            public void run() {

                try {
                    Thread.sleep(6000);//DELAY INICIAL
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                while (true) {
                    try {

                        synchronized (this) { //SEÇÃO CRÍTICA DO CONSUMIDOR

                            //CALCULA A MÉDIA USANDO A ESTRUTURA COMPARTILHADA E SALVA NO VETOR MÉDIAS

                            for (int i = 0; i < Modelo.getEstruturaCompartilhada().length; i++) {

                                double soma = 0;
                                for (double valor : Modelo.getArrayDaEstruturaCompartilhada(i))
                                    soma += valor;
                                medias[i] = soma / Modelo.getArrayDaEstruturaCompartilhada(i).length;

                            }

                        }
                        runOnUiThread(new Runnable() { //MÉTODOS QUE ALTERAM A VIEW
                            @Override
                            public void run() {
                                //FORMATA DOUBLE PARA MUDAR NÚMERO DE CASAS APÓS A VÍRGULA
                                String strMedia = String.format(Locale.getDefault(), "%.0f", medias[0]);
                                texts[0].setText(strMedia);

                                strMedia = String.format(Locale.getDefault(), "%.0f", medias[1]);
                                texts[1].setText(strMedia);

                                strMedia = String.format(Locale.getDefault(), "%.1f", medias[2]);
                                texts[2].setText(strMedia);
                            }
                        });
                        Thread.sleep(6000); //CALCULA MÉDIA DE 6 EM 6 SEGUNDOS
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
