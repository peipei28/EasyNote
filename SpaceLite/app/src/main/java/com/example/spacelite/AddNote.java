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
import android.widget.Toast;

import java.util.HashMap;

public class AddNote extends AppCompatActivity {

    EditText edtTitle,edtData;
    Button btnDone;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        edtTitle = findViewById(R.id.edtTitle);
        edtData = findViewById(R.id.edtData);
        btnDone = findViewById(R.id.btnDone);


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNote();
            }
        });

    }

    private void newNote() {
        String title, data;
        title = edtTitle.getText().toString();
        data = edtData.getText().toString();
        note = new Note(title, data);
        registerNote();
    }

    private void registerNote() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("NOTEPAD");

        alertDialogBuilder
                .setMessage("Are you sure you want to save?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AddNote.this, "Saving", Toast.LENGTH_SHORT).show();
                        insertNote();
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

    private void insertNote() {
        class RegisterNote extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddNote.this,
                        "Saving", " Have a nice day !", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("title", note.title);
                hashMap.put("data", note.data);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest
                        ("http://socstudents.net/easynote/note.php", hashMap);
                return s;
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(AddNote.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddNote.this, notepad.class);
                    AddNote.this.finish();
                    startActivity(intent);
                } else if (s.equalsIgnoreCase("nodata")) {
                    Toast.makeText(AddNote.this, "Please fill in data first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddNote.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
        RegisterNote registerNote = new RegisterNote();
        registerNote.execute();
    }


        }



