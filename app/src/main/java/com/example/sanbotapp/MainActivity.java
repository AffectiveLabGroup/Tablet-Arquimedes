package com.example.sanbotapp;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanbotapp.gestion.GestionMediaPlayer;
import com.example.sanbotapp.moduloOpenAI.ModuloOpenAIAudioSpeech;
import com.example.sanbotapp.robotControl.FaceRecognitionControl;
import com.example.sanbotapp.robotControl.HeadControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.example.sanbotapp.robotControl.SystemControl;
import com.example.sanbotapp.robotControl.WheelControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.beans.OperationResult;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

public class MainActivity extends TopBaseActivity {

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
    private HandMotionManager handMotionManager;

    private Button transparencia;
    private Button volverIntro;
    private Button volverEnigma;
    private Button boton;
    private Button goBoton;
    private Button volverIntroCorrecta;
    private Button goEnigma;
    private Button exit;
    private Button saltar;

    private TextView imgFondo;
    private Button btnRespuesta;
    private LinearLayout imgFondoenigma;
    private EditText etRespuesta;
    private ImageView imageViewBoton;
    private ImageView fondo;
    private String intentgo = "Enigma2Activity";

    private Intent intentG = null;

    private Runnable sleepRunnable;
    private Handler handlerSleep = new Handler();

    private AudioManager audioManager;
    MediaPlayer mp1;
    MediaPlayer mp2;

