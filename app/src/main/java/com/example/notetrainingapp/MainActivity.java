package com.example.notetrainingapp;
//TODO: Сделать подтверждение удаления
//TODO: Сделать цветнцые кнопки
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText edit_Text_main;
    Button btn_push_main;
    LinearLayout linearLayout_lines_holder;
    String STORAGE_FILE_PATH = "NOTES_STORAGE.txt";//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/NOTES_STORAGE.txt";
    FileWorking FileWorking = new FileWorking(STORAGE_FILE_PATH);
    //

    @SuppressLint("ResourceType")
    protected void generateLineInMain(ViewGroup lines_holder, String muscles_group_name) {
        Intent myIntent = new Intent(this, Activity.class);
        FileWorking FileWorking = new FileWorking(STORAGE_FILE_PATH);
        Map<String, Map<String, ArrayList<String>>> all_notes_data_map = FileWorking.getFromStorage(STORAGE_FILE_PATH);
        ConstraintLayout line_main = new ConstraintLayout(getApplicationContext());
        Button menu_line_button = new Button(getApplicationContext());
        menu_line_button.setAllCaps(false);
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
//                showDialog();
                showDialogDeleteLineConfirmation(line_main,STORAGE_FILE_PATH,muscles_group_name);
  //              if(confirmDialogAnswer()) {
                 //   FileWorking.delMusclesGroup(STORAGE_FILE_PATH, muscles_group_name);
                 //   linearLayout_lines_holder.removeView(line_main);//linearLayout_scroll_left_part.removeView(linearLayout_line_left);
    //            }
            }
        };
        menu_line_button.setOnClickListener(listener_go_to_note_activity_Button);
        btn_delete_line.setOnClickListener(listener_deleting_whole_line_Button);
        linearLayout_lines_holder.addView(line_main);

        line_main.addView(menu_line_button);
        line_main.addView(btn_delete_line);
        btn_delete_line.setId(1);
        menu_line_button.setId(2);
        line_main.getLayoutParams().width = -1;

        int[] chainViews = {menu_line_button.getId(), btn_delete_line.getId()};
        ConstraintSet set = new ConstraintSet();
        Log.e("s", "IDS " + Integer.toString(btn_delete_line.getId()));
        set.clone(line_main);
        set.constrainWidth(menu_line_button.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.constrainWidth(btn_delete_line.getId(), ConstraintSet.MATCH_CONSTRAINT);
        float[] chainWeights = {4, 1};
        set.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                chainViews, chainWeights,
                ConstraintSet.CHAIN_SPREAD);
        line_main.setConstraintSet(set);


}

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1);

        FileWorking FileWorking = new FileWorking(STORAGE_FILE_PATH);

        btn_push_main = findViewById(R.id.button_push);
        edit_Text_main = findViewById(R.id.get_text_view);
        linearLayout_lines_holder = findViewById(R.id.line_layout);
    //    Intent myIntent = new Intent(this, Activity.class);
        Map<String, Map<String, ArrayList<String>>> all_notes_data_map = FileWorking.getFromStorage(STORAGE_FILE_PATH);
        for (String muscles_group_name : all_notes_data_map.keySet()) {
            generateLineInMain(linearLayout_lines_holder,muscles_group_name);
        }
        View.OnClickListener listener_add_Button = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String muscles_group_name = edit_Text_main.getText().toString();
                if (muscles_group_name.equals("")) {
                    return;
                }
                if (FileWorking.getFromStorage(STORAGE_FILE_PATH).containsKey(muscles_group_name)) {
                    return;
                }

               generateLineInMain(linearLayout_lines_holder,muscles_group_name);
               FileWorking.addNewMusclesGroup(STORAGE_FILE_PATH,muscles_group_name);
               edit_Text_main.getText().clear();
            }
        };
        btn_push_main.setOnClickListener(listener_add_Button);
    }

     public boolean confirmDialogAnswer(){
         boolean dialogAnswer = false;
         //showDialog();
         return  dialogAnswer;
     }
     //showDialog(line_main,STORAGE_FILE_PATH,muscles_group_name);
     public void showDialogDeleteLineConfirmation(ViewGroup layoutToDelete, String STORAGE_FILE_PATH, String muscles_group_name) {
         DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 FileWorking.delMusclesGroup(STORAGE_FILE_PATH, muscles_group_name);
                 linearLayout_lines_holder.removeView(layoutToDelete);//linearLayout_scroll_left_part.removeView(linearLayout_line_left);
             }
         };
        CustomDialogFragment dialog = new CustomDialogFragment(positiveOnClickListener,null, "Подтверждение","Вы уверены, что хотите удалить \""+ muscles_group_name +"\" ?");
        dialog.show(getSupportFragmentManager(), "custom");

     }

    public static String stringCutting(String string_input, int limit_in_line) {
        String result_String = "";
        int last_space_index = -1;
        int last_space_added_index = -1;
        for (int i = 0; i < string_input.length(); i++) {

            if ((i - last_space_added_index) == limit_in_line) {

                if ((last_space_index <= last_space_added_index)) {// || (string_input.charAt(i + 1) == ' ')) {//if lenth of word more than limit
                    // OR didnt found a space while parsing
                    result_String += string_input.substring(last_space_added_index + 1, i + 1);
                    if (i != string_input.length() - 1) result_String += "\n";
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
