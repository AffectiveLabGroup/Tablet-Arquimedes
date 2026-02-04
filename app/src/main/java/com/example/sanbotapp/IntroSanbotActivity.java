package com.example.sanbotapp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
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
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class IntroSanbotActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {


    private GestionMediaPlayer gestionMediaPlayer;
    private Handler handlerSpeech = new Handler(Looper.getMainLooper());

    private ImageView imgFondo;
    private TextView dos;
    private TextView pista;
    private Button btnRepetirSuma;
    private Button btnRepetirPista;
    private Button btnHablar;
    private Button exit;

    private Intent intent = null;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private static final int PERMISSION_REQUEST_AUDIO = 1;

    @Override
    public void onInit(int status){
        if (status == TextToSpeech.SUCCESS) {
            tts.setPitch(0.8f);   // incluso 0.8f si sigue preguntando
            tts.setSpeechRate(1.0f); // evita exageraciones
            tts.setLanguage(new Locale("es", "ES"));
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.explicacion);
        tts = new TextToSpeech(this, this);


        // Coger variable respuesta que le ha llegado por parametro
        Intent intentej = getIntent();
        if (intentej != null ) {
            String respuesta = intentej.getStringExtra("intentgo");
                if (respuesta.equals("Enigma2Activity")) {
                    intent = new Intent(IntroSanbotActivity.this, Enigma2Activity.class);
                } else if(respuesta.equals("Enigma3Activity")){
                    intent = new Intent(IntroSanbotActivity.this, Enigma3Activity.class);
                } else if(respuesta.equals("EnigmaNombre")){
                    intent = new Intent(IntroSanbotActivity.this, EnigmaNombre.class);
                } else if(respuesta.equals("EnigmaSopa")){
                    intent = new Intent(IntroSanbotActivity.this, EnigmaSopa.class);
                } else {
                    intent = new Intent(IntroSanbotActivity.this, Enigma2Activity.class);
                }

        }

        imgFondo = findViewById(R.id.fondo);
        dos = findViewById(R.id.explicacion);
        pista = findViewById(R.id.pista);
        btnRepetirPista = findViewById(R.id.btnRepetirPista);
        btnRepetirSuma = findViewById(R.id.btnRepetirSuma);
        exit = findViewById(R.id.exit);
        btnHablar = findViewById(R.id.hablar);


        gestionMediaPlayer = new GestionMediaPlayer();



        setonClicks();

        // Pide permiso para usar el micrófono si hace falta
        checkAudioPermission();

        // Inicializa el reconocedor de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0).toLowerCase(Locale.getDefault());
                    System.out.println("Texto reconocido: " + text);

                    if (text.contains("hola")) {
                        hablar("¡Hola! Encantado de verte otra vez.");
                    } else if (text.contains("cuatro") || text.contains("4")) {
                        hablar("¡Genial! Veo que lo vais pillando.");

                        dos.setVisibility(View.GONE);
                        btnRepetirSuma.setVisibility(View.GONE);
                        btnRepetirPista.setVisibility(View.VISIBLE);
                        pista.setVisibility(View.VISIBLE);

                        try{
                            Thread.sleep(3000);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hablar("Una cosa más, mientras esteis resolviendo el ejercicio, podéis venir y pedirme una pista.");
                        hablar("Para ello, siguiendo los pasos de antes, tendréis que decirme la palabra PISTA.");

                        try{
                            Thread.sleep(13500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hablar("Ayudante, elige a alguien del grupo y probemos");
                    } else if (text.contains("pista")) {
                        hablar("¿Quieres una pista? Primero tendrás que intentar resolver el enigma.");
                        try{
                            Thread.sleep(5000);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hablar("¡Vamos a ello!");

                        try{
                            Thread.sleep(1000);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        startActivity(intent);
                    } else {
                        hablar("Uy, esa no es la respuesta que esperaba. Inténtalo de nuevo.");
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

    public void setonClicks() {

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(IntroSanbotActivity.this);
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


        btnRepetirSuma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hablar("Mi ayudante elegirá a un voluntario que me diga el resultado de la suma que se puede ver en mi pantalla, recordar" +
                        "que teneis que tocar mi cabeza y esperar a que mis orejas se pongan de color verde antes de decirme la respuesta");

            }
        });

        btnRepetirPista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hablar("Al igual que hemos hecho antes con la suma, mi ayudante va a elegir a un voluntario para que me diga la palabra PISTA");

            }
        });

        btnHablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });
    }


    // Una vez iniciada la aplicacion quiero que el robot me salude
    @Override
    public void onResume() {
        super.onResume();

        Global.ENIGMA1 = true;

        // Asegurarse de que la interfaz esté completamente cargada antes de comenzar a hablar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                // Luego de un breve retraso (500ms), iniciar el saludo del robot
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        hablar("Soy un poco tímida, por lo que agradecería que me hablarais de uno en uno");

                        hablar("Aún sigo un poco confusa.");

                        hablar("Mi ayudante se encargará de elegir a una persona para hablar conmigo.");

                        hablar("Cuando tengais la respuesta podéis acercaros y tocarme la cabeza");

                        hablar("Cuando mis orejas estén de color verde como ahora, podréis decirme la respuesta.");


                        imgFondo.setVisibility(View.VISIBLE);
                        dos.setVisibility(View.VISIBLE);
                        exit.setVisibility(View.VISIBLE);
                        btnRepetirSuma.setVisibility(View.VISIBLE);
                        btnHablar.setVisibility(View.VISIBLE);
                        hablar("¡Hagamos una prueba!, ayudante, elige a un voluntario que me diga el resultado de la suma que se puede ver en mi pantalla");
                        

                    }
                }, 500);  // Retraso de 500 ms para que la imagen se cargue primero
            }
        }, 100);  // Retraso mínimo para asegurarse de que la interfaz cargue completamente
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

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


}
