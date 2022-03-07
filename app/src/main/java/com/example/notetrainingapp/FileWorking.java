package com.example.notetrainingapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

/*STRUCTURE OF DATA
* KEY-MUSCLES_GROUP_NAME(key for map1):
*       KEY-EXCERCISE_NAME(key for map2):
*              ARRAYLIST OF NOTES
&map1: key - MUSCLES_GROUP_NAME, value - map2
&map2: key - EXCERCISE_NAME, value - list of notes for this exercise
*
*/

class FileWorking {
    String FILE_NAME;
    FileWorking( String FILE_NAME){
        this.FILE_NAME = FILE_NAME;
    }
    public boolean isFileExists(String FILE_NAME){
        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/" + this.FILE_NAME);
        return sdCardFile.exists();
    }
    public static String fileStorageState(String FILE_NAME){
        return Environment.getExternalStorageState();

    }
    public boolean isFileStorageEmpty(String FILE_NAME){
        if(!isFileExists(FILE_NAME)){return false;}
        return getFromStorage(FILE_NAME).isEmpty();
    }

    public String getLastNote(String FILE_NAME,String muscles_group_name, String exercise_name){
           return this.getFromStorage(FILE_NAME).get(muscles_group_name).get(exercise_name).get(-1);
    }

    //DELETING
    public void remNote(String FILE_NAME,String muscles_group_name, String exercise_name, int del_note_id){
        Map<String, Map<String, ArrayList<String>>> dataFromFileMap = getFromStorage(FILE_NAME);
        ArrayList<String>notesArrayList = dataFromFileMap.get(muscles_group_name).get(exercise_name);
        dataFromFileMap.get(muscles_group_name).get(exercise_name).remove(del_note_id);
        saveIntoStorage(dataFromFileMap);
    }

    public void delNote(String FILE_NAME,String muscles_group_name, String exercise_name, String del_note){
        Map<String, Map<String, ArrayList<String>>> dataFromFileMap = getFromStorage(FILE_NAME);
        ArrayList<String>notesArrayList = dataFromFileMap.get(muscles_group_name).get(exercise_name);
        dataFromFileMap.get(muscles_group_name).get(exercise_name).remove(notesArrayList.lastIndexOf(del_note));
        saveIntoStorage(dataFromFileMap);
    }

        public void delExercise(String FILE_NAME,String muscles_group_name, String del_exercise_name){
        Map<String, Map<String, ArrayList<String>>> dataFromFileMap = getFromStorage(FILE_NAME);
        dataFromFileMap.get(muscles_group_name).remove(del_exercise_name);//put(new_exercise_name,  new ArrayList<>());
        saveIntoStorage(dataFromFileMap);
    }
    public void delMusclesGroup(String FILE_NAME, String del_muscles_group){
        Map<String, Map<String, ArrayList<String>>> dataFromFileMap = getFromStorage(FILE_NAME);
        dataFromFileMap.remove(del_muscles_group);//put(new_muscles_group, new HashMap<>());
        saveIntoStorage(dataFromFileMap);
    }
    //CREATING
    public void addNewNote(String FILE_NAME,String muscles_group_name, String exercise_name, String new_note){
        Map<String, Map<String, ArrayList<String>>> dataFromFileMap = getFromStorage(FILE_NAME);
        dataFromFileMap.get(muscles_group_name).get(exercise_name).add(new_note);
        saveIntoStorage(dataFromFileMap);
    }
    public  boolean addNewExercise(String FILE_NAME,String muscles_group_name, String new_exercise_name){
        Map<String, Map<String, ArrayList<String>>> dataFromFileMap = getFromStorage(FILE_NAME);
        try {
            dataFromFileMap.get(muscles_group_name).put(new_exercise_name, new ArrayList<>());
        }
        catch (Exception e){
            return false;//ALREADY EXISTING
        }
        saveIntoStorage(dataFromFileMap);
        return true;
    }
    public boolean addNewMusclesGroup(String FILE_NAME, String new_muscles_group){
        Map<String, Map<String, ArrayList<String>>> dataFromFileMap = getFromStorage(FILE_NAME);
        try {
            dataFromFileMap.put(new_muscles_group, new HashMap<>());
        }catch (Exception e){
            return false;//ALREADY EXISTING
        }
        saveIntoStorage(dataFromFileMap);
        return true;
    }
    public Map<String, Map<String, ArrayList<String>>> getFromStorage(String FILE_NAME){

        Map<String, Map<String,ArrayList<String>>> muscles_group_data_map = new HashMap<String, Map<String,ArrayList<String>>>();
        try {
            File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/" + this.FILE_NAME);
            Log.d("TAG", sdCardFile.getPath()); //<-- check the log to make sure the path is correct.

            FileReader fr = new FileReader(sdCardFile);
            Log.e("FILE_READER", "FILE_READER IS CREATED");
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            String muscles_group_name = "";
            String exercise_name = "";
            ArrayList<String>exercise_notes =  null;
            Map<String,ArrayList<String>>exercise_info  = null;//new HashMap<>();
            while (line != null) {
                if(line.equals("{{{")) {
                    muscles_group_name = reader.readLine();
                    exercise_info = new HashMap<>();
                }
                if(line.equals("{{")){
                    exercise_notes =  new ArrayList<>();
                    exercise_name = reader.readLine();
                }
                if(line.equals("{")){
                    exercise_notes.clear();
                    line = reader.readLine();
                    while (!line.equals("}")){
                        exercise_notes.add(line);
                        line = reader.readLine();
                    }
                }
                if(line.equals("}}")){
                    exercise_info.put(exercise_name,exercise_notes);
                }
                if(line.equals("}}}")){
                    muscles_group_data_map.put(muscles_group_name,exercise_info);
                }
                line = reader.readLine();
            }
            reader.close();fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("File not FOUND", String.valueOf(e));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", String.valueOf(e));
        }
        return muscles_group_data_map;
    }
    public void saveIntoStorage(Map<String, Map<String,ArrayList<String>>> muscles_data_map) {
        File sdCardFile = new File(Environment.getExternalStorageDirectory() + "/" +this.FILE_NAME);
        Log.d("TAG", sdCardFile.getPath()); //<-- check the log to make sure the path is correct.
        try{

            FileWriter writer = new FileWriter(sdCardFile, false);
            Log.e("FILE_WRITER","FILE_WRITER IS CREATED");
            writer.write("");//clearing file
            for (String muscles_group : muscles_data_map.keySet()) {
                writer.append("{{{"+'\n');
                writer.append(muscles_group+'\n');
                Map<String,ArrayList<String>> exersises_info = muscles_data_map.get(muscles_group);
                for (String muscles_exercise: exersises_info.keySet()){
                    writer.append("{{"+'\n');
                    writer.append(muscles_exercise+'\n');
                    writer.append("{"+'\n');
                    for(String exersise_note: exersises_info.get(muscles_exercise)){
                        writer.append(exersise_note+'\n');
                    }
                    writer.append("}"+'\n');
                    writer.append("}}"+'\n');

                }
                writer.append("}}}"+'\n');
            }
            writer.flush(); writer.close();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
            Log.e("FILE_WRITER","FILE_WRITER IS NOT CREATED");
        }
    }
}