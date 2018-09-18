package com.softlab_roxana.speechtotext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.io.IOException;

public class Config {
    FileReader fr;
    BufferedReader br;
    ArrayList<String> section = new ArrayList<String>();
    ArrayList<String> finalSubtitle = new ArrayList<String>();
    //ArrayList<String> finalSubtitleOriginal = new ArrayList<String>();
    int subText = 0;

    public ArrayList<String> readFile (String dir){

        String line = "";

        try {

            File file = new File (dir);

            fr = new FileReader(file);
            br = new BufferedReader (fr);
            String line2;

            while((line = br.readLine())!=null) {

                line2 = remove(line);
                //System.out.println(line);
                if(subText == 0) {
                    //finalSubtitle.add(line2);
                    //finalSubtitleOriginal.add(line);
                    subText=1;
                }else if (subText == 1){
                    finalSubtitle.add(line2);
                    //finalSubtitleOriginal.add(line);
                    //finalSubtitle.add(line2);
                    //finalSubtitleOriginal.add("");
                    subText=2;
                }else if(subText == 2){
                    finalSubtitle.add(line2);
                    subText=3;
                }else if(subText == 3){
                    finalSubtitle.add(line2);
                    subText=4;
                }else if(subText == 4){
                    finalSubtitle.add(line2);
                    subText=1;
                }
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalSubtitle;
    }


    public static String remove(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }//remove1
}
