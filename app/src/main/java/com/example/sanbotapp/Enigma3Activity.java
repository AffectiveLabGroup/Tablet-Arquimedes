package com.example.sanbotapp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class Enigma3Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ImageView enigma3;
    private EditText respuesta;
    private Button comprobar;
    private Button pista;
    private Button fin;
    private Button atras;
    private Button saltar;

    private Integer pistas = 3;

    private Runnable runnable;
    private Button btnhablar;
    private Handler handler = new Handler();

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
        setContentView(R.layout.enigma3);


        tts = new TextToSpeech(this, this);
        enigma3 = findViewById(R.id.enigma3);
        respuesta = findViewById(R.id.respuesta);
        comprobar = findViewById(R.id.comprobar);
        pista = findViewById(R.id.pistas);
        fin = findViewById(R.id.fin);
        atras = findViewById(R.id.atras);
        exit = findViewById(R.id.exit);
        repetir = findViewById(R.id.repetir);
        saltar = findViewById(R.id.saltar);
        btnhablar = findViewById(R.id.btnhablar);

        enigma3.setVisibility(View.VISIBLE);


        intent = new Intent(Enigma3Activity.this, ArquimedesActivity.class);
        checkAudioPermission();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // El robot pregunta "¿quieres una pista?"
                //TODO: VARIAR, pregunar sobre si han entendido el enigma o sobre si necesitan ayuda o si necesitan una pista

                // Preguntar aleatoriamente si necesita una pista o si quiere que le repita el enunciado
                int random = (int) (Math.random() * 3);
                if(random == 0){
                    if(pistas > 0){
                        hablar("¿Necesitas una pista?");
                        startListening();
                    } else{
                        hablar("Ya no me quedan más pistas, ¿Quieres que te repita alguna?");
                        startListening();;
                    }
                } else if (random == 1) {
                    hablar("Si tienéis alguna duda, no dudéis en preguntarme");
                    startListening();
                } else if (random == 2) {
                    hablar("¿Has entendido el enigma? Recuerda que si necesitas que te repita el enunciado, solo tienes que pedírmelo");
                    startListening();

                } else {
                    hablar("¿Necesitas una pista?");
                    startListening();
                }
                // Configura el handler para que vuelva a ejecutar este bloque en 30 segundos
                handler.postDelayed(this, 120000); // 30,000 milisegundos = 30 segundos
            }
        };

        // Iniciar el ciclo de preguntas después de una pequeña demora inicial (opcional)
        handler.postDelayed(runnable, 120000);

        View myView = findViewById(R.id.enigma3); // Reemplaza 'mi_vista' con el ID de tu vista.

        myView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Cancelar el ciclo de preguntas
                handler.removeCallbacks(runnable);
                // Configura el handler para que vuelva a ejecutar este bloque en 30 segundos
                handler.postDelayed(runnable, 120000);
            }
            return true; // Devuelve true para consumir el evento.
        });


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {// Si sanbot detecta la palabra "hola" en el audio, entonces saluda
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0).toLowerCase(Locale.getDefault());

                System.out.println("Texto reconocido: " + text);

                if ( text.contains("Arquímedes") || text.contains("arquimedes") || text.contains("arquímedes") || text.contains("Arquimedes")) {
                    handler.removeCallbacks(runnable);

                    if (fin.getVisibility() == View.VISIBLE) {
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

                        hablar("Ahora que sabes quién es mi creador, me gustaría hablarte un poco sobre él. Clica en la flecha para continuar. ");
                        //TODO: MANDAR A LA SIGUIENTE ACTIVIDAD E INTRODUCIR QUE VA A HACER UNA PRESENTACIÓN SOBRE ARQUÍMEDES


                        //Intent intent = new Intent(Enigma3Activity.this, ArquimedesActivity.class);
                        //startActivity(intent);
                    } else{

                        // ofrecer más respuestas variadas
                        int random = (int) (Math.random() * 3);
                        if(random == 0){
                            hablar("¡Eureka! Sí, el es mi creador. Uno de los hombres más inteligentes de la historia.Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        } else if(random == 1){
                            hablar("Exacto, no sé cómo se me pudo olvidar el nombre de uno de los hombres más inteligentes de la historia.Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        } else if(random == 2){
                            hablar("Claro, es cierto, el es mi creador, no sé cómo se me pudo olvidar. Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        } else {
                            hablar("¡Eureka! Esa era la respuesta correcta, gracias por descubrir el nombre de mi creador. Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        }

                        try{
                            Thread.sleep(7000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                } else if (text.contains("repetir") || text.contains("repite")){
                    handler.removeCallbacks(runnable);
                    hablar("Claro, el enigma consiste en descrifrar la frase que se muestra en la pantalla. Seguro que con eso conseguimos descubrir el nombre de mi creador");
                    handler.postDelayed(runnable, 120000);

                } else if (text.contains("pista") || text.equals("sí")){
                    handler.removeCallbacks(runnable);
                    if(pistas == 0){

                        // Ya no tengo más pistas, pero voy a repetirta alguna. Haz que saque la primera la segunda o la tercera pista de forma aleatoria
                        int pistaAleatoria = (int) (Math.random() * 3) + 1;
                        if (pistaAleatoria == 1){
                            hablar("Ya no tengo más pistas, pero voy a repetirte alguna. Quizás la letra obtenida en el primer enigma tenga algo que ver con la letra d");
                        } else if (pistaAleatoria == 2){
                            hablar("Vale, aquí tienes una pista. ¿Recuerdas la solución del enigma anterior? Si restas las cifras y le quitas 2, aparece un número importante");
                        } else if (pistaAleatoria == 3){
                            hablar("Te voy a repetir una de las pistas, ¿Por qué no comienzas por la palabra más corta?");
                        }
                        handler.postDelayed(runnable, 120000);
                    } else if (pistas == 3){

                        hablar("¿Por qué no comienzas por la palabra más corta?");
                        pistas--;
                        handler.postDelayed(runnable, 120000);
                    } else if (pistas == 2){

                        hablar("¿Recuerdas la solución del enigma anterior? Si restas las cifras y le quitas 2, aparece un número importante");
                        pistas--;
                        handler.postDelayed(runnable, 120000);
                    } else if (pistas == 1){
                        hablar("Esta es ya tu última pista, quizás la letra obtenida en el primer enigma tenga algo que ver con la letra d");
                        pistas--;
                        handler.postDelayed(runnable, 120000);
                    }
                } else if(text.contains("no") || text.contains("no quiero") || text.contains("no gracias")){
                    handler.removeCallbacks(runnable);
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("Vale, si necesitas que te repita el problema, solo tienes que pedírmelo.");
                    } else if (respuestaAleatoria == 2){
                        hablar("De acuerdo, si necesitas una pista, solo tienes que pedirla.");
                    } else if (respuestaAleatoria == 3){
                        hablar("Está bien, si necesitas ayuda, no dudes en pedírmela.");
                    }
                    handler.postDelayed(runnable, 120000);
                } else if(text.contains("ayuda") || text.contains("ayúdame") || text.contains("necesito ayuda")){
                    handler.removeCallbacks(runnable);
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("Claro, recuerda que puedes tocar mi cabeza y decirme la palabra PISTA, o puedes consultar las pistas clicando en el botón Consultar pista, que se puede ver en mi pantalla.");
                    } else if (respuestaAleatoria == 2){
                        hablar("Por supuesto, recuerda que si necesitas una pista, solo tienes que pedirla diciéndome la palabra PISTA.");
                    } else if (respuestaAleatoria == 3){
                        hablar("Está bien, si necesitas que te repita el problema, toca mi cabeza y pídemelo.");
                    }
                    handler.postDelayed(runnable, 120000);
                } else if(text.contains("gracias") || text.contains("gracias sol") || text.contains("gracias robot")){
                    handler.removeCallbacks(runnable);
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("¡De nada! Recuerda que puedes preguntarme lo que sea.");
                    } else if (respuestaAleatoria == 2){
                        hablar("¡No hay de qué! Estoy aquí para ayudarte.");
                    } else if (respuestaAleatoria == 3){
                        hablar("¡No tienes por qué darme las gracias! Estoy aquí para ayudarte.");
                    }
                    handler.postDelayed(runnable, 120000);
                } else{
                    handler.removeCallbacks(runnable);

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


                    handler.postDelayed(runnable, 120000);
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

                hablar("Recordar venir a mi para decirme la respuesta o pedirme pistas.");

                try {
                    Thread.sleep(5000);
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

                    .setItems(new String[]{"Enigma descrifrar nombre", "Enigma sopa de letras", "Arquimedes", "Enigma2" }, (dialog, which) -> {
                        if(which == 0){
                            intent = new Intent(Enigma3Activity.this, EnigmaNombre.class);
                        } else if(which == 1){
                            intent = new Intent(Enigma3Activity.this, EnigmaSopa.class);
                        }  else if(which == 2){
                            intent = new Intent(Enigma3Activity.this, ArquimedesActivity.class);
                        } else if(which == 3){
                            intent = new Intent(Enigma3Activity.this, Enigma2Activity.class);
                        } else{
                            intent = new Intent(Enigma3Activity.this, ArquimedesActivity.class);
                        }
                    })
                    .setNegativeButton("SALTAR", (dialog, which) -> {
                        startActivity(intent);
                    })
                    .show();
        });

        btnhablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Enigma3Activity.this);
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
                handler.removeCallbacks(runnable);
                hablar("Recuerda que tienes que descifrar la frase para resolver el enigma. ¡Buena suerte!");
                handler.postDelayed(runnable, 120000);
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Enigma3Activity.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Estás seguro de que quieres ir al segundo enigma?");

                // Botón "Sí"
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Enigma3Activity.this, Enigma2Activity.class);
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
                if (respuesta.getText().toString().equals("mi nombre es arquímedes") || respuesta.getText().toString().equals("Mi nombre es arquímedes") || respuesta.getText().toString().equals("Mi nombre es Arquímedes") || respuesta.getText().toString().equals("Mi nombre es Arquimedes")|| respuesta.getText().toString().equals("mi nombre es arquimedes")|| respuesta.getText().toString().equals("mi nombre es Arquímedes")) {


                    handler.removeCallbacks(runnable);
                    // Borrar el texto de la respuesta
                    respuesta.setText("");

                    int random = (int) (Math.random() * 3);
                    if(random == 0){
                        hablar("¡Eureka! Has conseguido descifrar la frase, vamos a esperar al resto de familias para comprobar si han conseguido descifrarla también. ¡Enhorabuena!");
                    } else if(random == 1){
                        hablar("Exacto, sois geniales, descifrar esa frase no era tarea fácil. Esperemos al resto de familias seguro que también lo consiguen. ¡Enhorabuena!");
                    } else if(random == 2){
                        hablar("Esa es la respuesta, gracias por descifrar la frase. Ahora esperemos a que el resto de familias también lo consigan. ¡Enhorabuena!");
                    } else {
                        hablar("¡Fantástico! Habéis conseguido descifrar la frase. Vamos a esperar a que el resto de familias también lo consigan. ¡Enhorabuena!");
                    }

                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    fin.setVisibility(View.VISIBLE);

                } else {

                    // Si los números no están en los círculos, mostrar mensaje de error

                    // Ofrecer respuestas personalizadas, si la frase contiene las parabras mi, nombre, es, arquímedes ofrecer una respuesta personalizada
                    if(respuesta.getText().toString().contains("mi") && !respuesta.getText().toString().contains("nombre") && !respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        hablar("¡Ya te falta menos! Parece que la primera palabra es correcta ¡mucho ánimo!");

                    } else if ( respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && !respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        hablar("¡Casi lo tienes! Parece que las dos primeras palabras son correctas, sigue intentándolo.");

                    } else if (respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        hablar("¡Estás muy cerca! Parece que las tres primeras palabras son correctas, ¡ya casi lo tienes!");

                    } else if (respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && respuesta.getText().toString().contains("es") && (respuesta.getText().toString().contains("arquimedes")
                            || respuesta.getText().toString().contains("Arquimedes")
                            || respuesta.getText().toString().contains("arquímedes")
                            || respuesta.getText().toString().contains("Arquímedes"))){

                        hablar("¡Exacto! Has conseguido descifrar la frase, ahora solo tienes que decirme el nombre de mi creador.");

                    } else if ( !respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && !respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        hablar("¡Casi lo tienes! Parece que la segunda palabra es correcta, sigue intentándolo.");

                    } else if ( !respuesta.getText().toString().contains("mi") && !respuesta.getText().toString().contains("nombre") && respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        hablar("¡Ya falta menos! Parece que la tercera palabra es correcta.");

                    } else if ( respuesta.getText().toString().contains("arquimedes")
                            || respuesta.getText().toString().contains("Arquimedes")
                            || respuesta.getText().toString().contains("arquímedes")
                            || respuesta.getText().toString().contains("Arquímedes")) {

                        hablar("¡Casi lo tienes! Parece que has descifrado una palabra importante, sigue intentándolo.");
                    } else {
                        hablar("¡Vaya! Parece que la frase no es correcta, sigue intentándolo.");
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

        pista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Enigma3Activity.this);

                // Inflar el diseño personalizado del diálogo
                View dialogView = getLayoutInflater().inflate(R.layout.pistas3, null);
                builder.setView(dialogView);

                // Configurar el TextView del diseño inflado
                TextView messageView = dialogView.findViewById(R.id.tvPistas);
                Button cerrar = dialogView.findViewById(R.id.btnEntendido);
                AlertDialog dialog = builder.create();


                // Cargar la tipografía personalizada
                Typeface customFont = ResourcesCompat.getFont(Enigma3Activity.this, R.font.julee_regular);
                messageView.setTypeface(customFont);
                messageView.setText("Para ver las pistas solo tienes que pedirlas"); // Texto del mensaje


                if (pistas == 0) {
                    messageView.setText("Pista 1: ¿Por qué no comienzas por la palabra más corta?\n\n" +
                            "Pista 2: ¿Recuerdas la solución del enigma anterior? Si restas las cifras y le quitas 2, aparece un número importante\n\n" +
                            "Pista 3: Quizás la letra obtenida en el primer enigma, tenga algo que ver con la letra d\n");
                } else if (pistas == 1) {
                    messageView.setText("Pista 1: ¿Por qué no comienzas por la palabra más corta?\n\n" +
                            "Pista 2: ¿Recuerdas la solución del enigma anterior? Si restas las cifras y le quitas 2, aparece un número importante\n");
                } else if (pistas == 2) {
                    messageView.setText("Pista 1 : ¿Por qué no comienzas por la palabra más corta?\n");
                }

                cerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cerrar el diálogo
                        dialog.dismiss();
                    }
                });

                // Configurar tamaño del diálogo según el diseño inflado
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getWindow().setLayout(1400, 800); // Ajustar a las dimensiones exactas de la imagen de fondo
                    }
                });

                dialog.show();

            }
        });


        fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finenigma){
                    startActivity(intent);
                } else {

                    // Leer el texto
                    hablar("Para continuar con la exposición tenéis que decirme el nombre de mi creador.");

                    // Crear el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(Enigma3Activity.this);

                    // Inflar el diseño personalizado del diálogo
                    View dialogView = getLayoutInflater().inflate(R.layout.decir72, null);
                    builder.setView(dialogView);

                    // Configurar el TextView del diseño inflado
                    TextView messageView = dialogView.findViewById(R.id.tvPistas);
                    Button btnAceptar = dialogView.findViewById(R.id.btnhablar);

                    AlertDialog dialog = builder.create();


                    // Cargar la tipografía personalizada
                    Typeface customFont = ResourcesCompat.getFont(Enigma3Activity.this, R.font.julee_regular);
                    messageView.setTypeface(customFont);
                    messageView.setText("Para continuar con la exposición teneis que decirme el nombre de mi creador"); // Texto del mensaje

                    btnAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Acción del botón
                            //TODO: ESCUCHA
                            startListening();
                        }
                    });

                    // Configurar tamaño del diálogo según el diseño inflado
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            dialog.getWindow().setLayout(1400, 800); // Ajustar a las dimensiones exactas de la imagen de fondo
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
