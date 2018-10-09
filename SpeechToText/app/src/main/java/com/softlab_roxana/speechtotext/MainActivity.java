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
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    static final int REQUEST_PERMISSION_KEY = 1;
    private int firstTime = 0;
    private int firstWordInASentence = 0;
    private int counterIterationsPaused = 0;
    private long tIni = 0;
    private long tFin = 0;
    private long timeTotal =0;
    private long seg = 0;
    private int min = 0;
    private int hora = 0;
    private float rmsValue = 0;
    private float rmsValuePrev;
    private Boolean pause = true;
    private String text = "";
    File file = createFile ();

    private static final long START_TIME_IN_MILLIS = 4000; //timeout to restart
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------coloca el volumen en 0---------------
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager == null) return;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        //-----------------------------------------------------

        returnedText = findViewById(R.id.txtSpeechInput);
        returnedText.setMovementMethod(new ScrollingMovementMethod());

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
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es_ES");//Para el español
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);

        recordbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                if (pause == true) {
                    tIni = System.currentTimeMillis();
                    speech.startListening(recognizerIntent);
                    pause = false;
                    recordbtn.setImageResource(R.drawable.ic_microphone_3);
                    pauseTimer(mCountDownTimer, mTimerRunning);
                    resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);
                    Log.d("Log", "START: " + pause);
                }
                else{
                    pauseTimer(mCountDownTimer, mTimerRunning);
                    resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);
                    recordbtn.setImageResource(R.drawable.ic_microphone_2);
                    pause = true;
                    Log.d("Log", "STOP: " + pause);
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
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("Log", "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("Log", "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);//nuevo..
        Log.d("Log", "FAILED " + errorMessage);

        if(errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || errorCode == SpeechRecognizer.ERROR_NO_MATCH || errorCode == SpeechRecognizer.ERROR_NETWORK){
            firstTime=1;//probar si debo quitar esto
            speech.startListening(recognizerIntent);
        }

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

        counterIterationsPaused = 0;
        String texto = matches.get(0);

        //------------------Fixing double writing error in long texts---------------------
    if (text.length() > 100 & texto.length()<20){
        firstWordInASentence=0;//para calcular el nuevo valor de tiempo de entrada

        //-------------------------TIME----------------------------
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

        //-----------------------------------------------------------
    }
        //----------------------------------------------------------------------------------



        //-----------GENERATE THE FILE---------------------------
        if (firstTime==0){
            write(file);

            firstTime=1;
        }
        //--------------------------------------------------------



        if (firstWordInASentence == 0) {// primera palabra reconocida

            //times=null;//limpiar tiempo // borrar
            tFin = System.currentTimeMillis() - 1000;//recovery time
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
            firstWordInASentence = 1;
        }


        //-----------  TIEMPO FINAL -------------------
        tFin = System.currentTimeMillis() - 1000;// ver si cambio solo este dato y no el anterior, quizas 500
        //---------------------------------------------


        text = matches.get(0);
        returnedText.setText(text);
        startTimer();
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.d("Log", "onReadyForSpeech" + arg0);
    }

    @Override
    public void onResults(Bundle results) {
        firstWordInASentence = 0;
        Log.d("Log", "onResults");
        if (text != "") {
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
        }
        text="";
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onRmsChanged(final float rmsdB) {
        pauseTimer(mCountDownTimer, mTimerRunning);
        resetTimer(mTimeLeftInMillis,START_TIME_IN_MILLIS);
        startTimer();
        if (rmsValue == 0){
            rmsValuePrev=rmsdB;
            if (pause == false) {

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

        rmsValue=rmsdB;

        if (rmsValue == rmsValuePrev ){
            counterIterationsPaused++;
        }
        else{
            rmsValue = 0;
            if (pause == false) {
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
        Log.d("log","band2: " + counterIterationsPaused);
        if (counterIterationsPaused >= 80){// Interations while nothing is heard, this can change.
            Log.d("log","pass 80");
            rmsValue=0;
            rmsValuePrev=0;
            counterIterationsPaused=0;
            speech.stopListening();
        }
         Log.d("Log", "onRmsChanged: " + rmsdB);
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
                firstWordInASentence = 0;

                if (text != "") {//

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
                }
                text="";
                speech.startListening(recognizerIntent);
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

