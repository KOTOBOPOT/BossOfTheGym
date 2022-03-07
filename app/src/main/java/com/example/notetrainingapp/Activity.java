package com.example.notetrainingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
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
    int MAX_LENGTH_EDITTEXT_LINE_WHILE_INPUT = 25;
    int LIM_IN_EDITTEXT_HINT = 10;
    int LIM_IN_NOTE_TEXT = 10;
    public void generate_note(LinearLayout note_holder, String note_text){
        if (note_text.equals("")) return;//LinearLayout line_main_copy = (LinearLayout) ((LinearLayout) v.getParent()).getParent();
        Button text_note_button = new Button(getApplicationContext());
        text_note_button.setText(MainActivity.stringCutting(note_text,LIM_IN_NOTE_TEXT));
        text_note_button.setAllCaps(false);
        note_holder.addView(text_note_button);

        text_note_button.getLayoutParams().height = -1;//MATCH PARENT

        ((HorizontalScrollView)note_holder.getParent()).fullScroll(View.FOCUS_RIGHT);
    }
    public void generate_line(LinearLayout line_holder, String exercise_name, String muscles_group_name) {
        if (!FileWorking.isFileExists(STORAGE_FILE_PATH)) {
            Log.e("ERROR","FILE NOT EXIST");
            return;
        }

        Map<String, ArrayList<String>> exercises_info_map = FileWorking.getFromStorage(STORAGE_FILE_PATH).get(muscles_group_name);
        LinearLayout line_main = new LinearLayout(getApplicationContext());
        HorizontalScrollView horizontalScrollView_line = new HorizontalScrollView(getApplicationContext());
        LinearLayout linearLayout_right = new LinearLayout(getApplicationContext());

        LinearLayout linearLayout_line_left = new LinearLayout(getApplicationContext());

        EditText editText_output_line = new EditText(getApplicationContext());
        editText_output_line.setHint(MainActivity.stringCutting(exercise_name,LIM_IN_EDITTEXT_HINT));
     editText_output_line.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_LENGTH_EDITTEXT_LINE_WHILE_INPUT)});
        Button btn_note_adding = new Button(getApplicationContext());
        btn_note_adding.setText("+");
        Button btn_delete_note = new Button(getApplicationContext());
        Button btn_delete_line = new Button(getApplicationContext());
        btn_delete_note.setText("-");
        btn_delete_line.setText("-");
        View.OnClickListener listener_adding_new_note_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note_text =editText_output_line.getText().toString();
                if(note_text.equals("")){return;}
                generate_note(linearLayout_right, note_text);
                editText_output_line.getText().clear();

                horizontalScrollView_line.fullScroll(View.FOCUS_RIGHT);
                FileWorking.addNewNote(STORAGE_FILE_PATH,muscles_group_name,exercise_name,note_text);
                Log.e("NOTE ADDED", "Note is " + note_text);
            }
        };
        View.OnClickListener listener_delete_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDeleteNoteConfirmation(linearLayout_right, line_main,STORAGE_FILE_PATH, muscles_group_name, exercise_name);

            }
        };
        View.OnClickListener listener_deleting_whole_line_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDeleteLineConfirmation(line_holder,line_main,STORAGE_FILE_PATH, muscles_group_name,exercise_name);
/*
                line_holder.removeView(line_main);
                FileWorking.delExercise(STORAGE_FILE_PATH,muscles_group_name,exercise_name);
*/
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

        horizontalScrollView_line.post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView_line.fullScroll(View.FOCUS_RIGHT);
            }
        });
        line_main.setOrientation(LinearLayout.HORIZONTAL);
        line_main.addView(linearLayout_line_left);

        line_main.addView(horizontalScrollView_line);
        btn_note_adding.getLayoutParams().height = -1;btn_delete_note.getLayoutParams().height = -1;btn_delete_line.getLayoutParams().height = -1;

        if ((exercises_info_map.containsKey(exercise_name))&&(exercise_name!=edit_Text_main.getText().toString())) {

            for (String note_text : exercises_info_map.get(exercise_name)) {
                generate_note(linearLayout_right,note_text);
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
            Log.d("Line generated", ex_name);
        }


        String finalMuscles_group_name1 = muscles_group_name;
        View.OnClickListener listener_add_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ex_name = edit_Text_main.getText().toString();
                if (ex_name.equals("")) {
                    return;
                }
                Map<String, ArrayList<String>> exercises_info_map = FileWorking.getFromStorage(STORAGE_FILE_PATH).get(finalMuscles_group_name1);
                if (exercises_info_map.containsKey(ex_name)) {
                    return;
                }
                generate_line(linearLayout_lines_holder,ex_name, finalMuscles_group_name1);
            }
        };
        btn_push_main.setOnClickListener(listener_add_Button);
    }

    public void showDialogDeleteNoteConfirmation(ViewGroup note_holder,ViewGroup layoutToDelete, String STORAGE_FILE_PATH, String muscles_group_name,String exercise_name) {
        DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    note_holder.removeViewAt(note_holder.getChildCount() - 1);
                }
                catch (Exception e){
                    Log.e("ERROR2", e.toString());
                    return;
                }
                FileWorking.remNote(STORAGE_FILE_PATH, muscles_group_name, exercise_name, note_holder.getChildCount() );
            }
        };
        CustomDialogFragment dialog = new CustomDialogFragment(positiveOnClickListener,null, "Подтверждение","Вы уверены, что хотите удалить последнюю запись в \""+ exercise_name +"\" ?");
        dialog.show(getSupportFragmentManager(), "custom");

    }



    public void showDialogDeleteLineConfirmation(ViewGroup line_holder,ViewGroup layoutToDelete, String STORAGE_FILE_PATH, String muscles_group_name,String exercise_name) {
        DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                line_holder.removeView(layoutToDelete);
                FileWorking.delExercise(STORAGE_FILE_PATH,muscles_group_name,exercise_name);
            }
        };
        CustomDialogFragment dialog = new CustomDialogFragment(positiveOnClickListener,null, "Подтверждение","Вы уверены, что хотите удалить упражнение \""+ exercise_name +"\" ?");
        dialog.show(getSupportFragmentManager(), "custom");

    }

}