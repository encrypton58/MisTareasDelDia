package com.Mc256Design.mistareasdeldia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class activityAddUser extends AppCompatActivity {

    //TODO: witgets
    EditText nameUser;
    Button addUser, openGallery;
    ImageView imageUser, imageData;
    RelativeLayout relativeLayout;
    //TODO: Contextos
    Context context;
    //TODO integers
    final int REQUEST_GALLERY = 999;
    //TODO: booleans
    boolean imageIsSet = false;
    //instacia de las clases
    SqliteManager sqliteManager;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        context = this;
        initialElements();
    }

    private void initialElements(){
        this.nameUser = this.findViewById(R.id.addUserInputName);
        this.addUser = this.findViewById(R.id.addUserButton);
        this.openGallery = this.findViewById(R.id.addUserOpenGallery);
        this.imageUser = this.findViewById(R.id.addUserImageVIew);
        this.imageData = this.findViewById(R.id.addUserDataImage);
        this.relativeLayout = this.findViewById(R.id.addUserLayout);
        this.progressDialog = new ProgressDialog(context);
        sqliteManager = new SqliteManager(context);
        this.imageData.setVisibility(View.INVISIBLE);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadUserDataBase();
            }
        });
    }

    private void uploadUserDataBase(){

        if (!checkInput() && imageIsSet) {

            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog_progress_dialog);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

            async a = new async();
            a.execute();

        }else if (!imageIsSet) {
            AlertDialog.Builder buider = new AlertDialog.Builder(context);
            buider.setTitle("Error en imagen");
            buider.setMessage("No Puedes Dejar La imagen Vacia");
            buider.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = buider.create();
            dialog.show();

        }

    }

    private void openGallery(){
        ActivityCompat.requestPermissions(activityAddUser.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);

    }

    public boolean checkInput(){
        if(nameUser.getText().toString().isEmpty()){
            AlertDialog.Builder buider = new AlertDialog.Builder(context);
            buider.setTitle("Error En Entrada");
            buider.setMessage("No puedes Dejar el nombre vacio");
            buider.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = buider.create();
            dialog.show();
            return true;
        }else{
            return false;
        }
    }

    private byte[] imageViewToByte(ImageView image){
        Bitmap bit = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG,100,stream);

        return stream.toByteArray();
    }

    //TODO: metodos sobre escritos por el sistema
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_GALLERY){
            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent  = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY);
            }else{
                Snackbar snake = Snackbar.make(relativeLayout,"Se han negado los Permisos Vuelve a intertarlo", Snackbar.LENGTH_SHORT);
                snake.show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try{
                assert uri != null;
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap bit = BitmapFactory.decodeStream(is);
                RoundedBitmapDrawable rd = RoundedBitmapDrawableFactory.create(getResources(),bit);
                rd.setCornerRadius(bit.getHeight());
                imageUser.setImageDrawable(rd);
                imageData.setImageBitmap(bit);
                imageIsSet = true;
            }catch(FileNotFoundException fi){
                System.out.println(fi.getMessage());
            }

        }else{
            imageIsSet = false;
            AlertDialog.Builder buider = new AlertDialog.Builder(context);
            buider.setTitle("Error en imagen");
            buider.setMessage("No Puedes Dejar La imagen Vacia");
            buider.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = buider.create();
            dialog.show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    //TODO: fin de los metodos sobre escritos
    @SuppressLint("StaticFieldLeak")
    private class async extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            sqliteManager.insertDataUsuarios(nameUser.getText().toString(), imageViewToByte(imageData));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }



}