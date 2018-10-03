package com.softlab_roxana.speechtotext;

import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static com.softlab_roxana.speechtotext.Function.resetTimer;
import static com.softlab_roxana.speechtotext.Function.pauseTimer;
import static com.softlab_roxana.speechtotext.Function.createFile;
import static com.softlab_roxana.speechtotext.Function.write;
import static com.softlab_roxana.speechtotext.Function.writeToSDFile;

public class MainActivity extends AppCompatActivity implements RecognitionListener
{

    private TextView returnedText;
    ImageButton recordbtn;
    ImageView lv1;
    ImageView lv2;
    ImageView lv3;
    ImageView lv4;
    ImageView lv5;
    ImageView lv6;
    ImageView lv7;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    static final int REQUEST_PERMISSION_KEY = 1;
    private int b = 0;
    private int band = 0;
    private int band2 = 0;
    private int band3 = 0;
    private long tIni = 0;
    private long tFin = 0;
    private long timeTotal =0;
    private long seg = 0;
    private int min = 0;
    private int hora = 0;
    private float cont = 0;
    private float cont2 = 0;
    private float contPrev;
    private Boolean startPause = true;
    private Boolean same = false;
    //private ArrayList<String> times = new ArrayList<>();//borrar
    private String text = "";
    File file = createFile ();



