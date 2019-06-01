package com.example.spacelite;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.HashMap;

public class Addtask extends AppCompatActivity {

    EditText edtTask,editP;
    Button btnT;
    Task task;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        edtTask = findViewById(R.id.edtTask);
        editP = findViewById(R.id.editP);
        btnT = findViewById(R.id.btnT);

        btnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTask();
            }
        });
    }

    private void newTask() {
        String nTask, priority;
        nTask = edtTask.getText().toString();
        priority = editP.getText().toString();
        task = new Task(nTask,priority);
        registerTask();
    }

    private void registerTask() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("NOTEPAD");

        alertDialogBuilder
                .setMessage("Are you sure you want to save?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Addtask.this, "Saving", Toast.LENGTH_SHORT).show();
                        insertTask();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void insertTask() {
        class RegisterTask extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Addtask.this,
                        "Saving", " Have a nice day !", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("task", task.ntask);
                hashMap.put("priority", task.priority);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest
                        ("http://socstudents.net/easynote/addtask.php", hashMap);
                return s;
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(Addtask.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Addtask.this, ToDoList.class);
                    Addtask.this.finish();
                    startActivity(intent);
                } else if (s.equalsIgnoreCase("nodata")) {
                    Toast.makeText(Addtask.this, "Please fill in data first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Addtask.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
        RegisterTask registerTask = new RegisterTask();
        registerTask.execute();
    }


}

