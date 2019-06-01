package com.example.spacelite;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    EditText edEmail,edName, edPassword, edPhone;
    Button btnRegister;
    TextView tvAlready;
    User user;
    ImageView btnTake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edEmail = findViewById(R.id.edEmail);
        edName = findViewById(R.id.edName);
        edPassword = findViewById(R.id.edPassword);
        edPhone = findViewById(R.id.edPhone);
        btnRegister = findViewById(R.id.btnRegister);
        tvAlready = findViewById(R.id.tvAlready);
        btnTake = findViewById(R.id.btnTake);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUserInput();
            }
        });

        tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTakePicture();
            }
        });

    }

    private void dialogTakePicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Take Picture");

        alertDialogBuilder
                .setMessage("Please make sure you are using your own picture")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void registerUserInput() {
        String email,name, password, phone;
        email = edEmail.getText().toString();
        name = edName.getText().toString();
        password = edPassword.getText().toString();
        phone = edPhone.getText().toString();
        user = new User(email,name, password, phone);
        registerUserDialog();
    }

    private void registerUserDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Welcome to EASYNOTE");

        alertDialogBuilder
                .setMessage("Are you sure you want to register?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       new Encode_image().execute(getDir(),user.phone+".jpg");
                        Toast.makeText(RegisterActivity.this, "Processing", Toast.LENGTH_SHORT).show();
                        insertData();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap,400,500);
            btnTake.setImageBitmap(imageBitmap);
            btnTake.buildDrawingCache();
            ContextWrapper cw = new ContextWrapper(this);
            File pictureFileDir = cw.getDir("basic", Context.MODE_PRIVATE);
            if (!pictureFileDir.exists()) {
                pictureFileDir.mkdir();
            }
            Log.e("FILE NAME", "" + pictureFileDir.toString());
            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                return;
            }
            FileOutputStream outStream = null;
            String photoFile = "profile.jpg";
            File outFile = new File(pictureFileDir, photoFile);
            try {
                outStream = new FileOutputStream(outFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                //hasimage = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertData(){
        class RegisterUser extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this,
                        "Registration", "Thanks for register !", false, false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("email", user.email);
                hashMap.put("name", user.name);
                hashMap.put("password", user.password);
                hashMap.put("phone", user.phone);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest
                        ("http://socstudents.net/easynote/register.php", hashMap);
                return s;
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.finish();
                    startActivity(intent);
                } else if (s.equalsIgnoreCase("nodata")) {
                    Toast.makeText(RegisterActivity.this, "Please fill in data first", Toast.LENGTH_SHORT).show();
                }
            }

        }
        RegisterUser registerUser = new RegisterUser();
        registerUser.execute();
    }

    public String getDir(){
        ContextWrapper cw = new ContextWrapper(this);
        File pictureFileDir = cw.getDir("basic", Context.MODE_PRIVATE);
        if (!pictureFileDir.exists()) {
            pictureFileDir.mkdir();
        }
        Log.d("GETDIR",pictureFileDir.getAbsolutePath());
        return pictureFileDir.getAbsolutePath()+"/profile.jpg";
    }

    public class Encode_image extends AsyncTask<String, String, Void> {
        private String encoded_string, image_name;
        Bitmap bitmap;

        @Override
        protected Void doInBackground(String... args) {
            String filname = args[0];
            image_name = args[1];
            bitmap = BitmapFactory.decodeFile(filname);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            makeRequest(encoded_string, image_name);
        } //submit image

        private void makeRequest(final String encoded_string, final String image_name) {
            class UploadAll extends AsyncTask<Void, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(Void... params) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("encoded_string", encoded_string);
                    map.put("image_name", image_name);
                    RequestHandler rh = new RequestHandler();//request server connection
                    String s = rh.sendPostRequest("http://socstudents.net/easynote/userImage.php", map);
                    return s;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (s.equalsIgnoreCase("Success")) {
                        insertData();
                        // ToamakeTst.ext(RegisterActivity.this, "Success Upload Image", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Failed Registration", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            UploadAll uploadall = new UploadAll();
            uploadall.execute();
        }
   }
   }