    private boolean siguienteAccion = false;


    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_main);

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
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);

        transparencia = findViewById(R.id.transparencia);
        volverIntro = findViewById(R.id.volverIntro);
        imgFondo = findViewById(R.id.imageView);
        btnRespuesta = findViewById(R.id.btnRespuesta);
        imgFondoenigma = findViewById(R.id.imageViewEnigma1);
        etRespuesta = findViewById(R.id.etRespuesta);
        imageViewBoton = findViewById(R.id.imageViewBoton);
        volverEnigma = findViewById(R.id.volverEnigma);
        boton = findViewById(R.id.despertar);
        goBoton = findViewById(R.id.goboton);
        volverIntroCorrecta = findViewById(R.id.volverIntro_correcta);
        goEnigma = findViewById(R.id.goEnigmaCorrecto);
        exit = findViewById(R.id.exit);
        fondo = findViewById(R.id.imageViewFondo);
        saltar = findViewById(R.id.saltar);


        faceRecognitionControl.stopFaceRecognition();

        mp1 = MediaPlayer.create(MainActivity.this,R.raw.error);
        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp1.release();
            }
        }    );


        mp2 = MediaPlayer.create(MainActivity.this,R.raw.correct);
        mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp2.release();
            }
        }    );


        setonClicks();

        systemManager.switchFloatBar(false,MainActivity.class.getName());
    }

    public OperationResult switchFloatBar(boolean isShow,String className){
        return systemManager.switchFloatBar(isShow,className);
    }

    // Una vez iniciada la aplicacion quiero que el robot me salude
    @Override
    public void onResume() {
        super.onResume();
        //
       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgFondo.setVisibility(View.VISIBLE);
                // Bajar cabeza
                headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.ABAJO);
            }
        }, 100);

        // Iniciar el ciclo de "sleep"
        iniciarCicloSleep();

    }

    private void iniciarCicloSleep() {
        // Crear un runnable que aplicará la emoción de "sleep" cada cierto tiempo
        sleepRunnable = new Runnable() {
            @Override
            public void run() {
                systemControl.cambiarEmocion(EmotionsType.SLEEP);
                // Reprogramar para que se ejecute nuevamente en 18 segundos
                handlerSleep.postDelayed(this, 8000); // 8,000 ms = 8 segundos
            }
        };
        // Iniciar la primera ejecución del runnable
        handlerSleep.post(sleepRunnable);
    }

    private void detenerCicloSleep() {
        // Detener la ejecución del ciclo de "sleep"
        if (handlerSleep != null) {
            handlerSleep.removeCallbacks(sleepRunnable);
        }
    }

    public void setonClicks() {

        saltar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecciona una actividad")

                    .setItems(new String[]{"Segundo enigma", "Enigma descrifrar frase", "Enigma descrifrar nombre", "Enigma sopa de letras", }, (dialog, which) -> {
                        if(which == 0){
                            intentgo = "Enigma2Activity";
                        } else if(which == 1){
                            intentgo = "Enigma3Activity";
                        } else if(which == 2){
                            intentgo = "EnigmaNombre";
                        } else if(which == 3){
                            intentgo = "EnigmaSopa";
                        }
                    })
                    .setNegativeButton("SALTAR", (dialog, which) -> {
                        if (intentgo.equals("Enigma2Activity")) {
                            intentG = new Intent(MainActivity.this, Enigma2Activity.class);
                        } else if(intentgo.equals("Enigma3Activity")){
                            intentG = new Intent(MainActivity.this, Enigma3Activity.class);
                        } else if(intentgo.equals("EnigmaNombre")){
                            intentG = new Intent(MainActivity.this, EnigmaNombre.class);
                        } else if(intentgo.equals("EnigmaSopa")){
                            intentG = new Intent(MainActivity.this, EnigmaSopa.class);
                        } else {
                            intentG = new Intent(MainActivity.this, Enigma2Activity.class);
                        }
                        startActivity(intentG);
                    })
                    .show();
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Estás seguro de que quieres salir?");

                // Botón "Sí"
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.CENTRO);
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

        transparencia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("VAMOS PANTALLA ENIGMA 1 SIN FLECHA");
                imgFondo.setVisibility(View.GONE);
                transparencia.setVisibility(View.GONE);
                imgFondoenigma.setVisibility(View.VISIBLE);
                btnRespuesta.setVisibility(View.VISIBLE);
                etRespuesta.setVisibility(View.VISIBLE);
                volverIntro.setVisibility(View.VISIBLE);
            }
        });

        volverIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("VOLVEMOS PANTALLA INICIO");
                imgFondo.setVisibility(View.VISIBLE);
                transparencia.setVisibility(View.VISIBLE);
                imgFondoenigma.setVisibility(View.GONE);
                btnRespuesta.setVisibility(View.GONE);
                etRespuesta.setVisibility(View.GONE);
                volverIntro.setVisibility(View.GONE);
            }
        });

        btnRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Deshabilitar el botón para evitar múltiples clics
                btnRespuesta.setEnabled(false);

                if (etRespuesta.getText().toString().equals("A")) {
                    System.out.println("RESPONDEMOS CORRECTAMENTE Y VAMOS PANTALLA ENIGMA 1 CON FLECHA");

                    // TODO: Sacar dialog de respuesta correcta
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    // Inflar el diseño personalizado del diálogo
                    View dialogView = getLayoutInflater().inflate(R.layout.respuestas, null);
                    builder.setView(dialogView);

                    // Configurar el TextView del diseño inflado
                    TextView messageView = dialogView.findViewById(R.id.tvPistas);
                    AlertDialog dialog = builder.create();

                    Button btnEntendido = dialogView.findViewById(R.id.btnEntendido);
                    btnEntendido.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    // añade una imagen al dialogo
                    ImageView image = dialogView.findViewById(R.id.imageView);
                    image.setImageResource(R.drawable.verificado);

                    // Cargar la tipografía personalizada
                    Typeface customFont = ResourcesCompat.getFont(MainActivity.this, R.font.julee_regular);
                    messageView.setTypeface(customFont);
                    messageView.setText("¡Enhorabuena! La respuesta es correcta"); // Texto del mensaje

                    // Configurar tamaño del diálogo según el diseño inflado
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            dialog.getWindow().setLayout(1400, 800); // Ajustar a las dimensiones exactas de la imagen de fondo
                        }
                    });

                    dialog.show();



                    if (!mp2.isPlaying()) {
                        mp2.start();
                        mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                btnRespuesta.setEnabled(true); // Rehabilitar el botón cuando termine
                            }
                        });
                    }


                    goBoton.setVisibility(View.VISIBLE);
                    volverIntro.setVisibility(View.GONE);
                    volverIntroCorrecta.setVisibility(View.VISIBLE);


                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    // Inflar el diseño personalizado del diálogo
                    View dialogView = getLayoutInflater().inflate(R.layout.respuestas, null);
                    builder.setView(dialogView);

                    // Configurar el TextView del diseño inflado
                    TextView messageView = dialogView.findViewById(R.id.tvPistas);
                    AlertDialog dialog = builder.create();

                    //boton entendido
                    Button btnEntendido = dialogView.findViewById(R.id.btnEntendido);
                    btnEntendido.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    // añade una imagen al dialogo
                    ImageView image = dialogView.findViewById(R.id.imageView);
                    image.setImageResource(R.drawable.cancelar);

                    // Cargar la tipografía personalizada
                    Typeface customFont = ResourcesCompat.getFont(MainActivity.this, R.font.julee_regular);
                    messageView.setTypeface(customFont);
                    messageView.setText("¡Vaya! Parece que la respuesta es incorrecta"); // Texto del mensaje

                    // Configurar tamaño del diálogo según el diseño inflado
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            dialog.getWindow().setLayout(1400, 800); // Ajustar a las dimensiones exactas de la imagen de fondo
                        }
                    });

                    dialog.show();



                    if (!mp1.isPlaying()) {
                        mp1.start();
                        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                btnRespuesta.setEnabled(true); // Rehabilitar el botón cuando termine
                            }
                        });
                    }

                }

                etRespuesta.setText("");
            }
        });


        volverIntroCorrecta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("VOLVEMOS PANTALLA INICIO");
                goBoton.setVisibility(View.GONE);
                etRespuesta.setVisibility(View.GONE);
                btnRespuesta.setVisibility(View.GONE);
                imgFondo.setVisibility(View.VISIBLE);
                volverIntroCorrecta.setVisibility(View.GONE);
                goEnigma.setVisibility(View.VISIBLE);
                imgFondoenigma.setVisibility(View.GONE);
            }
        });

        goEnigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("VAMOS PANTALLA ENIGMA 1 CON FLECHA");
                imgFondo.setVisibility(View.GONE);
                goEnigma.setVisibility(View.GONE);
                btnRespuesta.setVisibility(View.VISIBLE);
                etRespuesta.setVisibility(View.VISIBLE);
                volverIntroCorrecta.setVisibility(View.VISIBLE);
                imgFondoenigma.setVisibility(View.VISIBLE);
                goBoton.setVisibility(View.VISIBLE);
            }
        });

        goBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Global.ENIGMA1 == true){
                    if (intentgo.equals("Enigma2Activity")) {
                        intentG = new Intent(MainActivity.this, Enigma2Activity.class);
                    } else if(intentgo.equals("Enigma3Activity")){
                        intentG = new Intent(MainActivity.this, Enigma3Activity.class);
                    } else if(intentgo.equals("EnigmaNombre")){
                        intentG = new Intent(MainActivity.this, EnigmaNombre.class);
                    } else if(intentgo.equals("EnigmaSopa")){
                        intentG = new Intent(MainActivity.this, EnigmaSopa.class);
                    } else {
                        intentG = new Intent(MainActivity.this, Enigma2Activity.class);
                    }

                    startActivity(intentG);
                } else{
                    System.out.println("VAMOS PANTALLA DESPERTAR");
                    volverIntro.setVisibility(View.GONE);
                    volverIntroCorrecta.setVisibility(View.GONE);
                    goBoton.setVisibility(View.GONE);
                    boton.setVisibility(View.VISIBLE);
                    imageViewBoton.setVisibility(View.VISIBLE);
                    etRespuesta.setVisibility(View.GONE);
                    volverEnigma.setVisibility(View.VISIBLE);
                    btnRespuesta.setVisibility(View.GONE);
                    imgFondoenigma.setVisibility(View.GONE);
                }



            }
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("DESPERTAMOS");
                imgFondo.setVisibility(View.GONE);
                imgFondoenigma.setVisibility(View.GONE);
                boton.setVisibility(View.GONE);
                imageViewBoton.setVisibility(View.GONE);
                volverEnigma.setVisibility(View.GONE);
                exit.setVisibility(View.GONE);
                fondo.setVisibility(View.GONE);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        despertar();
                    }
                }, 200);
            }
        });

        volverEnigma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("VOLVEMOS PANTALLA ENIGMA 1 CON FLECHA");
                volverEnigma.setVisibility(View.GONE);
                goBoton.setVisibility(View.VISIBLE);
                volverIntroCorrecta.setVisibility(View.VISIBLE);
                boton.setVisibility(View.GONE);
                imageViewBoton.setVisibility(View.GONE);
                etRespuesta.setVisibility(View.VISIBLE);
                btnRespuesta.setVisibility(View.VISIBLE);
                imgFondoenigma.setVisibility(View.VISIBLE);
            }
        });

    }



    public void despertar() {
        speechControl.setVelocidadHabla(35);
        speechControl.setEntonacionHabla(50);

        // Levantar cabeza

        headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.ARRIBA);

        detenerCicloSleep();


        //Mover a la izquierda
        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.IZQUIERDA);

        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.DERECHA);


        //Mover a la derecha
        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.DERECHA);
        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.IZQUIERDA);


        //Girar sobre si mismo
        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.GIRAR);


        // Expresión perdido
        systemControl.cambiarEmocion(EmotionsType.NORMAL);

        // Mover cabeza
        AbsoluteAngleHeadMotion absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,10);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

        speechControl.hablar("Vaya, parece que me he quedado dormida...");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,110);
        headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechControl.hablar("No sé donde estoy...");


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.CENTRO);

        speechControl.hablar("Todo esto es muy raro..., no recuerdo nada... ");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        systemControl.cambiarEmocion(EmotionsType.FAINT);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechControl.hablar("Me siento muy confusa...");


        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        systemControl.cambiarEmocion(EmotionsType.NORMAL);

        AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 5, 30);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        speechControl.hablar("Perdón si os he asustado, me llamo Lola.");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        absoluteAngleHandMotion = new AbsoluteAngleHandMotion(AbsoluteAngleHandMotion.PART_RIGHT, 5, 170);
        handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Expresión feliz
        systemControl.cambiarEmocion(EmotionsType.SMILE);

        speechControl.hablar("¡Mi creador me comentó que conocería nuevos amigos! ¡Estoy segura de que sois vosotros! Tengo que descubrir el nombre de mi creador y no puedo hacerlo sin vuestra ayuda.");

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Me gustaría pasar la variable global de la respuesta a la siguiente pantalla

            Intent intentej = new Intent(MainActivity.this, IntroSanbotActivity.class);
            // Pasar el intent a string para poder pasarlo a la siguiente pantalla

            intentej.putExtra("intentgo", intentgo);
            startActivity(intentej);




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
