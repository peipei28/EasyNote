package com.example.spacelite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ToDoList extends AppCompatActivity {

    ArrayList<HashMap<String, String>> tasklist;
    Spinner spp;
    ListView lvtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        spp = findViewById(R.id.spp);
        lvtask = findViewById(R.id.lvtask);
        loadTask(spp.getSelectedItem().toString());


        spp.setSelection(0, false);
        spp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadTask(spp.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lvtask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String dTask = tasklist.get(position).get("task");
                confirmTask(dTask);
                return false;
            }
        });

    }

    private void confirmTask (final String dTask) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("To Do List");

            alertDialogBuilder
                    .setMessage("Do you finish this task?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTask(dTask);
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


    private void deleteTask(final String dTask) {
        class DeleteTask extends AsyncTask<Void,String,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("task",dTask);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/easynote/deletetask.php",hashMap);
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")){
                    Toast.makeText(ToDoList.this, "Well Done !", Toast.LENGTH_LONG).show();
                    loadTask(spp.getSelectedItem().toString());
                }else{
                    Toast.makeText(ToDoList.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        DeleteTask deleteTask = new DeleteTask();
        deleteTask.execute();
    }


    private void loadTask(final String priority) {
            class LoadTask extends AsyncTask<Void,Void,String>{

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("priority",priority);
                    RequestHandler rh = new RequestHandler();
                    tasklist = new ArrayList<>();
                    String s = rh.sendPostRequest
                            ("http://socstudents.net/easynote/loadtask.php",hashMap);
                    return s;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    // Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    tasklist.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray taskarray = jsonObject.getJSONArray("task");
                        Log.e("ERROR", jsonObject.toString());
                        for (int i = 0; i < taskarray.length(); i++) {
                            JSONObject c = taskarray.getJSONObject(i);
                            String task = c.getString("task");
                            HashMap<String, String> tasklisthash = new HashMap<>();
                            tasklisthash.put("task", task);
                            tasklist.add(tasklisthash);
                        }
                    } catch (final JSONException e) {
                        Log.e("JSONERROR", e.toString());
                    }


                    ListAdapter adapter = new TaskAdapter(
                            ToDoList.this, tasklist,
                            R.layout.cus_window, new String[]
                            {"task"}, new int[]
                            {R.id.textView});
                    lvtask.setAdapter(adapter);
                }

            }
            LoadTask loadTask = new LoadTask();
            loadTask.execute();
    }



    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_option, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addtask:
                Intent intent = new Intent(ToDoList.this, Addtask.class);
                startActivity(intent);
                return true;
        }

            switch (item.getItemId()) {
                case R.id.finishtask:
                    Toast.makeText(ToDoList.this, "Long Press when you finish task", Toast.LENGTH_SHORT).show();
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

}
