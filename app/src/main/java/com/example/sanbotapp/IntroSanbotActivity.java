package com.example.sanbotapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.res.ResourcesCompat;
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

public class IntroSanbotActivity extends TopBaseActivity {

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
    private HandsControl handsControl;
    private HandMotionManager handMotionManager;

    private ModuloOpenAIAudioSpeech moduloOpenAISpeechVoice;
    private GestionMediaPlayer gestionMediaPlayer;
    private Handler handlerSpeech = new Handler(Looper.getMainLooper());

    private ImageView imgFondo;
    private TextView dos;
    private TextView pista;
    private Button btnRepetirSuma;
    private Button btnRepetirPista;
    private Button exit;

    private Intent intent = null;




    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.explicacion);

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
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        handsControl = new HandsControl(handMotionManager);

        faceRecognitionControl.stopFaceRecognition();

        imgFondo = findViewById(R.id.fondo);
        dos = findViewById(R.id.explicacion);
        pista = findViewById(R.id.pista);
        btnRepetirPista = findViewById(R.id.btnRepetirPista);
        btnRepetirSuma = findViewById(R.id.btnRepetirSuma);
        exit = findViewById(R.id.exit);

        moduloOpenAISpeechVoice = new ModuloOpenAIAudioSpeech();
        gestionMediaPlayer = new GestionMediaPlayer();

        systemManager.switchFloatBar(false,IntroSanbotActivity.class.getName());


        setonClicks();

        // Si sanbot detecta la palabra "hola" en el audio, entonces saluda
        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                System.out.println("Texto reconocido: " + grammar.getText());

                if (grammar.getText().contains("cuatro")||grammar.getText().contains("4")) {

                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    dos.setVisibility(View.GONE);
                    btnRepetirSuma.setVisibility(View.GONE);
                    btnRepetirPista.setVisibility(View.VISIBLE);
                    pista.setVisibility(View.VISIBLE);

                    // Expresion feliz
                    systemControl.cambiarEmocion(EmotionsType.SMILE);
                    speechControl.hablar("¡Genial! Veo que lo vais pillando.");

                    try{
                        Thread.sleep(3000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    speechControl.hablar("Una cosa más, mientras esteis resolviendo el ejercicio, podéis venir y pedirme una pista. Para ello, siguiendo los pasos de antes, tendréis que decirme la palabra PISTA.");

                    try{
                        Thread.sleep(13500);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    speechControl.hablar("Ayudante, elige a alguien del grupo y probemos");
                    while(speechControl.isRobotHablando()) {
                    }

                    return true;

                } else if (grammar.getText().contains("pista")) {
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    systemControl.cambiarEmocion(EmotionsType.QUESTION);
                    speechControl.hablar("¿Quieres una pista? Primero tendrás que intentar resolver el enigma");


                    try{
                        Thread.sleep(5000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    speechControl.hablar("¡Vamos a ello!");

                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    startActivity(intent);

                    return true;
                } else {
                    speechControl.hablar("Uy, esa no es la repuesta que esperaba, intentalo de nuevo.");
                    try{
                        Thread.sleep(3000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }

            }

            @Override
            public void onRecognizeVolume(int i) {
                System.out.println("onRecognizeVolume ----------------------------------------------");
            }

        });

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
                speechControl.hablar("Mi ayudante elegirá a un voluntario que me diga el resultado de la suma que se puede ver en mi pantalla, recordar" +
                        "que teneis que tocar mi cabeza y esperar a que mis orejas se pongan de color verde antes de decirme la respuesta");

            }
        });

        btnRepetirPista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechControl.hablar("Al igual que hemos hecho antes con la suma, mi ayudante va a elegir a un voluntario para que me diga la palabra PISTA");

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

                        systemControl.cambiarEmocion(EmotionsType.GOODBYE);
                        hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_PINK);

                        AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 30);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);


                        speechControl.hablar("Soy un poco tímida, por lo que agradecería que me hablarais de uno en uno");

                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hardwareControl.apagarLED(LED.PART_ALL);

                        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_BOTH, 5, 170);
                        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                        systemControl.cambiarEmocion(EmotionsType.FAINT);
                        speechControl.hablar("Aún sigo un poco confusa.");


                        try {
                            Thread.sleep(4500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // TODO: HACER PARIPE DE QUE ME ELIGE COMO AYUDANTE
                        systemControl.cambiarEmocion(EmotionsType.SMILE);
                        speechControl.hablar("Mi ayudante se encargará de elegir a una persona para hablar conmigo.");


                        try {
                            Thread.sleep(7000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        speechControl.hablar("Cuando tengais la respuesta podéis acercaros y tocarme la cabeza");


                        try {
                            Thread.sleep(7000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hardwareControl.encenderLED(LED.PART_LEFT_HEAD, LED.MODE_GREEN);
                        hardwareControl.encenderLED(LED.PART_RIGHT_HEAD, LED.MODE_GREEN);


                        speechControl.hablar("Cuando mis orejas estén de color verde como ahora, podréis decirme la respuesta.");

                        try {
                            Thread.sleep(7000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_CLOSE);



                        imgFondo.setVisibility(View.VISIBLE);
                        dos.setVisibility(View.VISIBLE);
                        exit.setVisibility(View.VISIBLE);
                        btnRepetirSuma.setVisibility(View.VISIBLE);
                        speechControl.hablar("¡Hagamos una prueba!, ayudante, elige a un voluntario que me diga el resultado de la suma que se puede ver en mi pantalla");


                        try {
                            Thread.sleep(7000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

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


}
