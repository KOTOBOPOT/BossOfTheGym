//TODO:  U STAYED ON THE FACT, THAT U UNDERSTOOD,
// UR CODE IS FUCKING BULLSHIT.
// AIM: MAKE ALL BUTTON LISTENERS - FUNC WITH ARGUMENTS(FOR EXAMOLE U DID THIS WITH FOR-CYCLE)
package com.example.notetrainingapp;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity extends AppCompatActivity {

    EditText edit_Text_main;
    Button btn_push_main;
    LinearLayout linearLayout_lines_holder;
    TextView txt_view_muscles_group_name;
    String STORAGE_FILE_PATH = "NOTES_STORAGE.txt";//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/NOTES_STORAGE.txt";
    FileWorking FileWorking = new FileWorking(STORAGE_FILE_PATH);
    public void generate_line(LinearLayout line_holder, String exercise_name, String muscles_group_name) {
        if (!FileWorking.isFileExists(STORAGE_FILE_PATH)) {
            return;
        }
        Map<String, ArrayList<String>> exercises_info_map = FileWorking.getFromStorage(STORAGE_FILE_PATH).get(muscles_group_name);
        LinearLayout line_main = new LinearLayout(getApplicationContext());
        HorizontalScrollView horizontalScrollView_line = new HorizontalScrollView(getApplicationContext());
        LinearLayout linearLayout_right = new LinearLayout(getApplicationContext());
        LinearLayout linearLayout_line_left = new LinearLayout(getApplicationContext());

        EditText editText_output_line = new EditText(getApplicationContext());
        editText_output_line.setHint(exercise_name);
        Button btn_note_adding = new Button(getApplicationContext());
        btn_note_adding.setText("+");
        Button btn_delete_note = new Button(getApplicationContext());
        Button btn_delete_line = new Button(getApplicationContext());
        btn_delete_note.setText("-");
        btn_delete_line.setText("-");


        View.OnClickListener listener_adding_new_note_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note_text = editText_output_line.getText().toString();
                if (note_text.equals("")) return;//LinearLayout line_main_copy = (LinearLayout) ((LinearLayout) v.getParent()).getParent();
                Button text_note_button = new Button(getApplicationContext());
                text_note_button.setText(note_text);
                editText_output_line.getText().clear();
                text_note_button.setAllCaps(false);
                linearLayout_right.addView(text_note_button);
                text_note_button.getLayoutParams().width = -2;
                FileWorking.addNewNote(STORAGE_FILE_PATH,muscles_group_name,exercise_name,note_text);
                Log.e("NOTE ADDED", "Note is " + note_text);
            }
        };
        View.OnClickListener listener_delete_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    linearLayout_right.removeViewAt(-1);//right.getChildCount() - 1);
                    String last_note = FileWorking.getLastNote(STORAGE_FILE_PATH,muscles_group_name,exercise_name);
                    FileWorking.delNote(STORAGE_FILE_PATH, muscles_group_name, exercise_name, last_note);
                } catch (Exception e) {

                }
            }
        };
        View.OnClickListener listener_deleting_whole_line_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_holder.removeView(line_main);
                FileWorking.delExercise(STORAGE_FILE_PATH,muscles_group_name,exercise_name);
            }
        };
        btn_note_adding.setOnClickListener(listener_adding_new_note_Button);
        btn_delete_note.setOnClickListener(listener_delete_Button);
        btn_delete_line.setOnClickListener(listener_deleting_whole_line_Button);


        linearLayout_line_left.addView(btn_delete_line);
        linearLayout_line_left.addView(editText_output_line);
        linearLayout_line_left.addView(btn_note_adding);
        linearLayout_line_left.addView(btn_delete_note);

        int button_DelAdd_Standart_Size = 150;
        btn_delete_line.getLayoutParams().width = button_DelAdd_Standart_Size;
        btn_delete_note.getLayoutParams().width = button_DelAdd_Standart_Size;
        btn_note_adding.getLayoutParams().width = button_DelAdd_Standart_Size;

        horizontalScrollView_line.addView(linearLayout_right);
        line_main.setOrientation(LinearLayout.HORIZONTAL);
        line_main.addView(linearLayout_line_left);
        line_main.addView(horizontalScrollView_line);

        if (exercises_info_map.containsKey(exercise_name)) {
            for (String note_text : exercises_info_map.get(exercise_name)) {
                Button note_button = new Button(getApplicationContext());
                note_button.setText(note_text);
                linearLayout_right.addView(note_button);
            }
        } else {//if push_button pushed
            edit_Text_main.getText().clear();
            FileWorking.addNewExercise(STORAGE_FILE_PATH,muscles_group_name,exercise_name);
        }

        line_holder.addView(line_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        String muscles_group_name = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            muscles_group_name = extras.getString("muscles_group_name");
            Log.e("New Activity Launched", muscles_group_name);
        }

        btn_push_main = findViewById(R.id.button_push);
        edit_Text_main = findViewById(R.id.get_text_view);
        linearLayout_lines_holder = findViewById(R.id.line_layout);
        txt_view_muscles_group_name = findViewById(R.id.txt_view_up);
        txt_view_muscles_group_name.setText(muscles_group_name);


        Map<String, ArrayList<String>> exercises_info_map = FileWorking.getFromStorage(STORAGE_FILE_PATH).get(muscles_group_name);
        if(exercises_info_map!=null) for (String ex_name : exercises_info_map.keySet()) {
            generate_line(linearLayout_lines_holder, ex_name, muscles_group_name);
        }
        /**/

        String finalMuscles_group_name = muscles_group_name;
        String finalMuscles_group_name1 = muscles_group_name;
        View.OnClickListener listener_add_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ex_name = edit_Text_main.getText().toString();
                if (ex_name.equals("")) {
                    return;
                }
                generate_line(linearLayout_lines_holder,ex_name, finalMuscles_group_name1);
            }
        };
        btn_push_main.setOnClickListener(listener_add_Button);
    }

}
                /*//TODO: RELEASE SAVING TO STORAGE
                FileWorking.addNewExercise(STORAGE_FILE_PATH, finalMuscles_group_name, ex_name);

                LinearLayout line_main = new LinearLayout(getApplicationContext());
                HorizontalScrollView horizontalScrollView_line = new HorizontalScrollView(getApplicationContext());
                LinearLayout linearLayout_line = new LinearLayout(getApplicationContext());

                EditText editText_output_line = new EditText(getApplicationContext());
                editText_output_line.setHint(ex_name);
                edit_Text_main.getText().clear();
                Button btn_note_adding = new Button(getApplicationContext());
                btn_note_adding.setText("+");
                Button btn_delete_note = new Button(getApplicationContext());
                Button btn_delete_line = new Button(getApplicationContext());
                btn_delete_note.setText("-");
                btn_delete_line.setText("-");

                LinearLayout linearLayout_line_left = new LinearLayout(getApplicationContext());


                horizontalScrollView_line.addView(linearLayout_line);
                line_main.setOrientation(LinearLayout.HORIZONTAL);
                line_main.addView(linearLayout_line_left);
                line_main.addView(horizontalScrollView_line);
                //TODO: try to replace all vars to funcs
                *//*View.OnClickListener listener_deleting_whole_line_Button = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout line_main_copy = (LinearLayout) ((LinearLayout) v.getParent()).getParent();
                        line_main_copy.removeView(linearLayout_line_left);
                        line_main_copy.removeView(line_main_copy.getChildAt(-1));//Deleting horizontal part of current line
                    }
                };*//*
                View.OnClickListener listener_adding_new_note_Button = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String note_text = editText_output_line.getText().toString();
                        if(note_text.equals(""))return;

                        LinearLayout line_main_copy = (LinearLayout) ((LinearLayout) v.getParent()).getParent();

                        Button text_note_button =new Button(getApplicationContext());
                        text_note_button.setText(note_text);
                        editText_output_line.getText().clear();
                        text_note_button.setAllCaps(false);
                        //horizontalScrollView_line;
                        //TODO: CANT GET HORIZONTAL VIEW CHILD
                        //Attempt to invoke virtual method 'int android.widget.HorizontalScrollView.getChildCount()' on a null object reference on a null object reference
        //                int BULLSHIT = ((ViewGroup)line_main_copy.getChildAt(-1)).getChildCount();
          //              Log.e("CHILD_COUNTER", Integer.toString(BULLSHIT));
            //            ((ViewGroup)((HorizontalScrollView)line_main_copy.getChildAt(-1)).getChildAt(0)).addView(text_note_button);//finding linearlayoutline(child of horizontal scroll view) and adding new note to it
                            linearLayout_line.addView(text_note_button);
                        text_note_button.getLayoutParams().width = -2;
                      Log.e("NOTE ADDED","Note is "+note_text);
                    }
                };
                View.OnClickListener listener_delete_Button = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            linearLayout_line.removeViewAt(linearLayout_line.getChildCount() - 1);
                        } catch (Exception e) {

                        }
                    }
                };
                btn_note_adding.setOnClickListener(listener_adding_new_note_Button);
                btn_delete_note.setOnClickListener(listener_delete_Button);
                btn_delete_line.setOnClickListener(listener_deleting_whole_line_Button);

                linearLayout_line_left.addView(btn_delete_line);
                linearLayout_line_left.addView(editText_output_line);
                linearLayout_line_left.addView(btn_note_adding);
                linearLayout_line_left.addView(btn_delete_note);

       *//*         horizontalScrollView_line.addView(linearLayout_line);

                line_main.setOrientation(LinearLayout.HORIZONTAL);
                line_main.addView(linearLayout_line_left);
                line_main.addView(horizontalScrollView_line);*//*

                linearLayout_lines_holder.addView(line_main);
            }
        } ;
        btn_push_main.setOnClickListener(listener_add_Button);
    }*/



//line_main.removeView(linearLayout_line_left);//linearLayout_scroll_left_part.removeView(linearLayout_line_left);
//horizontalScrollView_line.removeView(linearLayout_line);
