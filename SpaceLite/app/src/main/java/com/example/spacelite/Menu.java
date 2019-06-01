package com.example.spacelite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Menu extends AppCompatActivity {

    ImageView btnTo;
    ImageView btnNo;
    String email,name,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnTo = findViewById(R.id.btnTo);
        btnNo = findViewById(R.id.btnNo);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        email= bundle.getString("email");
        phone = bundle.getString("phone");
        name = bundle.getString("name");

        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this,ToDoList.class);
                startActivity(intent);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this,notepad.class);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.myprofile:
                Intent intent = new Intent(Menu.this,ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("email",email);
                bundle.putString("name",name);
                bundle.putString("phone",phone);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            case R.id.logout:
                Intent logoutintent = new Intent(this, LoginActivity.class);
                startActivity(logoutintent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
