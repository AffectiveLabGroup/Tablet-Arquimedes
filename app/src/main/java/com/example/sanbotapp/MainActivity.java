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
import android.speech.tts.TextToSpeech;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

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

    private TextToSpeech tts;

    private Runnable sleepRunnable;
    private Handler handlerSleep = new Handler();

    private AudioManager audioManager;
    MediaPlayer mp1;
    MediaPlayer mp2;

    private boolean siguienteAccion = false;

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
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

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
            }
        }, 100);

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


    private void hablar(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    public void despertar() {
        //Abrir ojos

        hablar("Vaya, parece que me he quedado dormida...");

        hablar("No sé donde estoy...");

        hablar("Todo esto es muy raro..., no recuerdo nada... ");

        hablar("Me siento muy confusa...");

        hablar("Perdón si os he asustado, me llamo Lola.");

        hablar("¡Mi creador me comentó que conocería nuevos amigos! ¡Estoy segura de que sois vosotros! Tengo que descubrir el nombre de mi creador y no puedo hacerlo sin vuestra ayuda.");

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

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


}
