package com.example.spacelite;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {
    ImageView profilepic;
    EditText newphone, newemail, newname, oldpass, newpass;
    String email,name,phone;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilepic = findViewById(R.id.profilepic);
        newphone = findViewById(R.id.newphone);
        newemail = findViewById(R.id.newemail);
        newname = findViewById(R.id.newname);
        oldpass = findViewById(R.id.oldpass);
        newpass = findViewById(R.id.newpass);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nemail = newemail.getText().toString();
                String nname = newname.getText().toString();
                String opass = oldpass.getText().toString();
                String npass = newpass.getText().toString();
                dialogUpdate(nemail,nname,opass,npass);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name = bundle.getString("name");
        phone = bundle.getString("phone");
        email = bundle.getString("email");

        newphone.setText(phone);
        String image_url = "http://socstudents.net/easynote/userProfile.php" + phone + ".jpg";
        Picasso.with(this).load(image_url)
                .resize(400, 400).into(profilepic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadUserProfile();
    }

    private void dialogUpdate(final String nemail, final String nname, final  String opass, final String npass) {
        class UpdateProfile extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", nemail);
                hashMap.put("name", nname);
                hashMap.put("phone", phone);
                hashMap.put("opassword", opass);
                hashMap.put("npassword", npass);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/easynote/updateprofile.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, Menu.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    bundle.putString("name", name);
                    bundle.putString("phone", phone);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.execute();

    }



    void loadUserProfile() {
        class LoadUserProfile extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userid", phone);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/easynote/loadprofile.php", hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
               // Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray userarray = jsonObject.getJSONArray("user");
                    JSONObject c = userarray.getJSONObject(0);
                    name = c.getString("name");
                    email = c.getString("email");
                } catch (JSONException e) {

                }
                newemail.setText(email);
                newname.setText(name);
                }
            }
        LoadUserProfile loadUserProfile = new LoadUserProfile();
        loadUserProfile.execute();
        }

        }


