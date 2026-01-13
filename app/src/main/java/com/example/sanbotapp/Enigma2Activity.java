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
import android.util.Log;
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


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Enigma2Activity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private Handler handlerSpeech = new Handler(Looper.getMainLooper());

    private ImageView imgFondo;

    private Integer pistas = 3;

    private AlertDialog dialog;
    String cadena = "";

    private boolean respuesta = false;

    private Runnable runnable;
    private Handler handler = new Handler();

    private Button exit;
    private Button repetir;

    private Boolean finenigma = false;
    private Button btnhablar;


    // Por defecto es esta
    private Intent intent = null;

    Button comprobar;
    Button pistasButton;
    Button volverEnigma1;
    Button flechaSiguiente;
        EditText vertical11;
    EditText vertical12;
    EditText vertical21;
    EditText vertical22;
    EditText centro;
    Button saltar;

    ImageView enigma2;
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
        setContentView(R.layout.enigma2);

        tts = new TextToSpeech(this, this);

        comprobar = findViewById(R.id.comprobar);
        vertical11 = findViewById(R.id.vertical11);
        vertical12 = findViewById(R.id.vertical12);
        vertical21 = findViewById(R.id.vertical21);
        vertical22 = findViewById(R.id.vertical22);
        centro = findViewById(R.id.centro);
        pistasButton = findViewById(R.id.pistas);
        volverEnigma1 = findViewById(R.id.volverEnigma1);
        flechaSiguiente = findViewById(R.id.flechaSiguiente);
        exit = findViewById(R.id.exit);
        repetir = findViewById(R.id.repetir);
        saltar = findViewById(R.id.saltar);
        btnhablar = findViewById(R.id.btnhablar);


        intent = new Intent(Enigma2Activity.this, Enigma3Activity.class);

        enigma2 = findViewById(R.id.enigma2);
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
                        startListening();
                    }
                } else if (random == 1) {
                    hablar("Si tienéis alguna duda, no dudéis en preguntarme");

                } else if (random == 2) {
                    hablar("¿Has entendido el enigma? Recuerda que si necesitas que te repita el enunciado, solo tienes que pedírmelo");

                } else {
                    hablar("¿Necesitas una pista?");
                    startListening();

                }
                // Configura el handler para que vuelva a ejecutar este bloque en 30 segundos
                handler.postDelayed(this, 120000); // 30,000 milisegundos = 30 segundos /// Antes 120000
            }
        };

        // Iniciar el ciclo de preguntas después de una pequeña demora inicial (opcional)
        handler.postDelayed(runnable, 120000);

        View myView = findViewById(R.id.enigma2); // Reemplaza 'mi_vista' con el ID de tu vista.

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


                    if (text.contains("72") || text.contains("setenta y dos")) {
                        handler.removeCallbacks(runnable);

                        // Cambiar de actividad si esta visible la flecha siguiente
                        if (flechaSiguiente.getVisibility() == View.VISIBLE) {
                            finenigma = true;

                            // Ofreceme respuestas distintas para cada caso
                            int random = (int) (Math.random() * 3);
                            if (random == 0) {
                                hablar("¡Genial! Esa es la respuesta. Gracias por resolver este enigma ¡Sois geniales!");
                            } else if (random == 1) {
                                hablar("¡Correcto! Esa es la respuesta. ¡Sois unos cracks! ");
                            } else {
                                hablar("¡Eso es! ¡Sois unos genios! ");
                            }

                            // Esperar a que termine de hablar
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //hablar("Clica en la flecha para pasar al siguiente enigma");
                            //TODO: SE PASA AL SIGUIENTE ENIGMA
                            intent = new Intent(Enigma2Activity.this, Enigma3Activity.class);
                            startActivity(intent);


                        } else {

                            // Ofreceme respuestas distintas para cada caso
                            int random = (int) (Math.random() * 3);
                            if (random == 0) {
                                hablar("¡Genial! Esa es la respuesta. Gracias por resolver este enigma ¡Sois geniales! Prueba a introducir la respuesta por pantalla y esperemos al resto de familias");
                            } else if (random == 1) {
                                hablar("¡Correcto! Esa es la respuesta. ¡Sois unos cracks! Prueba a introducir la respuesta por pantalla y esperemos al resto de familias");
                            } else {
                                hablar("¡Eso es! ¡Sois unos genios! Sigamos con el siguiente enigma. Prueba a introducir la respuesta por pantalla y esperemos al resto de familias");
                            }

                        }

                    } else if (text.contains("repetir") || text.contains("repite")) {
                        handler.removeCallbacks(runnable);
                        hablar("Claro, el enigma consiste colocar un número en cada casilla, de tal forma que el resultado de la multiplicación de los números de cada fila y columna sea el mismo");
                        handler.postDelayed(runnable, 130000);

                    } else if (text.contains("pista") || text.contains("sí")) {
                        handler.removeCallbacks(runnable);
                        if (pistas == 0) {

                            // Ya no tengo más pistas, pero voy a repetirta alguna. Haz que saque la primera la segunda o la tercera pista de forma aleatoria
                            int pistaAleatoria = (int) (Math.random() * 3) + 1;
                            if (pistaAleatoria == 1) {
                                hablar("Ya no tengo más pistas, pero voy a repetirte alguna. Descarta el número 5");
                            } else if (pistaAleatoria == 2) {
                                hablar("Vale, aquí tienes una pista. El número 7 no se utiliza");
                            } else if (pistaAleatoria == 3) {
                                hablar("Te voy a repetir la tercera pista, debes colocar el número 2 en el centro");
                            }
                            handler.postDelayed(runnable, 120000);
                        } else if (pistas == 3) {

                            hablar("Claro, aquí tienes una primera pista. Voy a ser buena. Descarta el número 5");
                            pistas--;
                            handler.postDelayed(runnable, 120000);
                        } else if (pistas == 2) {
                            hablar("Por supuesto, esta segunda pista es que no te hace falta el 7");
                            pistas--;
                            handler.postDelayed(runnable, 120000);
                        } else if (pistas == 1) {

                            hablar("Claro, pero esta es ya la última pista. Debes colocar el número 2 en el centro ¡Suerte con el enigma!");
                            pistas--;
                            handler.postDelayed(runnable, 120000);
                        }
                    } else if (text.contains("no") || text.contains("no quiero") || text.contains("no gracias")) {
                        handler.removeCallbacks(runnable);
                        // TODO: OFRECER RESPUESTAS MÁS VARIADAS DE FORMA ALEATORIA

                        int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                        if (respuestaAleatoria == 1) {
                            hablar("Vale, si necesitas que te repita el problema, solo tienes que pedírmelo.");
                        } else if (respuestaAleatoria == 2) {
                            hablar("De acuerdo, si necesitas una pista, solo tienes que pedirla.");
                        } else if (respuestaAleatoria == 3) {
                            hablar("Está bien, si necesitas ayuda, no dudes en pedírmela.");
                        }
                        handler.postDelayed(runnable, 120000);
                    } else if (text.contains("ayuda") || text.contains("ayúdame") || text.contains("necesito ayuda")) {
                        handler.removeCallbacks(runnable);

                        int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                        if (respuestaAleatoria == 1) {
                            hablar("Claro, recuerda que puedes tocar mi cabeza y decirme la palabra PISTA, o puedes consultar las pistas clicando en el botón Consultar pista, que se puede ver en mi pantalla.");
                        } else if (respuestaAleatoria == 2) {
                            hablar("Por supuesto, recuerda que si necesitas una pista, solo tienes que pedirla diciéndome la palabra PISTA.");
                        } else if (respuestaAleatoria == 3) {
                            hablar("Está bien, si necesitas que te repita el problema, toca mi cabeza y pídemelo.");
                        }
                        handler.postDelayed(runnable, 120000);
                    } else if (text.contains("gracias") || text.contains("gracias robot")) {
                        handler.removeCallbacks(runnable);
                        // Ofreceme 3 respuestas diferentes aleatorias
                        int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                        if (respuestaAleatoria == 1) {
                            hablar("¡De nada! Recuerda que puedes preguntarme lo que sea.");
                        } else if (respuestaAleatoria == 2) {
                            hablar("¡No hay de qué! Estoy aquí para ayudarte.");
                        } else if (respuestaAleatoria == 3) {
                            hablar("¡No tienes por qué darme las gracias! Estoy aquí para ayudarte.");
                        }
                        handler.postDelayed(runnable, 120000);
                    } else {
                        handler.removeCallbacks(runnable);

                        try {
                            int respuestadeluser = Integer.parseInt(text.toString());

                            if (respuestadeluser < 72) {
                                int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                                if (respuestaAleatoria == 1) {
                                    hablar("¡Vaya! Esa no es la respuesta correcta. Prueba con un número más grande. ¡Ánimo!");
                                } else if (respuestaAleatoria == 2) {
                                    hablar("¡Incorrecto! Ese número es demasiado pequeño. ¡No te rindas!");
                                } else if (respuestaAleatoria == 3) {
                                    hablar("¡Oh no! ¿Quieres que te repita el enunciado? Recuerda que debes multiplicar las filas y las columnas.");
                                } else {
                                    hablar("¡Vaya! Esa no es la respuesta que esperaba, inténtalo de nuevo.");
                                }

                            } else if (respuestadeluser > 72) {
                                int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                                if (respuestaAleatoria == 1) {
                                    hablar("¡Vaya! Esa no es la respuesta correcta. Prueba con un número más pequeño. ¡Ánimo!");
                                } else if (respuestaAleatoria == 2) {
                                    hablar("¡Incorrecto! Ese número es demasiado grande. ¡No te rindas!");
                                } else if (respuestaAleatoria == 3) {
                                    hablar("¡Oh no! ¿Quieres que te repita el enunciado? Recuerda que debes multiplicar las filas y las columnas.");
                                } else {
                                    hablar("¡Vaya! Esa no es la respuesta que esperaba, inténtalo de nuevo.");
                                }

                            }
                        } catch (NumberFormatException e) {

                            int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                            if (respuestaAleatoria == 1) {
                                hablar("¡Vaya! Esa no es la respuesta correcta. ¡No te preocupes, sigue intentándolo!");
                            } else if (respuestaAleatoria == 2) {
                                hablar("¡Incorrecto! Vuelve a intentarlo, estoy segura de que puedes resolverlo. ¡Buena suerte!");
                            } else if (respuestaAleatoria == 3) {
                                hablar("¡Oh no! ¿Quieres que te repita el enunciado?");
                            } else {
                                hablar("¡Vaya! Esa no es la respuesta que esperaba");
                            }

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException d) {
                                d.printStackTrace();
                            }

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

        if(Global.ENIGMA2 == false){
            Global.ENIGMA2 = true;

            // Asegurarse de que la interfaz esté completamente cargada antes de comenzar a hablar
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Primero cargar la imagen
                    enigma2.setVisibility(View.VISIBLE);

                    // Luego de un breve retraso (500ms), iniciar el saludo del robot
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            hablar("Solamente recuerdo una cosa..., no sé qué significa, pero tenéis que ayudarme a descifrarlo y así descubrir el nombre de mi creador");
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            hablar("Para resolverlo debes colocar un número en cada círculo, de tal forma que el resultado de la multiplicación de los números de cada fila y columna sea el mismo.");

                            try {
                                Thread.sleep(9000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            hablar("Mi ayudante os va a repartir una hoja con el enunciado. ");

                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            hablar("¡Parece muy complicado!, ¡Espero que entre todos lo consigamos!");


                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            hablar("Volver a vuestras mesas e intentar resolverlo");
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            hablar("¡Confío en vosotros!, ¡Buena suerte!");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }
                    }, 500);  // Retraso de 500 ms para que la imagen se cargue primero
                }
            }, 100);  // Retraso mínimo para asegurarse de que la interfaz cargue completamente
        }
    }

    public void setonClicks(){

        saltar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecciona una actividad")

                    .setItems(new String[]{"Enigma descrifrar frase", "Enigma descrifrar nombre", "Enigma sopa de letras", }, (dialog, which) -> {
                        if(which == 0){
                            intent = new Intent(Enigma2Activity.this, Enigma3Activity.class);
                        } else if(which == 1){
                            intent = new Intent(Enigma2Activity.this, EnigmaNombre.class);
                        } else if(which == 2){
                            intent = new Intent(Enigma2Activity.this, EnigmaSopa.class);
                        } else{
                            intent = new Intent(Enigma2Activity.this, Enigma3Activity.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Enigma2Activity.this);
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
                hablar("Recuerda que el enigma consiste en colocar un número en cada casilla, de tal forma que el resultado de la multiplicación de los números de cada fila y columna sea el mismo");
                handler.postDelayed(runnable, 120000);
            }
        });

        volverEnigma1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Enigma2Activity.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Estás seguro de que quieres ir al primer enigma?");

                // Botón "Sí"
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Enigma2Activity.this, MainActivity.class);
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



                if(vertical11.getText().toString().isEmpty() || vertical12.getText().toString().isEmpty() || vertical21.getText().toString().isEmpty() || vertical22.getText().toString().isEmpty() || centro.getText().toString().isEmpty()){


                    // Ofrece más respuestas de error
                    Integer respuestaAleatoria = (int) (Math.random() * 4);
                    if (respuestaAleatoria == 0){
                        hablar("¡Vaya! Parece que no has completado todo los círculos");
                    } else if (respuestaAleatoria == 1){
                        hablar("No has completado todos los círculos, si necesitas ayuda no dudes en pedirla");
                    } else if (respuestaAleatoria == 2){
                        hablar("¡Ups! Parece que te has olvidado de rellenar algún círculo");
                    } else {
                        hablar("Parece que te has olvidado de rellenar algún círculo, si necesitas ayuda no dudes en pedirla");
                    }

                    return;
                }

                // TODO: Comprobar y ofrecer respuestas más personalizadas, puede que la fila o una de las columnas esten bien completadas

                Integer resultadoV1 = Integer.parseInt(vertical11.getText().toString()) * Integer.parseInt(vertical12.getText().toString()) * 4;
                Integer resultadoV2 = Integer.parseInt(vertical21.getText().toString()) * Integer.parseInt(vertical22.getText().toString()) * 9;
                Integer horizontal = Integer.parseInt(centro.getText().toString()) * 4 * 9;


                // Comprobar si los números están en los círculos
                if (resultadoV1 == 72 && resultadoV2 == 72 && horizontal == 72) {
                    handler.removeCallbacks(runnable);

                    // Vaciar los edit text
                    vertical11.setText("");
                    vertical12.setText("");
                    vertical21.setText("");
                    vertical22.setText("");
                    centro.setText("");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //TODO: No poner bien las respues, sino decir que está bien pero que ahora le falta decirle la solución de la multiplicación

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        hablar("¡Muy bien! ¡Has resuelto el enigma! Vamos a esperar al resto de familias para que lo resuelvan también.");
                    } else if (respuestaAleatoria == 2){
                        hablar("¡Perfecto! Esos son los números correctos. Antes de decirme la solución de la multiplicación, vamos a esperar a que el resto de familias resuelvan el enigma.");
                    } else if (respuestaAleatoria == 3){
                        hablar("¡Genial! Antes de pasar al siguiente enigma vamos a esperar a que el resto de familias resuelvan este.");
                    } else {
                        hablar("¡Muy bien! ¡Has resuelto el enigma! Vamos a esperar al resto de familias para que lo resuelvan también.");
                    }

                    flechaSiguiente.setVisibility(View.VISIBLE);

                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                } else {

                    // Si los números no están en los círculos, mostrar mensaje de error
                    //systemControl.cambiarEmocion(EmotionsType.SWEAT);
                    //hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_RED);

                    // Comentar si se repite alguno de los números, cada uno de los numeros tiene que ser diferente entre vertical11, vertical12, vertical21, vertical22 y centro
                    if (vertical11.getText().toString().equals(vertical12.getText().toString()) || vertical11.getText().toString().equals(vertical21.getText().toString())
                            || vertical11.getText().toString().equals(vertical22.getText().toString()) || vertical11.getText().toString().equals(centro.getText().toString())
                            || vertical12.getText().toString().equals(vertical21.getText().toString()) || vertical12.getText().toString().equals(vertical22.getText().toString())
                            || vertical12.getText().toString().equals(centro.getText().toString()) || vertical21.getText().toString().equals(vertical22.getText().toString())
                            || vertical21.getText().toString().equals(centro.getText().toString()) || vertical22.getText().toString().equals(centro.getText().toString())
                            || vertical11.getText().toString().equals("4") || vertical12.getText().toString().equals("4") || vertical21.getText().toString().equals("4")
                            || vertical22.getText().toString().equals("4") || centro.getText().toString().equals("4") || vertical11.getText().toString().equals("9")
                            || vertical12.getText().toString().equals("9") || vertical21.getText().toString().equals("9") || vertical22.getText().toString().equals("9") || centro.getText().toString().equals("9") )
                    {
                        hablar("¡Vaya! Parece que has repetido algún número, recuerda que cada círculo debe tener un número diferente");
                    } else if (resultadoV1 == 72 && resultadoV2 != 72 && horizontal != 72) {
                        hablar("¡Oh no! Esa no es la solución, pero parece que la primera columna es correcta ¡Buen trabajo!");

                    } else if(resultadoV2 == 72 && resultadoV1 != 72 && horizontal != 72){
                        hablar("La solución es incorrecta, aunque la segunda columna parece que está perfecta ¡Sigue así!");

                    } else if(resultadoV2 != 72 && resultadoV1 != 72 && horizontal == 72){
                        hablar("Parece que la solución no es correcta, pero la fila horizontal está bien ¡Sigue intentándolo!");

                    } else if(resultadoV2 == 72 && resultadoV1 == 72 && horizontal != 72){
                        hablar("Ya casi lo tienes, las columnas están bien, pero la fila horizontal no es correcta. ¡Sigue intentándolo!");

                    }else if(resultadoV2 == 72 && resultadoV1 != 72 && horizontal == 72){
                        hablar("¡Casi lo tienes! La segunda columna y la fila horizontal están bien, pero la primera columna no es correcta. ¡Ánimo!");

                    }else if(resultadoV2 != 72 && resultadoV1 == 72 && horizontal == 72){
                        hablar("¡Vamos! La primera columna y la fila horizontal están bien, pero la segunda columna no es correcta. ¡Sigue intentándolo!");

                    }else if (resultadoV1 < 72) {
                        hablar("Parece que esa no es la solución, la primera columna es demasiado baja. Intenta con valores más altos.");

                    } else if (resultadoV2 < 72) {
                        hablar("Esa no es la respuesta correcta, la segunda columna es demasiado baja. Intenta con valores más altos.");

                    } else if (horizontal < 72) {
                        hablar("Esa no es la respuesta correcta, la fila horizontal es demasiado baja. Prueba con números más grandes.");
                    } else {
                        hablar("Parece que algo no está bien, la operación horizontal es demasiado alta. Prueba con números más pequeños.");
                    }

                    try{
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    vertical11.setText("");
                    vertical12.setText("");
                    vertical21.setText("");
                    vertical22.setText("");
                    centro.setText("");

                }
            }
        });

        pistasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Enigma2Activity.this);

                // Inflar el diseño personalizado del diálogo
                View dialogView = getLayoutInflater().inflate(R.layout.pistas2, null);
                builder.setView(dialogView);

                // Configurar el TextView del diseño inflado
                TextView messageView = dialogView.findViewById(R.id.tvPistas);
                Button cerrar = dialogView.findViewById(R.id.btnEntendido);
                AlertDialog dialog = builder.create();


                // Cargar la tipografía personalizada
                Typeface customFont = ResourcesCompat.getFont(Enigma2Activity.this, R.font.julee_regular);
                messageView.setTypeface(customFont);
                messageView.setText("Para ver las pistas solo tienes que pedirlas"); // Texto del mensaje

                if (pistas == 0) {
                    messageView.setText("Pista 1: Descarta el número 5\n\n" +
                            "Pista 2: Tampoco te hace falta el 7\n\n" +
                            "Pista 3: Debes colocar el número 2 en el centro\n");
                } else if (pistas == 1) {
                    messageView.setText("Pista 1: Descarta el número 5\n\n" +
                            "Pista 2: Tampoco te hace falta el 7\n");
                } else if (pistas == 2) {
                    messageView.setText("Pista 1: Descarta el número 5\n");
                }

                cerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cerrar el diálogo
                        dialog.dismiss();
                    }
                });

                // Configurar tamaño del diálogo según el diseño inflado



                dialog.show();

            }
        });


        flechaSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finenigma){
                    startActivity(intent);
                } else {
                    // Decir texto
                    hablar("Para pasar al siguiente enigma tenéis que decirme el resultado de la multiplicación de las filas y columnas");
                    // Crear el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(Enigma2Activity.this);

                    // Inflar el diseño personalizado del diálogo
                    View dialogView = getLayoutInflater().inflate(R.layout.decir72, null);
                    builder.setView(dialogView);

                    // Configurar el TextView del diseño inflado
                    TextView messageView = dialogView.findViewById(R.id.tvPistas);
                    Button btnAceptar = dialogView.findViewById(R.id.btnhablar);

                    AlertDialog dialog = builder.create();


                    // Cargar la tipografía personalizada
                    Typeface customFont = ResourcesCompat.getFont(Enigma2Activity.this, R.font.julee_regular);
                    messageView.setTypeface(customFont);
                    messageView.setText("Para pasar al siguiente enigma tenéis que decirme el resultado de la multiplicación de las filas y columnas"); // Texto del mensaje

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
