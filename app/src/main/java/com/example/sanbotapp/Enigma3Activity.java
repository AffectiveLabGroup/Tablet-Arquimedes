package com.example.sanbotapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
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

public class Enigma3Activity extends TopBaseActivity {

    private SpeechControl speechControl;
    private FaceRecognitionControl faceRecognitionControl;
    private SpeechManager speechManager;
    private MediaManager mediaManager;
    private SystemControl systemControl;
    private SystemManager systemManager;
    private HeadControl headControl;
    private HeadMotionManager headMotionManager;
    private WheelControl wheelControl;
    private WheelMotionManager wheelMotionManager;
    private HardwareControl hardwareControl;
    private HardWareManager hardWareManager;

    private ImageView enigma3;
    private EditText respuesta;
    private Button comprobar;
    private Button pista;
    private Button fin;
    private Button atras;
    private Button saltar;

    private Integer pistas = 3;

    private Runnable runnable;
    private Handler handler = new Handler();

    private Button exit;
    private Button repetir;

    private Intent intent = null;
    private Boolean finenigma = false;



    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.enigma3);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        speechControl = new SpeechControl(speechManager);
        faceRecognitionControl = new FaceRecognitionControl(speechManager, mediaManager);
        systemControl = new SystemControl(systemManager);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        headControl = new HeadControl(headMotionManager);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        wheelControl = new WheelControl(wheelMotionManager);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        hardwareControl = new HardwareControl(hardWareManager);

        enigma3 = findViewById(R.id.enigma3);
        respuesta = findViewById(R.id.respuesta);
        comprobar = findViewById(R.id.comprobar);
        pista = findViewById(R.id.pistas);
        fin = findViewById(R.id.fin);
        atras = findViewById(R.id.atras);
        exit = findViewById(R.id.exit);
        repetir = findViewById(R.id.repetir);
        saltar = findViewById(R.id.saltar);

        enigma3.setVisibility(View.VISIBLE);

        systemManager.switchFloatBar(false,Enigma3Activity.class.getName());


        intent = new Intent(Enigma3Activity.this, ArquimedesActivity.class);

        faceRecognitionControl.stopFaceRecognition();

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
                        speechControl.hablar("¿Necesitas una pista?");
                        while (speechControl.isRobotHablando()) {
                        }
                        // Poner a la escucha
                        speechManager.doWakeUp();
                    } else{
                        speechControl.hablar("Ya no me quedan más pistas, ¿Quieres que te repita alguna?");
                        while (speechControl.isRobotHablando()) {
                        }
                        // Poner a la escucha
                        speechManager.doWakeUp();
                    }
                } else if (random == 1) {
                    speechControl.hablar("Si tienéis alguna duda, no dudéis en preguntarme");
                    while (speechControl.isRobotHablando()) {
                    }
                } else if (random == 2) {
                    speechControl.hablar("¿Has entendido el enigma? Recuerda que si necesitas que te repita el enunciado, solo tienes que pedírmelo");
                    while (speechControl.isRobotHablando()) {
                    }

                } else {
                    speechControl.hablar("¿Necesitas una pista?");
                    while (speechControl.isRobotHablando()) {
                    }
                    // Poner a la escucha
                    speechManager.doWakeUp();
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


        // Si sanbot detecta la palabra "hola" en el audio, entonces saluda
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                System.out.println("Texto reconocido: " + grammar.getText());

                if ( grammar.getText().contains("Arquímedes") || grammar.getText().contains("arquimedes") || grammar.getText().contains("arquímedes") || grammar.getText().contains("Arquimedes")) {
                    handler.removeCallbacks(runnable);

                    if (fin.getVisibility() == View.VISIBLE) {
                        finenigma = true;
                        // Expresion feliz
                        systemControl.cambiarEmocion(EmotionsType.SMILE);
                        hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_GREEN);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // ofrecer más respuestas variadas
                        int random = (int) (Math.random() * 3);
                        if(random == 0){
                            speechControl.hablar("¡Eureka! Sí, el es mi creador. Uno de los hombres más inteligentes de la historia.");
                        } else if(random == 1){
                            speechControl.hablar("Exacto, no sé cómo se me pudo olvidar el nombre de uno de los hombres más inteligentes de la historia.");
                        } else if(random == 2){
                            speechControl.hablar("Claro, es cierto, el es mi creador, no sé cómo se me pudo olvidar.");
                        } else {
                            speechControl.hablar("¡Eureka! Esa era la respuesta correcta, gracias por descubrir el nombre de mi creador.");
                        }

                        // Girar
                        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.GIRAR);

                        try{
                            Thread.sleep(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        speechControl.hablar("Ahora que sabes quién es mi creador, me gustaría hablarte un poco sobre él. Clica en la flecha para continuar. ");
                        //TODO: MANDAR A LA SIGUIENTE ACTIVIDAD E INTRODUCIR QUE VA A HACER UNA PRESENTACIÓN SOBRE ARQUÍMEDES


                        //Intent intent = new Intent(Enigma3Activity.this, ArquimedesActivity.class);
                        //startActivity(intent);
                    } else{
                        // Expresion feliz
                        systemControl.cambiarEmocion(EmotionsType.SMILE);
                        hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_GREEN);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // ofrecer más respuestas variadas
                        int random = (int) (Math.random() * 3);
                        if(random == 0){
                            speechControl.hablar("¡Eureka! Sí, el es mi creador. Uno de los hombres más inteligentes de la historia.Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        } else if(random == 1){
                            speechControl.hablar("Exacto, no sé cómo se me pudo olvidar el nombre de uno de los hombres más inteligentes de la historia.Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        } else if(random == 2){
                            speechControl.hablar("Claro, es cierto, el es mi creador, no sé cómo se me pudo olvidar. Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        } else {
                            speechControl.hablar("¡Eureka! Esa era la respuesta correcta, gracias por descubrir el nombre de mi creador. Prueba a introducir la respuesta en la pantalla, y esperemos al resto de familias");
                        }

                        try{
                            Thread.sleep(7000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }



                    return true;
                } else if (grammar.getText().contains("repetir") || grammar.getText().contains("repite")){
                    handler.removeCallbacks(runnable);
                    speechControl.hablar("Claro, el enigma consiste en descrifrar la frase que se muestra en la pantalla. Seguro que con eso conseguimos descubrir el nombre de mi creador");
                    handler.postDelayed(runnable, 120000);
                    return true;

                } else if (grammar.getText().contains("pista") || grammar.getText().equals("sí")){
                    handler.removeCallbacks(runnable);
                    if(pistas == 0){

                        // Ya no tengo más pistas, pero voy a repetirta alguna. Haz que saque la primera la segunda o la tercera pista de forma aleatoria
                        int pistaAleatoria = (int) (Math.random() * 3) + 1;
                        if (pistaAleatoria == 1){
                            speechControl.hablar("Ya no tengo más pistas, pero voy a repetirte alguna. Quizás la letra obtenida en el primer enigma tenga algo que ver con la letra d");
                        } else if (pistaAleatoria == 2){
                            speechControl.hablar("Vale, aquí tienes una pista. ¿Recuerdas la solución del enigma anterior? Si restas las cifras y le quitas 2, aparece un número importante");
                        } else if (pistaAleatoria == 3){
                            speechControl.hablar("Te voy a repetir una de las pistas, ¿Por qué no comienzas por la palabra más corta?");
                        }
                        handler.postDelayed(runnable, 120000);
                        return true;
                    } else if (pistas == 3){

                        speechControl.hablar("¿Por qué no comienzas por la palabra más corta?");
                        pistas--;
                        handler.postDelayed(runnable, 120000);
                        return true;
                    } else if (pistas == 2){

                        speechControl.hablar("¿Recuerdas la solución del enigma anterior? Si restas las cifras y le quitas 2, aparece un número importante");
                        pistas--;
                        handler.postDelayed(runnable, 120000);
                        return true;
                    } else if (pistas == 1){
                        speechControl.hablar("Esta es ya tu última pista, quizás la letra obtenida en el primer enigma tenga algo que ver con la letra d");
                        pistas--;
                        handler.postDelayed(runnable, 120000);
                        return true;
                    }
                } else if(grammar.getText().contains("no") || grammar.getText().contains("no quiero") || grammar.getText().contains("no gracias")){
                    handler.removeCallbacks(runnable);
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("Vale, si necesitas que te repita el problema, solo tienes que pedírmelo.");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("De acuerdo, si necesitas una pista, solo tienes que pedirla.");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("Está bien, si necesitas ayuda, no dudes en pedírmela.");
                    }
                    handler.postDelayed(runnable, 120000);
                    return true;
                } else if(grammar.getText().contains("ayuda") || grammar.getText().contains("ayúdame") || grammar.getText().contains("necesito ayuda")){
                    handler.removeCallbacks(runnable);
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("Claro, recuerda que puedes tocar mi cabeza y decirme la palabra PISTA, o puedes consultar las pistas clicando en el botón Consultar pista, que se puede ver en mi pantalla.");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("Por supuesto, recuerda que si necesitas una pista, solo tienes que pedirla diciéndome la palabra PISTA.");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("Está bien, si necesitas que te repita el problema, toca mi cabeza y pídemelo.");
                    }
                    handler.postDelayed(runnable, 120000);
                    return true;
                } else if(grammar.getText().contains("gracias") || grammar.getText().contains("gracias sol") || grammar.getText().contains("gracias robot")){
                    handler.removeCallbacks(runnable);
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("¡De nada! Recuerda que puedes preguntarme lo que sea.");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("¡No hay de qué! Estoy aquí para ayudarte.");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("¡No tienes por qué darme las gracias! Estoy aquí para ayudarte.");
                    }
                    handler.postDelayed(runnable, 120000);
                    return true;
                } else{
                    handler.removeCallbacks(runnable);
                    systemControl.cambiarEmocion(EmotionsType.SWEAT);
                    hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_RED);

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("¡Vaya! Esa no es la respuesta correcta. ¡No te preocupes, sigue intentándolo!");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("¡Incorrecto! Vuelve a intentarlo, estoy segura de que puedes resolverlo. ¡Buena suerte!");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("¡Incorrecto! ¿Quieres que te repita el enunciado?");
                    } else {
                        speechControl.hablar("¡Vaya! Esa no es la respuesta que esperaba");
                    }

                    while (speechControl.isRobotHablando()) {
                    }
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException d) {
                        d.printStackTrace();
                    }

                    handler.postDelayed(runnable, 120000);
                    return true;
                }
                System.out.println(grammar.getText());
                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {
                System.out.println("onRecognizeVolume ----------------------------------------------");
            }

        });

        setonClicks();

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

                speechControl.hablar("Ahora que lo pienso, creo que mi creador me dejó una nota para ayudarme a recordar su nombre");


                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                systemControl.cambiarEmocion(EmotionsType.CRY);
                speechControl.hablar("¡Vaya! Parece que la nota está cifrada. ¿Pueden ayudarme a descifrarla? ¡Por favor! ¡Díganme el nombre de mi creador!");
                // Primero cargar la imagen


                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                systemControl.cambiarEmocion(EmotionsType.SMILE);
                speechControl.hablar("Al igual que antes, mi ayudante os repartirá una hoja con el enigma");

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                speechControl.hablar("Recordar venir a mi para decirme la respuesta o pedirme pistas.");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                speechControl.hablar("Ya podéis ir a vuestras mesas ¡Buena suerte!");

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
                speechControl.hablar("Recuerda que tienes que descifrar la frase para resolver el enigma. ¡Buena suerte!");
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
                    // Expresion feliz
                    systemControl.cambiarEmocion(EmotionsType.SMILE);
                    hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_GREEN);

                    // Borrar el texto de la respuesta
                    respuesta.setText("");

                    int random = (int) (Math.random() * 3);
                    if(random == 0){
                        speechControl.hablar("¡Eureka! Has conseguido descifrar la frase, vamos a esperar al resto de familias para comprobar si han conseguido descifrarla también. ¡Enhorabuena!");
                    } else if(random == 1){
                        speechControl.hablar("Exacto, sois geniales, descifrar esa frase no era tarea fácil. Esperemos al resto de familias seguro que también lo consiguen. ¡Enhorabuena!");
                    } else if(random == 2){
                        speechControl.hablar("Esa es la respuesta, gracias por descifrar la frase. Ahora esperemos a que el resto de familias también lo consigan. ¡Enhorabuena!");
                    } else {
                        speechControl.hablar("¡Fantástico! Habéis conseguido descifrar la frase. Vamos a esperar a que el resto de familias también lo consigan. ¡Enhorabuena!");
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

                        speechControl.hablar("¡Ya te falta menos! Parece que la primera palabra es correcta ¡mucho ánimo!");

                    } else if ( respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && !respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        speechControl.hablar("¡Casi lo tienes! Parece que las dos primeras palabras son correctas, sigue intentándolo.");

                    } else if (respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        speechControl.hablar("¡Estás muy cerca! Parece que las tres primeras palabras son correctas, ¡ya casi lo tienes!");

                    } else if (respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && respuesta.getText().toString().contains("es") && (respuesta.getText().toString().contains("arquimedes")
                            || respuesta.getText().toString().contains("Arquimedes")
                            || respuesta.getText().toString().contains("arquímedes")
                            || respuesta.getText().toString().contains("Arquímedes"))){

                        speechControl.hablar("¡Exacto! Has conseguido descifrar la frase, ahora solo tienes que decirme el nombre de mi creador.");

                    } else if ( !respuesta.getText().toString().contains("mi") && respuesta.getText().toString().contains("nombre") && !respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        speechControl.hablar("¡Casi lo tienes! Parece que la segunda palabra es correcta, sigue intentándolo.");

                    } else if ( !respuesta.getText().toString().contains("mi") && !respuesta.getText().toString().contains("nombre") && respuesta.getText().toString().contains("es") &&
                            !respuesta.getText().toString().contains("arquimedes") && !respuesta.getText().toString().contains("Arquimedes") && !respuesta.getText().toString().contains("arquímedes") && !respuesta.getText().toString().contains("Arquímedes")){

                        speechControl.hablar("¡Ya falta menos! Parece que la tercera palabra es correcta.");

                    } else if ( respuesta.getText().toString().contains("arquimedes")
                            || respuesta.getText().toString().contains("Arquimedes")
                            || respuesta.getText().toString().contains("arquímedes")
                            || respuesta.getText().toString().contains("Arquímedes")) {

                        speechControl.hablar("¡Casi lo tienes! Parece que has descifrado una palabra importante, sigue intentándolo.");
                    } else {
                        systemControl.cambiarEmocion(EmotionsType.SWEAT);
                        hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_RED);
                        speechControl.hablar("¡Vaya! Parece que la frase no es correcta, sigue intentándolo.");
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
                    speechControl.hablar("Para continuar con la exposición teneis que decirme el nombre de mi creador.");

                    // Crear el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(Enigma3Activity.this);

                    // Inflar el diseño personalizado del diálogo
                    View dialogView = getLayoutInflater().inflate(R.layout.decir72, null);
                    builder.setView(dialogView);

                    // Configurar el TextView del diseño inflado
                    TextView messageView = dialogView.findViewById(R.id.tvPistas);
                    AlertDialog dialog = builder.create();


                    // Cargar la tipografía personalizada
                    Typeface customFont = ResourcesCompat.getFont(Enigma3Activity.this, R.font.julee_regular);
                    messageView.setTypeface(customFont);
                    messageView.setText("Para continuar con la exposición teneis que decirme el nombre de mi creador"); // Texto del mensaje

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
