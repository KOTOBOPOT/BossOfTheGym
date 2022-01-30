package com.example.notetrainingapp;
//TODO: сделать так, чтобы приложение именно на телефоне не крашилось
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText edit_Text_main;
    Button btn_push_main;
    LinearLayout linearLayout_lines_holder;
    String STORAGE_FILE_PATH = "NOTES_STORAGE.txt";//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/NOTES_STORAGE.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);

        FileWorking FileWorking = new FileWorking(STORAGE_FILE_PATH);
/*        if(!FileWorking.isFileExists(STORAGE_FILE_PATH)){
            File file = new File(STORAGE_FILE_PATH);
        }*/

        btn_push_main = findViewById(R.id.button_push);
        edit_Text_main = findViewById(R.id.get_text_view);
        linearLayout_lines_holder = findViewById(R.id.line_layout);
        Intent myIntent = new Intent(this, Activity.class);

        Map<String, Map<String, ArrayList<String>>> all_notes_data_map = FileWorking.getFromStorage(STORAGE_FILE_PATH);//TODO: REALIESE ON CREATE LOAD OF ALL LINES.
        Log.e("isFileStorageExists?",Boolean.toString(FileWorking.isFileExists(STORAGE_FILE_PATH)));
       // if(FileWorking.isFileExists(STORAGE_FILE_PATH))
        for (String muscles_group_name : all_notes_data_map.keySet()) {
            LinearLayout line_main = new LinearLayout(getApplicationContext());
            Button menu_line_button = new Button(getApplicationContext());
            menu_line_button.setText(stringCutting(muscles_group_name, 70));/////
            Button btn_delete_line = new Button(getApplicationContext());
            btn_delete_line.setText("delete");

            View.OnClickListener listener_go_to_note_activity_Button = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myIntent.putExtra("muscles_group_name", muscles_group_name);
                    startActivity(myIntent);
                }
            };
            View.OnClickListener listener_deleting_whole_line_Button = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileWorking.delMusclesGroup(STORAGE_FILE_PATH, muscles_group_name);
                    linearLayout_lines_holder.removeView(line_main);//linearLayout_scroll_left_part.removeView(linearLayout_line_left);
                }
            };
            menu_line_button.setOnClickListener(listener_go_to_note_activity_Button);
            btn_delete_line.setOnClickListener(listener_deleting_whole_line_Button);
            line_main.setOrientation(LinearLayout.HORIZONTAL);
            line_main.addView(menu_line_button);
            line_main.addView(btn_delete_line);
            line_main.setGravity(Gravity.RIGHT);
            linearLayout_lines_holder.addView(line_main);
        }


        View.OnClickListener listener_add_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_Text_main.getText().toString().equals("")) {
                    return;
                }
                String muscles_group_name = edit_Text_main.getText().toString();

                FileWorking.addNewMusclesGroup(STORAGE_FILE_PATH, muscles_group_name);
                Log.e("","isFileStorageExists"+Boolean.toString(FileWorking.isFileExists(STORAGE_FILE_PATH)));
                LinearLayout line_main = new LinearLayout(getApplicationContext());
                //LinearLayout split_line = new LinearLayout(getApplicationContext());
                //split_line.setOrientation(LinearLayout.HORIZONTAL);
                Button menu_line_button = new Button(getApplicationContext());
                menu_line_button.setText(stringCutting(muscles_group_name, 70));/////

                edit_Text_main.getText().clear();
                Button btn_delete_line = new Button(getApplicationContext());
                btn_delete_line.setText("delete");

                View.OnClickListener listener_go_to_note_activity_Button = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myIntent.putExtra("muscles_group_name", muscles_group_name);
                        startActivity(myIntent);
                    }
                };
                View.OnClickListener listener_deleting_whole_line_Button = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileWorking.delMusclesGroup(STORAGE_FILE_PATH, muscles_group_name);
                        linearLayout_lines_holder.removeView(line_main);//linearLayout_scroll_left_part.removeView(linearLayout_line_left);
                    }
                };

                menu_line_button.setOnClickListener(listener_go_to_note_activity_Button);
                btn_delete_line.setOnClickListener(listener_deleting_whole_line_Button);
                line_main.setOrientation(LinearLayout.HORIZONTAL);
                line_main.addView(menu_line_button);
                line_main.addView(btn_delete_line);
                line_main.setGravity(Gravity.RIGHT);
                linearLayout_lines_holder.addView(line_main);
                line_main.getLayoutParams().width = -1;//MATCH_PARENT
                //               menu_line_button.getLayoutParams().width = -1;

            }
        };
        btn_push_main.setOnClickListener(listener_add_Button);

    }


    private String stringCutting(String string_input, int limit_in_line) {
        String result_String = "";
        int last_space_index = -1;
        int last_space_added_index = -1;
        for (int i = 0; i < string_input.length(); i++) {

            if ((i - last_space_added_index) == limit_in_line) {

                if ((last_space_index <= last_space_added_index) || (string_input.charAt(i + 1) == ' ')) {//if lenth of word more than limit
                    // OR didnt found a space while parsing
                    result_String += string_input.substring(last_space_added_index + 1, i + 1);
                    result_String += "\n";
                    last_space_added_index = i;
                } else {
                    result_String += string_input.substring(last_space_added_index + 1, last_space_index + 1);
                    result_String += "\n";
                    last_space_added_index = last_space_index;
                }
            }

            if (string_input.charAt(i) == ' ') {
                last_space_index = i;
            }
            if (last_space_index - last_space_added_index == 1) {
                last_space_added_index = last_space_index;
            }
        }
        if (string_input.length() != 0)
            result_String += string_input.substring(last_space_added_index + 1, string_input.length());
        return result_String;
    }


}