    private static final long START_TIME_IN_MILLIS = 4000; //AQUI
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //adrian dice que coloca el volumen en 0--------------
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager == null) return;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        //-----------------------------------------------------

        returnedText = findViewById(R.id.txtSpeechInput);
        returnedText.setMovementMethod(new ScrollingMovementMethod());


        //progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        recordbtn = findViewById(R.id.SpeakButton);
        lv1=findViewById(R.id.lv1);
        lv2=findViewById(R.id.lv2);
        lv3=findViewById(R.id.lv3);
        lv4=findViewById(R.id.lv4);
        lv5=findViewById(R.id.lv5);
        lv6=findViewById(R.id.lv6);
        lv7=findViewById(R.id.lv7);

        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }

        //progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");//Para el ingles
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es_ES");//Para el español
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());//toma el lenguaje del teléfono
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        /*
        Minimum time to listen in millis. Here 300 seconds
         */
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 200000);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,20000);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,10000);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);

        recordbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                //progressBar.setVisibility(View.VISIBLE);
                if (startPause == true) {
                    tIni = System.currentTimeMillis();
                    speech.startListening(recognizerIntent);
                    startPause = false;
                    recordbtn.setImageResource(R.drawable.ic_microphone_3);
                    pauseTimer(mCountDownTimer, mTimerRunning);
                    resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);
                    Log.d("Log", "START: " + startPause);
                }
                else{
                    pauseTimer(mCountDownTimer, mTimerRunning);
                    resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);
                    recordbtn.setImageResource(R.drawable.ic_microphone_2);
                    startPause = true;
                    Log.d("Log", "STOP: " + startPause);
                    speech.stopListening();
                    speech.cancel();
                    lv1.setImageResource(R.drawable.blanco);
                    lv2.setImageResource(R.drawable.blanco);
                    lv3.setImageResource(R.drawable.blanco);
                    lv4.setImageResource(R.drawable.blanco);
                    lv5.setImageResource(R.drawable.blanco);
                    lv6.setImageResource(R.drawable.blanco);
                    lv7.setImageResource(R.drawable.blanco);
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("Log", "resume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.stopListening();
            speech.cancel();
            speech.destroy();

            //speech.startListening(recognizerIntent);
            Log.d("Log", "reconociendo de nuevo");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speech != null) {
            speech.stopListening();
            speech.cancel();
            speech.destroy();
            Log.d("Log", "destroy");
        }
    }

    @Override
    public void onBeginningOfSpeech() {

        Log.d("Log", "onBeginningOfSpeech");
        //progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("Log", "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        //speech.startListening(recognizerIntent);
        Log.d("Log", "onEndOfSpeech");
        //progressBar.setVisibility(View.INVISIBLE);
        //recordbtn.setEnabled(true);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);//nuevo..
        Log.d("Log", "FAILED " + errorMessage);

        if(errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || errorCode == SpeechRecognizer.ERROR_NO_MATCH || errorCode == SpeechRecognizer.ERROR_NETWORK){
            b=1;
            //speech.stopListening();
            //speech.cancel();
            speech.startListening(recognizerIntent);
        }
        /*else {//por los momentos quitaremos que se impriman los errores
            //startPause = true;
            //progressBar.setVisibility(View.INVISIBLE);
            returnedText.setText(errorMessage);
            //recordbtn.setEnabled(true);
        }*/
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.d("Log", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        pauseTimer(mCountDownTimer, mTimerRunning);
        resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);

        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        band2 = 0;
        String texto = matches.get(0);

        //Log.d("Log", "valoooooooor de bandera " + band);
    if (text.length() > 100 & texto.length()<20){
        band=0;//para calcular el nuevo valor de tiempo de entrada
        same = true;

        //---------------PRUEBA------------------
        timeTotal = tFin - tIni;
        seg = timeTotal / 1000;

        if (seg < 60) {
            min = 0;
        } else {
            min = (int) (seg / 60);
            seg = seg - (min * 60);
        }

        if (min < 60) {
            hora = 0;

            writeToSDFile(text + " ",file);
            writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n', file);
        } else {
            hora = (int) (min / 60);
            min = min - (hora * 60);

            writeToSDFile(text + " ",file);
            writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n', file);
        }

        //---------------------PRUEBA-------------------------------

    }
        //Genera el archivo
        if (b==0){
            write(file);
            //words2 = texto.split("\\s+");

            b=1;
        }

        if (band == 0) {// primera palabra reconocida

            //times=null;//limpiar tiempo // borrar
            tFin = System.currentTimeMillis() - 1000;//ver si debo cambiar los 1000
            timeTotal = tFin - tIni;
            seg = timeTotal/1000;

            if (seg<60){
                min = 0;
            }
            else{
                min = (int) (seg/60);
                seg = seg - (min*60);
            }

            if (min<60){
                hora = 0;

            }
            else{
                hora = (int) (min/60);
                min = min - (hora*60);
            }
            writeToSDFile(" S" + hora + ":" + min + ":" + seg + " ",file);
        band = 1;
        }
        //-----------  TIEMPO FINAL -------------------
        tFin = System.currentTimeMillis() - 1000;// ver si cambio solo este dato y no el anterior, quizas 500
        //-----------  TIEMPO FINAL -------------------


        //Log.d("Log", "valoooooooor de bandera " + band);
        //Log.d("Log", "texto : " + matches.get(0) + " " +  words[0] + " " + words.length);


        /*for (String result : matches)
        {
            text += result + "\n";
        }*/

        text = matches.get(0); //  Remove this line while uncommenting above    codes
        returnedText.setText(text);
        startTimer();
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {

        Log.d("Log", "onReadyForSpeech" + arg0);
    }

    @Override
    public void onResults(Bundle results) {
        band = 0;
        Log.d("Log", "onResults");
        if (text != "") {//words2
            //writeToSDFile(" S" + hora + ":" + min + ":" + seg + " ",file);
            //tFin = System.currentTimeMillis()-5000;
            timeTotal = tFin - tIni;
            seg = timeTotal / 1000;

            if (seg < 60) {
                min = 0;
            } else {
                min = (int) (seg / 60);
                seg = seg - (min * 60);
            }

            if (min < 60) {
                hora = 0;
                writeToSDFile(text + " ",file);
                /*for (int i = 0; i < words2.length; ++i) {
                    writeToSDFile(words2[i] + " ", file);
                }*/
                writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n', file);
            } else {
                hora = (int) (min / 60);
                min = min - (hora * 60);

                writeToSDFile(text + " ",file);
                /*for (int i = 0; i < words2.length; ++i) {
                    writeToSDFile(words2[i] + " ", file);
                }*/
                writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n', file);
            }
        }
        text="";
        speech.startListening(recognizerIntent);//prueba
    }

    @Override
    public void onRmsChanged(final float rmsdB) {
        pauseTimer(mCountDownTimer, mTimerRunning);
        resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);
        startTimer();
        if (cont == 0){
            contPrev=rmsdB;
            if (startPause == false) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//
                        if (rmsdB < -2) {

                            lv1.setImageResource(R.drawable.blanco);
                            lv2.setImageResource(R.drawable.blanco);
                            lv3.setImageResource(R.drawable.blanco);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= -2 & rmsdB < 0) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.blanco);
                            lv3.setImageResource(R.drawable.blanco);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 0 & rmsdB < 2) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.blanco);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 2 & rmsdB < 4) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 4 & rmsdB < 6) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 6 & rmsdB < 8) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.naranja);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 8 & rmsdB < 9) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.naranja);
                            lv6.setImageResource(R.drawable.naranja_rojo);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 9) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.naranja);
                            lv6.setImageResource(R.drawable.naranja_rojo);
                            lv7.setImageResource(R.drawable.rojo);
                        }
                    }
                });
            }
        }

        cont=rmsdB;

        if (cont == contPrev ){
            band2++;
        }
        else{
            /*pauseTimer(mCountDownTimer, mTimerRunning);
            resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);
            same = false;*/
            cont = 0;
            //band2=0;
            if (startPause == false) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//

                        if (rmsdB < -2) {
                            lv1.setImageResource(R.drawable.blanco);
                            lv2.setImageResource(R.drawable.blanco);
                            lv3.setImageResource(R.drawable.blanco);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }
                        if (rmsdB >= -2 & rmsdB < 0) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.blanco);
                            lv3.setImageResource(R.drawable.blanco);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 0 & rmsdB < 2) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.blanco);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 2 & rmsdB < 4) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.blanco);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 4 & rmsdB < 6) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.blanco);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 6 & rmsdB < 8) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.naranja);
                            lv6.setImageResource(R.drawable.blanco);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 8 & rmsdB < 9) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.naranja);
                            lv6.setImageResource(R.drawable.naranja_rojo);
                            lv7.setImageResource(R.drawable.blanco);
                        }

                        if (rmsdB >= 9) {
                            lv1.setImageResource(R.drawable.azul);
                            lv2.setImageResource(R.drawable.azul_verde);
                            lv3.setImageResource(R.drawable.verde);
                            lv4.setImageResource(R.drawable.amarillo_naranja);
                            lv5.setImageResource(R.drawable.naranja);
                            lv6.setImageResource(R.drawable.naranja_rojo);
                            lv7.setImageResource(R.drawable.rojo);
                        }
                    }
                });
            }
        }
        Log.d("log","band2: " + band2);
        if (band2 >= 80){//revisar esto 70-50 - tiempo sin escuchar una palabra
            Log.d("log","pass 80");
            //speech.cancel();
            //speech.startListening(recognizerIntent);
            //if (band2 == 110){
            //    Log.d("log","pass 100");
            //    speech.stopListening();
            //    speech.cancel();
            //    speech.startListening(recognizerIntent);
            /*band3=1;
            cont2 = cont;*/
            cont=0;
            contPrev=0;
            band2=0;

            speech.stopListening();
            /*speech.cancel();
            speech.startListening(recognizerIntent);*/

        }

        //Log.d("Log", "onRmsChanged: " + rmsdB + " Cont: " + cont + " ContPrev: " + contPrev);
        Log.d("Log", "onRmsChanged: " + rmsdB);
        //progressBar.setProgress((int) rmsdB);

    }

    //-----------TIEMPO------------------
    private void startTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
            }
            @Override
            public void onFinish() {
                speech.stopListening();
                speech.cancel();
                band = 0;

                if (text != "") {//words2
                   // writeToSDFile(" S" + hora + ":" + min + ":" + seg + " ",file);
                    //tFin = System.currentTimeMillis() - 10000;//Ver si debo cambiar este 10000
                    timeTotal = tFin - tIni;
                    seg = timeTotal / 1000;

                    if (seg < 60) {
                        min = 0;
                    } else {
                        min = (int) (seg / 60);
                        seg = seg - (min * 60);
                    }

                    if (min < 60) {
                        hora = 0;

                        writeToSDFile(text + " ",file);
                        /*for (int i = 0; i < words2.length; ++i) {
                            writeToSDFile(words2[i] + " ", file);
                        }*/
                        writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n', file);
                    } else {
                        hora = (int) (min / 60);
                        min = min - (hora * 60);

                        writeToSDFile(text + " ",file);
                       /* for (int i = 0; i < words2.length; ++i) {
                            writeToSDFile(words2[i] + " ", file);
                        }*/
                        writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n', file);
                    }
                }
                text="";
                speech.startListening(recognizerIntent);
                //onRmsChanged(4);
                mTimerRunning = false;
            }
        }.start();
        mTimerRunning = true;
        Log.d("Log", "timer: " + mTimeLeftInMillis);
    }
    //-----------TIEMPO------------------


    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

}
class Function {

    public static  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public static File createFile (){
        File root = android.os.Environment.getExternalStorageDirectory();

        File dir = new File (root.getAbsolutePath() + "/folder");

        if (dir.exists()){
            dir.delete();
        }
        dir.mkdirs();

        File file = new File(dir, "text.txt");

        if (dir.exists()){
            file.delete();
        }

        return file;
    }

    public static void write (File file) {
        try {
            FileOutputStream f = new FileOutputStream(file);
            f.write("".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToSDFile(String speechToTextData, File file){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(speechToTextData);
            bw.flush();
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-----------TIEMPO------------------
    public static void pauseTimer(CountDownTimer mCountDownTimer, boolean mTimerRunning) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mTimerRunning = false;
        }
    }

    public static void resetTimer(long mTimeLeftInMillis, long START_TIME_IN_MILLIS) {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
    }
    //----------TIEMPO------------------

}

