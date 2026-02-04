package com.example.sanbotapp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sanbotapp.gestion.GestionMediaPlayer;
import com.example.sanbotapp.moduloOpenAI.ModuloOpenAIAudioSpeech;
import com.example.sanbotapp.robotControl.FaceRecognitionControl;
import com.example.sanbotapp.robotControl.HandsControl;
import com.example.sanbotapp.robotControl.HardwareControl;
import com.example.sanbotapp.robotControl.HeadControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.example.sanbotapp.robotControl.SystemControl;
import com.example.sanbotapp.robotControl.WheelControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.ProjectorManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ArquimedesActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {



    private Handler handlerSpeech = new Handler(Looper.getMainLooper());

    private ImageView arq1;
    private ImageView arq2;
    private ImageView arq3, arq4, arq5, arq6;

    private Button exit;
    private Button hablar;

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private static final int PERMISSION_REQUEST_AUDIO = 1;


    @Override
    public void onInit(int status){
        if (status == TextToSpeech.SUCCESS) {
            tts.setPitch(0.8f);   // incluso 0.8f si sigue preguntando
            tts.setSpeechRate(1.0f); // evita exage
            tts.setLanguage(new Locale("es", "ES"));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arquimedes);

        tts = new TextToSpeech(this, this);

        exit = findViewById(R.id.exit);
        hablar = findViewById(R.id.hablar);

        arq1 = findViewById(R.id.arq1);
        arq2 = findViewById(R.id.arq2);
        arq3 = findViewById(R.id.arq3);
        arq4 = findViewById(R.id.arq4);
        arq5 = findViewById(R.id.arq5);
        arq6 = findViewById(R.id.arq6);





        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(ArquimedesActivity.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Estás seguro de que quieres salir?");

                // Botón "Sí"
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);
                    }
                });

                // Botón "No"
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cierra el diálogo, no hace nada
                    }
                });

                // Mostrar el diálogo
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        hablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {// Si sanbot detecta la palabra "hola" en el audio, entonces saluda
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0).toLowerCase(Locale.getDefault());


                    System.out.println("Texto reconocido: " + text);

                if (text.contains("eureka") || text.contains("eureca") || text.contains("Eureka")) {

                    hablar("Exacto Eureka, ¡Hasta la próxima! ¡Eureka! ¡Eureka! Gracias por despertarme y ayudarme a recordar el nombre de mi creador");

                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if( text.contains("oro") ) {
                    hablar("Sí, debía ser un objeto de oro, pero ¿qué más debía tener para que Arquímedes pudiera demostrar que no era de oro puro?");

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if( text.contains("mismo peso") ) {

                    hablar("Casi, casi, ¿Mismo peso el qué?");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

                else {
                    //Respuestas aleatorias
                    int random = (int) (Math.random() * 3) + 1;
                    switch (random) {
                        case 1:
                            hablar("¡Vaya! Esa no es la respuesta correcta, inténtalo de nuevo.");
                            break;
                        case 2:
                            hablar("No, esa no es la respuesta que esperaba, vuelve a intentarlo.");
                            break;
                        case 3:
                            hablar("Piensa un poquito más, seguro que lo consigues.");
                            break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                }
            }


        @Override public void onReadyForSpeech(Bundle params) {}
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onError(int error) { /*startListening(); */}
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}

    });


    }



    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        speechRecognizer.startListening(intent);
    }
    private void hablar(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Para API 21 y superiores
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
        } else {
            // Para API 19–20
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_AUDIO);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Asegurarse de que la interfaz esté completamente cargada antes de mostrar el diálogo
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciarSaludo2(); // Llamada al método para mostrar el diálogo
            }
        }, 500); // Retraso de 500ms para asegurarse de que la interfaz esté lista
    }



        private void iniciarSaludo2() {
            new Thread(() -> {
                try {
                    // Primer diálogo
                    hablar("Estoy muy contenta de que hayáis conseguido llegar hasta aquí.");
                    Thread.sleep(7000);

                    hablar("Arquímedes fue mi creador, un gran matemático, físico, ingeniero, inventor y astrónomo griego.");
                    Thread.sleep(9000);

                    hablar("No sé cómo se me pudo olvidar su nombre, pero gracias a vosotros he podido recordarlo.");
                    Thread.sleep(6000);


                    // Tercera parte del diálogo
                    hablar("Una de las historias más famosas sobre Arquímedes es la del baño.");
                    Thread.sleep(5500);

                    // Cambio de imágenes (debe ejecutarse en el hilo de la UI)
                    runOnUiThread(() -> {
                        arq1.setVisibility(View.GONE);
                        arq2.setVisibility(View.VISIBLE);
                    });
                    Thread.sleep(1000);

                    hablar("Cuenta la historia que Arquímedes recibió un encargo del rey Herión, que quería saber si la corona que había adquirido era realmente de oro.");
                    Thread.sleep(10000);

                    runOnUiThread(() -> {
                        arq2.setVisibility(View.GONE);
                        arq3.setVisibility(View.VISIBLE);
                    });
                    Thread.sleep(1000);

                    hablar("Un día, mientras se bañaba, dio con la solución. Descubrió el principio de la flotación al ver cómo el agua se desbordaba al entrar en la bañera.");
                    Thread.sleep(11000);

                    runOnUiThread(() -> {
                        arq3.setVisibility(View.GONE);
                        arq4.setVisibility(View.VISIBLE);
                    });
                    Thread.sleep(1000);

                    hablar("Entusiasmado por su descubrimiento, salió corriendo gritando '¡Eureka! ¡Eureka!'. Que en griego significa ¡Lo he encontrado! ");
                    Thread.sleep(10000);

                    runOnUiThread(() -> {
                        arq4.setVisibility(View.GONE);
                        arq5.setVisibility(View.VISIBLE);
                    });
                    Thread.sleep(1000);

                    // Última parte del diálogo
                    hablar("Arquímedes pensó que si introducía la corona en la bañera, podía descubrir de qué estaba hecha midiendo el volumen de agua que desbordaba, que sería diferente según el material del que estuviera hecha.\n");
                    Thread.sleep(15000);

                    hablar("Espero que os haya gustado la historia de Arquímedes y que hayáis aprendido algo nuevo sobre él.");
                    Thread.sleep(8000);


                    runOnUiThread(() -> {
                        arq5.setVisibility(View.GONE);
                        arq6.setVisibility(View.VISIBLE);
                    });
                    Thread.sleep(1000);

                    hablar("¿Recordáis que palabra gritó Arquímedes al dar con este descubrimiento?");
                    Thread.sleep(6000);

                    runOnUiThread(() -> hablar.setVisibility(View.VISIBLE));



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
