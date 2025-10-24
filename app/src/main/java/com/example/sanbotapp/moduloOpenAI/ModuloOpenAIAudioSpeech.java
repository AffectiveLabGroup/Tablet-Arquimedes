package com.example.sanbotapp.moduloOpenAI;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModuloOpenAIAudioSpeech {
    private byte[] respuestaGPTVoz;
    private static final String API_URL = "https://api.openai.com/v1/audio/speech";
    private static final String API_KEY = "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";

    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Constructor
    public ModuloOpenAIAudioSpeech() {
    }

    // Función para realizar la consulta al endpoint Audio Speech de OpenAI
    public void peticionVozOpenAI(final String respuesta, final String voz, final OpenAICallback callback) {
        executorService.execute(() -> {
            try {
                // Crear el cuerpo de la petición JSON
                JSONObject request = new JSONObject();
                request.put("model", "tts-1");
                request.put("input", respuesta);
                request.put("voice", voz);

                // Crear la petición
                RequestBody peticion = RequestBody.create(
                        MediaType.parse("application/json"), request.toString());

                Request requestOpenAI = new Request.Builder()
                        .url(API_URL)
                        .post(peticion)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + API_KEY)
                        .build();

                // Ejecutar la petición y procesar la respuesta
                try (Response response = client.newCall(requestOpenAI).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Código de error inesperado " + response);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        respuestaGPTVoz = response.body().bytes();
                    }

                    // Llamar al callback de éxito
                    callback.onSuccess(respuestaGPTVoz);

                } catch (IOException e) {
                    Log.e("OpenAI Request Error", e.getMessage());
                    callback.onError(e);
                }

            } catch (JSONException e) {
                Log.e("OpenAI JSON Error", e.getMessage());
                callback.onError(e);
            }
        });
    }

    // Función que obtiene la respuesta en formato ristra de bytes de la consulta al Audio Speech de la API de OpenAI
    public byte[] getGPTVoz() {
        return respuestaGPTVoz;
    }

    // Interfaz para manejar el éxito o fallo de la petición
    public interface OpenAICallback {
        void onSuccess(byte[] result);
        void onError(Exception e);
    }

    // Función que pasado un texto, lo reproduce con la voz seleccionada
    public void reproducirVoz(String respuesta, String voz, OpenAICallback callback) {
        peticionVozOpenAI(respuesta, voz, callback);
    }
}
