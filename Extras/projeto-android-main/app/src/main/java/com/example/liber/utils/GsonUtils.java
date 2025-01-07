package com.example.liber.utils;

import com.example.liber.model.ErrorMessage;
import com.google.gson.Gson;

import okhttp3.ResponseBody;

public class GsonUtils {

    // Recebe uma mensagem de erro da API em JSON e converte para um objeto
    public static String getErrorMessageFromJson(ResponseBody response) {
        Gson gson = new Gson();
        ErrorMessage message = gson.fromJson(response.charStream(), ErrorMessage.class);
        return message.getMessage();
    }
}
