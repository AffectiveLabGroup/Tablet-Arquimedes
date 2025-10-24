package com.example.sanbotapp.gestion;

import android.media.MediaDataSource;
import android.media.MediaPlayer;

import com.example.sanbotapp.utils.ByteArrayMediaDataSource;

public class GestionMediaPlayer {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean finReproduccion = false;
    private OnReproduccionFinalizadaListener onReproduccionFinalizadaListener;

    // Constructor
    public GestionMediaPlayer(){
    }

    // Función que indica si el media player se está reproduciendo o no
    public boolean isMediaPlayerReproduciendose(){
        return mediaPlayer.isPlaying();
    }

    // Función que reproduce el media player con la ristra de bytes que se pase como parámetro
    public void reproducirMediaPlayer(byte[] data){
        mediaPlayer.reset();

        // Llama a la clase ByteArrayMediaDataSource que permite reproducir
        // una ristra de bytes, en vez de archivos de audio
        MediaDataSource mediaDataSource = new ByteArrayMediaDataSource(data);
        mediaPlayer.setDataSource(mediaDataSource);

        // Listener que se llama cuando el MediaPlayer está listo para reproducir
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start(); // Comienza la reproducción
            }
        });

        // Listener para detectar cuando la reproducción ha finalizado
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finReproduccion = true; // Marca que la reproducción ha terminado
                if (onReproduccionFinalizadaListener != null) {
                    onReproduccionFinalizadaListener.onReproduccionFinalizada(); // Llama al listener si está definido
                }
            }
        });

        mediaPlayer.prepareAsync(); // Prepara el MediaPlayer de forma asíncrona
    }

    // Función que detiene la reproducción del media player
    public void pararMediaPlayer(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    // Función para definir el listener que será llamado cuando la reproducción finalice
    public void setOnReproduccionFinalizadaListener(OnReproduccionFinalizadaListener listener) {
        this.onReproduccionFinalizadaListener = listener;
    }

    // Interfaz para el listener de finalización de la reproducción
    public interface OnReproduccionFinalizadaListener {
        void onReproduccionFinalizada();
    }
}
