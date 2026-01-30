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

import com.example.sanbotapp.robotControl.FaceRecognitionControl;
import com.example.sanbotapp.robotControl.HardwareControl;
import com.example.sanbotapp.robotControl.HeadControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.example.sanbotapp.robotControl.SystemControl;
import com.example.sanbotapp.robotControl.WheelControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
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

public class EnigmaNombre extends AppCompatActivity implements TextToSpeech.OnInitListener{


    private ImageView enigma3;
    private EditText respuesta;
    private Button comprobar;
    private Button fin;
    private Button atras;
    private Button saltar;

    private Button exit;
    private Button repetir;
    private Intent intent = null;

    private Boolean finenigma = false;


    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private static final int PERMISSION_REQUEST_AUDIO = 1;

    @Override
    public void onInit(int status){
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("es", "ES"));
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enigmanombre);

        tts = new TextToSpeech(this, this);

        enigma3 = findViewById(R.id.enigma3);
        respuesta = findViewById(R.id.respuesta);
        comprobar = findViewById(R.id.comprobar);
        fin = findViewById(R.id.fin);
        atras = findViewById(R.id.atras);
        exit = findViewById(R.id.exit);
        repetir = findViewById(R.id.repetir);
        saltar = findViewById(R.id.saltar);

        enigma3.setVisibility(View.VISIBLE);


        intent = new Intent(EnigmaNombre.this, ArquimedesActivity.class);


        // Si sanbot detecta la palabra "hola" en el audio, entonces saluda
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {// Si sanbot detecta la palabra "hola" en el audio, entonces saluda
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0).toLowerCase(Locale.getDefault());

                    System.out.println("Texto reconocido: " + text);

                if ( text.contains("Arquímedes") || text.contains("arquimedes") || text.contains("arquímedes") || text.contains("Arquimedes")) {


                    if (fin.getVisibility() == View.VISIBLE) {
                        // Expresion feliz
                        finenigma = true;

                        // ofrecer más respuestas variadas
                        int random = (int) (Math.random() * 3);
                        if(random == 0){
                            hablar("¡Eureka! Sí, el es mi creador. Uno de los hombres más inteligentes de la historia.");
                        } else if(random == 1){
                            hablar("Exacto, no sé cómo se me pudo olvidar el nombre de uno de los hombres más inteligentes de la historia.");
                        } else if(random == 2){
                            hablar("Claro, es cierto, el es mi creador, no sé cómo se me pudo olvidar.");
                        } else {
                            hablar("¡Eureka! Esa era la respuesta correcta, gracias por descubrir el nombre de mi creador.");
                        }

                        // Girar

                        try{
                            Thread.sleep(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hablar("Ahora que sabes quién es mi creador, me gustaría hablarte un poco sobre él. ");
                        //TODO: MANDAR A LA SIGUIENTE ACTIVIDAD E INTRODUCIR QUE VA A HACER UNA PRESENTACIÓN SOBRE ARQUÍMEDES

                        Intent intent = new Intent(EnigmaNombre.this, ArquimedesActivity.class);
                        startActivity(intent);
                    } else{

                        // ofrecer más respuestas variadas
                        int random = (int) (Math.random() * 3);
                        if(random == 0){
                            hablar("¡Eureka! Sí, el es mi creador. Uno de los hombres más inteligentes de la historia.Prueba a introducir la respuesta en la pantalla, y esperemos al resto de participantes");
                        } else if(random == 1){
                            hablar("Exacto, no sé cómo se me pudo olvidar el nombre de uno de los hombres más inteligentes de la historia.Prueba a introducir la respuesta en la pantalla, y esperemos al resto de participantes");
                        } else if(random == 2){
                            hablar("Claro, es cierto, el es mi creador, no sé cómo se me pudo olvidar. Prueba a introducir la respuesta en la pantalla, y esperemos al resto de participantes");
                        } else {
                            hablar("¡Eureka! Esa era la respuesta correcta, gracias por descubrir el nombre de mi creador. Prueba a introducir la respuesta en la pantalla, y esperemos al resto de participantes");
                        }

                        try{
                            Thread.sleep(7000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                } else if (text.contains("repetir") || text.contains("repite")){
                    hablar("Claro, el enigma consiste en descrifrar la palabra que se muestra en la pantalla. Seguro que con eso conseguimos descubrir el nombre de mi creador");

                } else if (text.contains("pista") || text.equals("sí")){

                    // Respuestas aleatorias y variadas del estilo, vaya, parece que no tengo ninguna pista
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("Vaya, parece que no tengo ninguna pista que darte.");
                    } else if (respuestaAleatoria == 2){
                        hablar("Lo siento, no tengo ninguna pista, tendrás que descibrirlo sin mi ayuda.");
                    } else if (respuestaAleatoria == 3){
                        hablar("Lo siento, no tengo ninguna pista que pueda darte.");
                    }


                } else if(text.contains("no") || text.contains("no quiero") || text.contains("no gracias")){

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("Vale, si necesitas que te repita el problema, solo tienes que pedírmelo.");
                    } else if (respuestaAleatoria == 2){
                        hablar("De acuerdo, si necesitas que te repita el enunciado solo tienes que pedirlo.");
                    } else if (respuestaAleatoria == 3){
                        hablar("Está bien, si necesitas ayuda, no dudes en pedírmela.");
                    }

                } else if(text.contains("ayuda") || text.contains("ayúdame") || text.contains("necesito ayuda")){

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("Claro, recuerda que puedes pedirme que te repita el enunciado si lo necesitas.");
                    } else if (respuestaAleatoria == 2){
                        hablar("Por supuesto, recuerda que puedes pedirme que te repita el enunciado si lo necesitas.");
                    } else if (respuestaAleatoria == 3){
                        hablar("Está bien, si necesitas que te repita el problema, toca mi cabeza y pídemelo.");
                    }

                } else if(text.contains("gracias") || text.contains("gracias sol") || text.contains("gracias robot")){

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("¡De nada! Recuerda que puedes preguntarme lo que sea.");
                    } else if (respuestaAleatoria == 2){
                        hablar("¡No hay de qué! Estoy aquí para ayudarte.");
                    } else if (respuestaAleatoria == 3){
                        hablar("¡No tienes por qué darme las gracias! Estoy aquí para ayudarte.");
                    }

                } else{

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("¡Vaya! Esa no es la respuesta correcta. ¡No te preocupes, sigue intentándolo!");
                    } else if (respuestaAleatoria == 2){
                        hablar("¡Incorrecto! Vuelve a intentarlo, estoy segura de que puedes resolverlo. ¡Buena suerte!");
                    } else if (respuestaAleatoria == 3){
                        hablar("¡Incorrecto! ¿Quieres que te repita el enunciado?");
                    } else {
                        hablar("¡Vaya! Esa no es la respuesta que esperaba");
                    }


                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException d) {
                        d.printStackTrace();
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

        setonClicks();

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

    // Una vez iniciada la aplicacion quiero que el robot me salude
    @Override
    public void onResume() {
        super.onResume();

        if(Global.ENIGMA3 == false){
            Global.ENIGMA3 = true;

        // Asegurarse de que la interfaz esté completamente cargada antes de comenzar a hablar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                hablar("Ahora que lo pienso, creo que mi creador me dejó una nota para ayudarme a recordar su nombre");


                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                hablar("¡Vaya! Parece que la nota está cifrada. ¿Pueden ayudarme a descifrarla? ¡Por favor! ¡Díganme el nombre de mi creador!");
                // Primero cargar la imagen


                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hablar("Al igual que antes, mi ayudante os repartirá una hoja con el enigma");

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                hablar("Recordar venir a mi para decirme la respuesta.");

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                hablar("Ya podéis ir a vuestras mesas ¡Buena suerte!");

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 500);  // Retraso mínimo para asegurarse de que la interfaz cargue completamente
        }
    }

    public void setonClicks(){

        saltar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecciona una actividad")

                    .setItems(new String[]{"Enigma descrifrar frase", "Enigma sopa de letras", "Arquimedes", "Enigma2" }, (dialog, which) -> {
                        if(which == 0){
                            intent = new Intent(EnigmaNombre.this, Enigma3Activity.class);
                        } else if(which == 1){
                            intent = new Intent(EnigmaNombre.this, EnigmaSopa.class);
                        }  else if(which == 2){
                            intent = new Intent(EnigmaNombre.this, ArquimedesActivity.class);
                        } else if(which == 3){
                            intent = new Intent(EnigmaNombre.this, Enigma2Activity.class);
                        } else{
                            intent = new Intent(EnigmaNombre.this, ArquimedesActivity.class);
                        }
                    })
                    .setNegativeButton("SALTAR", (dialog, which) -> {
                        startActivity(intent);
                    })
                    .show();
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(EnigmaNombre.this);
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

        repetir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hablar("Recuerda que tienes que descifrar la palabra para resolver el enigma. ¡Buena suerte!");

            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(EnigmaNombre.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Estás seguro de que quieres ir al segundo enigma?");

                // Botón "Sí"
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EnigmaNombre.this, Enigma2Activity.class);
                        startActivity(intent);
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

        comprobar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Comprobar si los números están en los círculos
                if (respuesta.getText().toString().equals("arquímedes")  || respuesta.getText().toString().equals("Arquímedes") || respuesta.getText().toString().equals("Arquimedes")|| respuesta.getText().toString().equals("arquimedes")) {


                    // Borrar el texto de la respuesta
                    respuesta.setText("");

                    int random = (int) (Math.random() * 3);
                    if(random == 0){
                        hablar("¡Eureka! Has conseguido descifrar la palabra, vamos a esperar al resto de participantes para comprobar si han conseguido descifrarla también. ¡Enhorabuena!");
                    } else if(random == 1){
                        hablar("Exacto, sois geniales, descifrar esa palabra no era tarea fácil. Esperemos al resto de participantes seguro que también lo consiguen. ¡Enhorabuena!");
                    } else if(random == 2){
                        hablar("Esa es la respuesta, gracias por descifrar la palabra. Ahora esperemos a que el resto de participantes también lo consigan. ¡Enhorabuena!");
                    } else {
                        hablar("¡Fantástico! Habéis conseguido descifrar la palabra. Vamos a esperar a que el resto de participantes también lo consigan. ¡Enhorabuena!");
                    }

                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    fin.setVisibility(View.VISIBLE);

                } else {

                    // ofrecer distintas frases de error
                    int random = (int) (Math.random() * 3);
                    if(random == 0){
                        hablar("¡Vaya! Parece que esa no es la palabra que buscamos, sigue intentándolo.");
                    } else if(random == 1){
                        hablar("¡Oh no! Esa no es la respuesta correcta, sigue intentándolo.");
                    } else if(random == 2){
                        hablar("¡Vaya! Parece que esa no es la palabra que buscamos, sigue intentándolo.");
                    } else {
                        hablar("¡Incorrecto! Sigue intentándolo, estoy segura de que lo encontrarás.");
                    }


                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Limpiar el EditText
                    respuesta.setText("");


                }
            }
        });


        fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finenigma){
                    startActivity(intent);
                } else {

                    // Leer el texto
                    hablar("Para continuar con la exposición teneis que decirme el nombre de mi creador.");


                    // Crear el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(EnigmaNombre.this);

                    // Inflar el diseño personalizado del diálogo
                    View dialogView = getLayoutInflater().inflate(R.layout.decir72, null);
                    builder.setView(dialogView);

                    // Configurar el TextView del diseño inflado
                    TextView messageView = dialogView.findViewById(R.id.tvPistas);
                    Button btnAceptar = dialogView.findViewById(R.id.btnhablar);
                    AlertDialog dialog = builder.create();


                    // Cargar la tipografía personalizada
                    Typeface customFont = ResourcesCompat.getFont(EnigmaNombre.this, R.font.julee_regular);
                    messageView.setTypeface(customFont);
                    messageView.setText("Para continuar con la exposición teneis que decirme el nombre de mi creador"); // Texto del mensaje

                    // Configurar tamaño del diálogo según el diseño inflado
                    btnAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Acción del botón
                            //TODO: ESCUCHA
                            startListening();
                        }
                    });


                    dialog.show();

                }


            }
        });

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
