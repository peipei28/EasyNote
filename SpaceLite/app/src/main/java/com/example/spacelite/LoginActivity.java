package com.example.spacelite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister,tvForgot;
    EditText edEmail,edPassword;
    Button btnLogin;
    SharedPreferences sharedPreferences;
    CheckBox cbRemember;
    Dialog dialogforgotpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgot = findViewById(R.id.tvForgot);
        cbRemember = findViewById(R.id.cbRemember);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String pass = edPassword.getText().toString();

                loginUser(email,pass);
            }
        });
        cbRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbRemember.isChecked()){
                    String email = edEmail.getText().toString();
                    String pass = edPassword.getText().toString();
                    savePref(email,pass);
                }

            }
        });
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordDialog();
            }
        });
        loadPref();
    }

    private void forgotPasswordDialog() {
        dialogforgotpass = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        dialogforgotpass.setContentView(R.layout.activity_forgot_password);
        dialogforgotpass.getWindow().setBackgroundDrawableResource(android.R.color.white);
        final EditText fEmail = dialogforgotpass.findViewById(R.id.fEmail);
        Button btnVerify = dialogforgotpass.findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String forgotemail =  fEmail.getText().toString();
                sendPassword(forgotemail);
            }
        });
        dialogforgotpass.show();
    }

    private void sendPassword(final String forgotemail) {
        class SendPassword extends AsyncTask<Void,String,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap();
                hashMap.put("email",forgotemail);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/easynote/verifyemail.php",hashMap);
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")){
                    Toast.makeText(LoginActivity.this, "Success. Check your email", Toast.LENGTH_LONG).show();
                    dialogforgotpass.dismiss();
                }else{
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        SendPassword sendPassword = new SendPassword();
        sendPassword.execute();
    }

    private void savePref(String e, String p) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", e);
        editor.putString("password", p);

        editor.commit();
        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void loadPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String premail = sharedPreferences.getString("email", "");
        String prpass = sharedPreferences.getString("password", "");
        if (premail.length()>0){
            cbRemember.setChecked(true);
            edEmail.setText(premail);
            edPassword.setText(prpass);
        }
    }

    private void loginUser(final String email, final String pass) {
        class LoginUser extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,
                        "Login user","Going into main page",false,false);
            }
            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("password", pass);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/easynote/login.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equalsIgnoreCase("failed")){
                    Toast.makeText(LoginActivity.this, "Please check your email or password", Toast.LENGTH_LONG).show();
                }
                else if(s.length()>7){
                   //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                    String[] val = s.split(",");
                    Intent intent = new Intent(LoginActivity.this,Menu.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("email",email);
                    bundle.putString("name",val[0]);
                    bundle.putString("phone",val[1]);
                    intent.putExtras(bundle);
                   startActivity(intent);
                }
            }
        }
        LoginUser loginUser = new LoginUser();
        loginUser.execute();
    }
}
