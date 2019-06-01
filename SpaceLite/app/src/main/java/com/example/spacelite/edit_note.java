package com.example.spacelite;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class edit_note extends AppCompatActivity {

    EditText editD;
    TextView editT;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        editT = findViewById(R.id.editT);
        editD = findViewById(R.id.editD);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String rtitle = bundle.getString("title");
        String rdata = bundle.getString("data");

        editT.setText(rtitle);
        editD.setText(rdata);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editT.getText().toString();
                String newdata = editD.getText().toString();
                updateNote(title,newdata);
            }
        });
    }

    private void updateNote(final String title,final String newdata) {
        class UpdateNote extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title", title);
                hashMap.put("data", newdata);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/easynote/updatenote.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(edit_note.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(edit_note.this, notepad.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(edit_note.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        UpdateNote updateNote = new UpdateNote();
        updateNote.execute();


    }
}
