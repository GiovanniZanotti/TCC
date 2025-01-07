package com.example.liber.utils;

import android.os.AsyncTask;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

// Classe utilizada para algumas chamadas assíncronas
public class Task extends AsyncTask<Call, String, Response> {

    String ret;

    @Override
    protected void onPreExecute() {

        // atualizarView("Tarefa Iniciada");
    }

    @Override
    protected Response doInBackground(Call... calls){

        try {

            return calls[0].execute();

        }catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;

        //return conteudo;
    }

    /*@Override
    protected void onPostExecute(String s) {





            String[] stateList = s.split("@@");

            ArrayList list = new ArrayList();

            for (int i = 0; i < stateList.length; i++) {
                String[] aux = stateList[i].split(" ");
                String ra, nome, email = "";
                nome = aux[0];
                ra = aux[1];
                email = aux[2];

                Aluno aluno = new Aluno(ra, nome, email);
                list.add(aluno.getNome());

            }

    }*/

    @Override
    protected void onProgressUpdate(String... values) {
        // atualizarView(values[0]);
    }
}


