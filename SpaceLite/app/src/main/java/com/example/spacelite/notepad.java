package com.example.spacelite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class notepad extends AppCompatActivity {

    ListView lvnote;
    Button btnAdd,btnDel;
    ArrayList<HashMap<String, String>> notelist;
    Dialog dialogdeleteNote,dialogeditNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        btnAdd = findViewById(R.id.btnAdd);
        btnDel = findViewById(R.id.btnDel);
        lvnote = findViewById(R.id.listviewNote);
        loadNote();


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notepad.this, AddNote.class);
                startActivity(intent);
            }
        });

       btnDel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               deletenoteDialog();
           }
       });

                lvnote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(notepad.this, edit_note.class);
                        Bundle bundle = new Bundle();
                        Log.e("HANIS",notelist.get(position).get("title"));
                        bundle.putString("title",notelist.get(position).get("title"));
                        bundle.putString("data",notelist.get(position).get("data"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }


    private void deletenoteDialog() {
        dialogdeleteNote = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        dialogdeleteNote.setContentView(R.layout.activity_deletenote);
        dialogdeleteNote.getWindow().setBackgroundDrawableResource(android.R.color.white);
        final EditText dTitle = dialogdeleteNote.findViewById(R.id.dTitle);
        Button btnDELETE = dialogdeleteNote.findViewById(R.id.btnDELETE);
        btnDELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dNote =  dTitle.getText().toString();
                deleteNOTE(dNote);
            }
        });
        dialogdeleteNote.show();
    }

    private void deleteNOTE (final String dNote) {
        class DeleteNote extends AsyncTask<Void,String,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("title",dNote);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/easynote/deletenote.php",hashMap);
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")){
                    Toast.makeText(notepad.this, "Success", Toast.LENGTH_LONG).show();
                    dialogdeleteNote.dismiss();
                    loadNote();
                }else{
                    Toast.makeText(notepad.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        DeleteNote deleteNOTE = new DeleteNote();
        deleteNOTE.execute();
    }


   /* private void updatenote(int position) {
        Intent intent = new Intent(notepad.this, AddNote.class);
        Bundle bundle = new Bundle();
        bundle.putString("title",notelist.get(position).get("title"));
        bundle.putString("data",notelist.get(position).get("data"));
        intent.putExtras(bundle);
        startActivity(intent);
    }*/



    private void loadNote() {
        class LoadNote extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                RequestHandler rh = new RequestHandler();
                notelist = new ArrayList<>();
                String s = rh.sendPostRequest
                        ("http://www.socstudents.net/easynote/loadnote.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                notelist.clear();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray restarray = jsonObject.getJSONArray("note");
                    Log.e("ERROR", jsonObject.toString());
                    for (int i = 0; i < restarray.length(); i++) {
                        JSONObject r = restarray.getJSONObject(i);
                        String title = r.getString("title");
                        String data = r.getString("data");
                        HashMap<String, String> Notelisthash = new HashMap<>();
                        Notelisthash.put("title", title);
                        Notelisthash.put("data", data);

                        notelist.add(Notelisthash);
                    }
                } catch (final JSONException e) {
                    Log.e("JSONERROR", e.toString());
                }

                ListAdapter adapter = new NoteAdapter(
                        notepad.this, notelist,
                        R.layout.cus_window, new String[]
                        {"title"}, new int[]
                        {R.id.textView});
                lvnote.setAdapter(adapter);
            }

        }
        LoadNote loadNote = new LoadNote();
        loadNote.execute();
    }

}




