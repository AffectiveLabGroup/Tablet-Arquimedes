package com.example.sanbotapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class ArquimedesActivity extends TopBaseActivity {

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
    private ProjectorManager projectorManager;

    private ModuloOpenAIAudioSpeech moduloOpenAISpeechVoice;
    private GestionMediaPlayer gestionMediaPlayer;
    private Handler handlerSpeech = new Handler(Looper.getMainLooper());

    private ImageView arq1;
    private ImageView arq2;
    private ImageView arq3;

    private Button exit;



    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.arquimedes);

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
        projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);

        exit = findViewById(R.id.exit);
        faceRecognitionControl.stopFaceRecognition();

        arq1 = findViewById(R.id.arq1);
        arq2 = findViewById(R.id.arq2);
        arq3 = findViewById(R.id.arq3);

        moduloOpenAISpeechVoice = new ModuloOpenAIAudioSpeech();
        gestionMediaPlayer = new GestionMediaPlayer();

        systemManager.switchFloatBar(false, ArquimedesActivity.class.getName());


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
                        projectorManager.switchProjector(false);
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

        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {

                System.out.println("Texto reconocido: " + grammar.getText());

                if (grammar.getText().contains("eureka") || grammar.getText().contains("eureca") || grammar.getText().contains("Eureka")) {

                    speechControl.hablar("Exacto Eureka, ¡Hasta la próxima! ¡Eureka! ¡Eureka! Gracias por despertarme y ayudarme a recordar el nombre de mi creador");

                    hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_FLICKER_GREEN);

                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if( grammar.getText().contains("oro") ) {

                    speechControl.hablar("Sí, debía ser un objeto de oro, pero ¿qué más debía tener para que Arquímedes pudiera demostrar que no era de oro puro?");

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;

                } else if( grammar.getText().contains("mismo peso") ) {

                    speechControl.hablar("Casi, casi, ¿Mismo peso el qué?");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return true;

                }

                else {
                    //Respuestas aleatorias
                    int random = (int) (Math.random() * 3) + 1;
                    switch (random) {
                        case 1:
                            speechControl.hablar("¡Vaya! Esa no es la respuesta correcta, inténtalo de nuevo.");
                            break;
                        case 2:
                            speechControl.hablar("No, esa no es la respuesta que esperaba, vuelve a intentarlo.");
                            break;
                        case 3:
                            speechControl.hablar("Piensa un poquito más, seguro que lo consigues.");
                            break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }

                return false;

            }

            @Override
            public void onRecognizeVolume(int i) {
                System.out.println("onRecognizeVolume ----------------------------------------------");
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

        // Asegurarse de que la interfaz esté completamente cargada antes de mostrar el diálogo
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preguntarSiProyectar(); // Llamada al método para mostrar el diálogo
            }
        }, 500); // Retraso de 500ms para asegurarse de que la interfaz esté lista
    }

    private void preguntarSiProyectar() {
        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Deseas que proyecte la presentación?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción cuando el usuario elige "Sí"
                        proyectarPresentacion();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción cuando el usuario elige "No"
                        iniciarSaludo2();
                    }
                });

        // Mostrar el diálogo
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void proyectarPresentacion() {
        // Aquí iría el código para iniciar la proyección
        projectorManager.switchProjector(true);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        projectorManager.setMode(ProjectorManager.MODE_WALL);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        projectorManager.setBright(31);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        projectorManager.setTrapezoidV(30);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        iniciarSaludo2();
    }


        private void iniciarSaludo2() {
            new Thread(() -> {
                try {
                    // Primer diálogo
                    speechControl.hablar("Estoy muy contenta de que hayáis conseguido llegar hasta aquí.");
                    Thread.sleep(5500);

                    speechControl.hablar("Arquímedes fue mi creador, un gran matemático, físico, ingeniero, inventor y astrónomo griego.");
                    Thread.sleep(9000);

                    speechControl.hablar("No sé cómo se me pudo olvidar su nombre, pero gracias a vosotros he podido recordarlo.");
                    Thread.sleep(6000);

                    // Cambio de imágenes (debe ejecutarse en el hilo de la UI)
                    runOnUiThread(() -> {
                        arq1.setVisibility(View.GONE);
                        arq2.setVisibility(View.VISIBLE);
                    });
                    Thread.sleep(1000);

                    // Tercera parte del diálogo
                    speechControl.hablar("Una de las historias más famosas sobre Arquímedes es la del baño.");
                    Thread.sleep(5500);

                    speechControl.hablar("Cuenta la historia que Arquímedes recibió un encargo del rey Herión, que quería saber si la corona que había adquirido era realmente de oro.");
                    Thread.sleep(10000);

                    speechControl.hablar("Un día, mientras se bañaba, dio con la solución. Descubrió el principio de la flotación al ver cómo el agua se desbordaba al entrar en la bañera.");
                    Thread.sleep(11000);

                    speechControl.hablar("Entusiasmado por su descubrimiento, salió corriendo gritando '¡Eureka! ¡Eureka!'. Que en griego significa ¡Lo he encontrado! ");
                    Thread.sleep(9000);

                    // Última parte del diálogo
                    speechControl.hablar("Arquímedes pensó que si introducía la corona en la bañera, podía descubrir de qué estaba hecha midiendo el volumen de agua que desbordaba, que sería diferente según el material del que estuviera hecha.\n");
                    Thread.sleep(16000);

                    speechControl.hablar("Espero que os haya gustado la historia de Arquímedes y que hayáis aprendido algo nuevo sobre él.");
                    Thread.sleep(8000);


                    runOnUiThread(() -> {
                        arq2.setVisibility(View.GONE);
                        arq3.setVisibility(View.VISIBLE);
                    });
                    Thread.sleep(1000);

                    speechControl.hablar("¿Recordáis que palabra gritó Arquímedes al dar con este descubrimiento?");
                    Thread.sleep(6000);

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
