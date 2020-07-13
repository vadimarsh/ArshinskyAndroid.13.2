package com.example.arshinskyandroid132;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private static String SAVEDDATA = "checkBoxState";
    private EditText loginEdit;
    private EditText pswEdit;
    private Button logBut;
    private Button regBut;
    private CheckBox checkBox;
    private SharedPreferences mySharedPref;

    private String loginInp;
    private String pswInp;
    private String authFileName = "reg.data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        loginEdit = findViewById(R.id.editLogin);
        pswEdit = findViewById(R.id.editPsw);
        logBut = findViewById(R.id.butLogin);
        regBut = findViewById(R.id.butReg);
        checkBox = findViewById(R.id.checkBox);

        mySharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
        checkBox.setChecked(mySharedPref.getBoolean(SAVEDDATA, false));

        logBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInp = loginEdit.getText().toString();
                pswInp = pswEdit.getText().toString();
                String[] logpsw = {"",""};
                if (loginInp.length() > 0 && pswInp.length() > 0) {
                    try {
                        if (checkBox.isChecked()) {
                            logpsw = readDataFromExternalStorage(authFileName).split(" ");
                        } else {
                            logpsw = readDataFromInternalStorage(authFileName).split(" ");
                        }

                        if ((logpsw[0].equals(loginInp)) && (logpsw[1].equals(pswInp))) {
                            Toast.makeText(MainActivity.this, "Success log-in", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid login or paswd", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(MainActivity.this, "Miss file with regdata", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "File error with regdata", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Fill all edits!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        regBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInp = loginEdit.getText().toString();
                pswInp = pswEdit.getText().toString();

                if (loginInp.length() > 0 && pswInp.length() > 0) {
                    if(checkBox.isChecked()){
                        saveDataToExternalStorage(authFileName, loginInp + " " + pswInp);
                    }
                    else{
                        saveDataToInternStorage(authFileName, loginInp + " " + pswInp);
                    }
                    Toast.makeText(MainActivity.this, "Register!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Fill all edits!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean state = isChecked;
                SharedPreferences.Editor myEditor = mySharedPref.edit();
                myEditor.putBoolean(SAVEDDATA, state);
                myEditor.apply();


            }
        });
    }

    private String readDataFromInternalStorage(String fname) throws IOException {
        StringBuilder readedData = new StringBuilder("");
        String readedLine;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(fname)))) {
            while ((readedLine = reader.readLine()) != null) {
                Log.d("Tag", readedLine);
                readedData.append(readedLine + " ");
            }
        }
        return readedData.toString();
    }

    private void saveDataToInternStorage(String fname, String dataToFile) {
        String[] datas = dataToFile.split(" ");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(fname, MODE_PRIVATE)))) {
            for (String data : datas) {
                bw.write(data + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private String readDataFromExternalStorage(String fname)throws IOException{
        StringBuilder readedData = new StringBuilder("");
        String readedLine;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(getApplicationContext().getExternalFilesDir(null), fname)))) {
            while ((readedLine = reader.readLine()) != null) {
                Log.d("Tag", readedLine);
                readedData.append(readedLine + " ");
            }
        }
        return readedData.toString();
    }
    private void saveDataToExternalStorage(String fname, String dataToFile) {
        String[] datas = dataToFile.split(" ");
        if (isExternalStorageWritable()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getApplicationContext().getExternalFilesDir(null), fname), false))) {
                for (String data : datas) {
                    bw.write(data + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}