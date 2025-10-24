package com.example.sanbotapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
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

public class EnigmaNombre extends TopBaseActivity {

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
    private Button fin;
    private Button atras;
    private Button saltar;

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
        setContentView(R.layout.enigmanombre);

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
        fin = findViewById(R.id.fin);
        atras = findViewById(R.id.atras);
        exit = findViewById(R.id.exit);
        repetir = findViewById(R.id.repetir);
        saltar = findViewById(R.id.saltar);

        enigma3.setVisibility(View.VISIBLE);

        systemManager.switchFloatBar(false, EnigmaNombre.class.getName());

        faceRecognitionControl.stopFaceRecognition();

        intent = new Intent(EnigmaNombre.this, ArquimedesActivity.class);


        // Si sanbot detecta la palabra "hola" en el audio, entonces saluda
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                System.out.println("Texto reconocido: " + grammar.getText());

                if ( grammar.getText().contains("Arquímedes") || grammar.getText().contains("arquimedes") || grammar.getText().contains("arquímedes") || grammar.getText().contains("Arquimedes")) {


                    if (fin.getVisibility() == View.VISIBLE) {
                        // Expresion feliz
                        finenigma = true;
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

                        //Intent intent = new Intent(EnigmaNombre.this, ArquimedesActivity.class);
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
                    speechControl.hablar("Claro, el enigma consiste en descrifrar la palabra que se muestra en la pantalla. Seguro que con eso conseguimos descubrir el nombre de mi creador");
                    return true;

                } else if (grammar.getText().contains("pista") || grammar.getText().equals("sí")){

                    // Respuestas aleatorias y variadas del estilo, vaya, parece que no tengo ninguna pista
                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("Vaya, parece que no tengo ninguna pista que darte.");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("Lo siento, no tengo ninguna pista, tendrás que descibrirlo sin mi ayuda.");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("Lo siento, no tengo ninguna pista que pueda darte.");
                    }


                } else if(grammar.getText().contains("no") || grammar.getText().contains("no quiero") || grammar.getText().contains("no gracias")){

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("Vale, si necesitas que te repita el problema, solo tienes que pedírmelo.");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("De acuerdo, si necesitas que te repita el enunciado solo tienes que pedirlo.");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("Está bien, si necesitas ayuda, no dudes en pedírmela.");
                    }
                    return true;
                } else if(grammar.getText().contains("ayuda") || grammar.getText().contains("ayúdame") || grammar.getText().contains("necesito ayuda")){

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("Claro, recuerda que puedes pedirme que te repita el enunciado si lo necesitas.");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("Por supuesto, recuerda que puedes pedirme que te repita el enunciado si lo necesitas.");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("Está bien, si necesitas que te repita el problema, toca mi cabeza y pídemelo.");
                    }
                    return true;
                } else if(grammar.getText().contains("gracias") || grammar.getText().contains("gracias sol") || grammar.getText().contains("gracias robot")){

                    int respuestaAleatoria = (int) (Math.random() * 3) + 1;
                    if (respuestaAleatoria == 1){
                        speechControl.hablar("¡De nada! Recuerda que puedes preguntarme lo que sea.");
                    } else if (respuestaAleatoria == 2){
                        speechControl.hablar("¡No hay de qué! Estoy aquí para ayudarte.");
                    } else if (respuestaAleatoria == 3){
                        speechControl.hablar("¡No tienes por qué darme las gracias! Estoy aquí para ayudarte.");
                    }
                    return true;
                } else{
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

                speechControl.hablar("Recordar venir a mi para decirme la respuesta.");

                try {
                    Thread.sleep(4000);
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
                speechControl.hablar("Recuerda que tienes que descifrar la palabra para resolver el enigma. ¡Buena suerte!");

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

                    // Expresion feliz
                    systemControl.cambiarEmocion(EmotionsType.SMILE);
                    hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_GREEN);

                    // Borrar el texto de la respuesta
                    respuesta.setText("");

                    int random = (int) (Math.random() * 3);
                    if(random == 0){
                        speechControl.hablar("¡Eureka! Has conseguido descifrar la palabra, vamos a esperar al resto de familias para comprobar si han conseguido descifrarla también. ¡Enhorabuena!");
                    } else if(random == 1){
                        speechControl.hablar("Exacto, sois geniales, descifrar esa palabra no era tarea fácil. Esperemos al resto de familias seguro que también lo consiguen. ¡Enhorabuena!");
                    } else if(random == 2){
                        speechControl.hablar("Esa es la respuesta, gracias por descifrar la palabra. Ahora esperemos a que el resto de familias también lo consigan. ¡Enhorabuena!");
                    } else {
                        speechControl.hablar("¡Fantástico! Habéis conseguido descifrar la palabra. Vamos a esperar a que el resto de familias también lo consigan. ¡Enhorabuena!");
                    }

                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    fin.setVisibility(View.VISIBLE);

                } else {


                    systemControl.cambiarEmocion(EmotionsType.SWEAT);
                    hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_RED);
                    // ofrecer distintas frases de error
                    int random = (int) (Math.random() * 3);
                    if(random == 0){
                        speechControl.hablar("¡Vaya! Parece que esa no es la palabra que buscamos, sigue intentándolo.");
                    } else if(random == 1){
                        speechControl.hablar("¡Oh no! Esa no es la respuesta correcta, sigue intentándolo.");
                    } else if(random == 2){
                        speechControl.hablar("¡Vaya! Parece que esa no es la palabra que buscamos, sigue intentándolo.");
                    } else {
                        speechControl.hablar("¡Incorrecto! Sigue intentándolo, estoy segura de que lo encontrarás.");
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
                    speechControl.hablar("Para continuar con la exposición teneis que decirme el nombre de mi creador.");


                    // Crear el AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(EnigmaNombre.this);

                    // Inflar el diseño personalizado del diálogo
                    View dialogView = getLayoutInflater().inflate(R.layout.decir72, null);
                    builder.setView(dialogView);

                    // Configurar el TextView del diseño inflado
                    TextView messageView = dialogView.findViewById(R.id.tvPistas);
                    AlertDialog dialog = builder.create();


                    // Cargar la tipografía personalizada
                    Typeface customFont = ResourcesCompat.getFont(EnigmaNombre.this, R.font.julee_regular);
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
