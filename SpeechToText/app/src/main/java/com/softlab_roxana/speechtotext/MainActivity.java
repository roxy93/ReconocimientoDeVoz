package com.softlab_roxana.speechtotext;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

import static com.softlab_roxana.speechtotext.Function.createFile;
import static com.softlab_roxana.speechtotext.Function.write;
import static com.softlab_roxana.speechtotext.Function.writeToSDFile;

public class MainActivity extends AppCompatActivity implements RecognitionListener{

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
    private long tIni = 0;
    private long tFin = 0;
    private long timeTotal =0;
    private long seg = 0;
    private int min = 0;
    private int hora = 0;
    private float cont = 0;
    private float contPrev;
    private Boolean startPause = true;
    private String[] words;
    private String[] words2;
    File file = createFile ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        returnedText = findViewById(R.id.txtSpeechInput);
        returnedText.setMovementMethod(new ScrollingMovementMethod());//no sirve

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
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 200000);
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
                    Log.d("Log", "START: " + startPause);
                }
                else{
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
        speech.startListening(recognizerIntent);
        Log.d("Log", "onEndOfSpeech");
        //progressBar.setVisibility(View.INVISIBLE);
        //recordbtn.setEnabled(true);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);//nuevo..
        Log.d("Log", "FAILED " + errorMessage);
        //recordbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone_2));

        //b=1;
       // speech.stopListening();
        //speech.cancel();

        speech.startListening(recognizerIntent);

        /*if (errorCode == SpeechRecognizer.ERROR_RECOGNIZER_BUSY){
            b=1;
            speech.stopListening();
           // speech.cancel();
            speech.startListening(recognizerIntent);
        }

        if(errorCode == SpeechRecognizer.ERROR_NETWORK){
            b=1;
            speech.startListening(recognizerIntent);
        }
        if(errorCode == SpeechRecognizer.ERROR_NO_MATCH){
            b=1;
            speech.startListening(recognizerIntent);
        }
        if(errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT){
            b=1;
            speech.stopListening();
            //speech.cancel();
            speech.startListening(recognizerIntent);
        }*/




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
        Log.d("Log", "onPartialResults" + startPause);

        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        band2 = 0;
        //File file = createFile ();
        String texto = matches.get(0);
        words = texto.split("\\s+");

        Log.d("Log", "valoooooooor de bandera " + band);

        //Genera el archivo
        if (b==0){
            write(file);
            words2 = texto.split("\\s+");

            b=1;
        }

        if (band == 0) {// primera palabra reconocida

            tFin = System.currentTimeMillis();
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
                writeToSDFile(" S" + hora + ":" + min + ":" + seg + " ",file);
            }
            else{
                hora = (int) (min/60);
                min = min - (hora*60);
                writeToSDFile(" S" + hora + ":" + min + ":" + seg + " ",file);
            }

        band = 1;
        }

        words2=words;

        Log.d("Log", "valoooooooor de bandera " + band);
        Log.d("Log", "texto : " + matches.get(0) + " " +  words[0] + " " + words.length);


        /*for (String result : matches)
        {
            text += result + "\n";
        }*/

        text = matches.get(0); //  Remove this line while uncommenting above    codes


        returnedText.setText(text);

    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.d("Log", "onReadyForSpeech" + arg0);
    }

    @Override
    public void onResults(Bundle results) {
        band=0;
        tFin = System.currentTimeMillis();
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
            for (int i=0; i < words2.length; ++i){
                writeToSDFile(words2[i] + " " ,file);
            }
            writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n',file);
        }
        else{
            hora = (int) (min/60);
            min = min - (hora*60);
            for (int i=0; i < words2.length; ++i){
                writeToSDFile(words2[i] + " " ,file);
            }
            writeToSDFile(" F" + hora + ":" + min + ":" + seg + '\n',file);
        }

        speech.startListening(recognizerIntent);//prueba
        //b = 1;
        Log.d("Log", "onResults");

    }

    @Override
    public void onRmsChanged(float rmsdB) {

        if (cont == 0){
            contPrev=rmsdB;
            if (startPause == false) {

                if (rmsdB < -2){
                lv1.setImageResource(R.drawable.blanco);
                lv2.setImageResource(R.drawable.blanco);
                lv3.setImageResource(R.drawable.blanco);
                lv4.setImageResource(R.drawable.blanco);
                lv5.setImageResource(R.drawable.blanco);
                lv6.setImageResource(R.drawable.blanco);
                lv7.setImageResource(R.drawable.blanco);
                }

                if (rmsdB >= -2 & rmsdB < 0){
                lv1.setImageResource(R.drawable.azul);
                lv2.setImageResource(R.drawable.blanco);
                lv3.setImageResource(R.drawable.blanco);
                lv4.setImageResource(R.drawable.blanco);
                lv5.setImageResource(R.drawable.blanco);
                lv6.setImageResource(R.drawable.blanco);
                lv7.setImageResource(R.drawable.blanco);
                }

                if (rmsdB >= 0 & rmsdB < 2){
                lv1.setImageResource(R.drawable.azul);
                lv2.setImageResource(R.drawable.azul_verde);
                lv3.setImageResource(R.drawable.blanco);
                lv4.setImageResource(R.drawable.blanco);
                lv5.setImageResource(R.drawable.blanco);
                lv6.setImageResource(R.drawable.blanco);
                lv7.setImageResource(R.drawable.blanco);
                }

                if (rmsdB >= 2 & rmsdB < 4){
                lv1.setImageResource(R.drawable.azul);
                lv2.setImageResource(R.drawable.azul_verde);
                lv3.setImageResource(R.drawable.verde);
                lv4.setImageResource(R.drawable.blanco);
                lv5.setImageResource(R.drawable.blanco);
                lv6.setImageResource(R.drawable.blanco);
                lv7.setImageResource(R.drawable.blanco);
                }

                if (rmsdB >= 4 & rmsdB < 6){
                lv1.setImageResource(R.drawable.azul);
                lv2.setImageResource(R.drawable.azul_verde);
                lv3.setImageResource(R.drawable.verde);
                lv4.setImageResource(R.drawable.amarillo_naranja);
                lv5.setImageResource(R.drawable.blanco);
                lv6.setImageResource(R.drawable.blanco);
                lv7.setImageResource(R.drawable.blanco);
                }

                if (rmsdB >= 6 & rmsdB < 8){
                lv1.setImageResource(R.drawable.azul);
                lv2.setImageResource(R.drawable.azul_verde);
                lv3.setImageResource(R.drawable.verde);
                lv4.setImageResource(R.drawable.amarillo_naranja);
                lv5.setImageResource(R.drawable.naranja);
                lv6.setImageResource(R.drawable.blanco);
                lv7.setImageResource(R.drawable.blanco);
                }

                if (rmsdB >= 8 & rmsdB < 9){
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
        }

        cont=rmsdB;

        if (cont == contPrev){
            band2++;
        }
        else{
            cont = 0;
            if (startPause == false) {
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
        }

        if (band2 == 110){//revisar esto 70-50 - tiempo sin escuchar una palabra
            speech.stopListening();
            speech.cancel();
            speech.startListening(recognizerIntent);
            band2=0;
            cont=0;
            contPrev=0;
            /*try {
                Thread.sleep(1000);
                speech.startListening(recognizerIntent);
            }catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }*/

        }

        //Log.d("Log", "onRmsChanged: " + rmsdB + " Cont: " + cont + " ContPrev: " + contPrev);
        Log.d("Log", "onRmsChanged: " + rmsdB);
        //progressBar.setProgress((int) rmsdB);

    }

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
        dir.mkdirs();
        File file = new File(dir, "text.txt");
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
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
