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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
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
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    static final int REQUEST_PERMISSION_KEY = 1;
    private int b = 0;
    private int band = 0;
    private int band2 = 0;
    private Boolean startPause = true;
    private String[] words;
    private String[] words2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        returnedText = (TextView) findViewById(R.id.txtSpeechInput);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        recordbtn = (ImageButton) findViewById(R.id.SpeakButton);

        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }

        //progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        /*
        Minimum time to listen in millis. Here 300 seconds
         */
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 300000);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);

        recordbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                //progressBar.setVisibility(View.VISIBLE);
                if (startPause == true) {
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
        Log.d("Log", "onEndOfSpeech");
        //progressBar.setVisibility(View.INVISIBLE);
        //recordbtn.setEnabled(true);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d("Log", "FAILED " + errorMessage);
        //recordbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone_2));

        b=1;
        //speech.stopListening();
        //speech.cancel();

       // speech.startListening(recognizerIntent);


        if(errorCode == SpeechRecognizer.ERROR_NO_MATCH){
            b=1;
            speech.startListening(recognizerIntent);
            //startPause = false;
        }
        if(errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT){
            b=1;
            speech.startListening(recognizerIntent);
            //startPause = false;
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
        Log.d("Log", "onPartialResults" + startPause);

        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        band2 = 0;
        File file = createFile ();
        String texto = matches.get(0);
        words = texto.split("\\s+");

        Log.d("Log", "valoooooooor de bandera " + band);
        if (b==0){
            write(file);
            words2 = texto.split("\\s+");
            b=1;
        }

        if (band == 1){
            //writeToSDFile(texto + " ",file);
            for (int i=0; i < words2.length; ++i){
                writeToSDFile(words2[i] + " ",file);
            }
        }
        band = 0;

        words2=words;

        Log.d("Log", "valoooooooor de bandera " + band);


        /*Log.d("Log", "texto : " + matches.get(0) + " " +  words[0] + " " + words.length + " " + words2[0] + " " + words2.length);*/
        Log.d("Log", "texto : " + matches.get(0) + " " +  words[0] + " " + words.length);

        /*if ((words[0].equals(words2[0])) & (words.length>=words2.length)){
            words2 = words;
            Log.d("Log", "Entró en el de iguales");
        }
        else{
            if (words.length < 5) {

            for (int i=0; i < words2.length; ++i){
                writeToSDFile(words2[i] + " ",file);
            }
                Log.d("Log", "Entró en el de diferentes");
            }
            words2 = words;
        }*/



        //(se debe quitar lo que viene despues si se descomenta esto)
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
        band=1;
        speech.startListening(recognizerIntent);//prueba
        Log.d("Log", "onResults");

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        if (rmsdB == 10.0){
            band2++;
        }
        if (band2 == 70){
            speech.startListening(recognizerIntent);
            band2=0;
        }
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

            //FileOutputStream f = new FileOutputStream(file);
            //f.write(speechToTextData.getBytes());
            //PrintWriter pw = new PrintWriter(f);
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(f));

            //bw.write(speechToTextData);
            //pw.flush();
            //pw.close();
            //f.flush();
            //f.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "plain/text");
            startActivity(intent);
        } catch(Exception ex) {
            Log.e("tag", "No file browser installed. " + ex.getMessage());
        }*/
    }
}
