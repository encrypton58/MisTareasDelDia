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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.Mc256Design.mistareasdeldia.navDrawerControl.NavDrawerImplementation;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import com.Mc256Design.mistareasdeldia.ClassRequired.popUpUserShow;

public class activityEditUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: widgets
    EditText newNameInput;
    Button openGallery, editUser;
    ImageView newDataImage, newImageUser;
    //TODO: instancia de clase
    SqliteManager sqliteManager;
    ProgressDialog progressDialog;
    NavDrawerImplementation nav;
    SetDarkMode darkMode;
    //TODO: Context
    Context context;
    //TODO: integers
    final int REQUEST_GALLERY = 999;
    //TODO: strings
    String user;
    // TODO: boleanos
    boolean imageIsSet = false;
    //TODO: Rouunded drawable
    RoundedBitmapDrawable rdUserIMagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        context = this;
        initialElements();
    }

    private void initialElements() {
        this.newNameInput = this.findViewById(R.id.editUserInputNewName);
        this.openGallery = this.findViewById(R.id.editUserSelectNewImage);
        this.editUser = this.findViewById(R.id.editUserEdit);
        this.newDataImage = this.findViewById(R.id.editUserNewDataImage);
        this.newImageUser = this.findViewById(R.id.editUserShowNewImage);
        this.newDataImage.setVisibility(View.INVISIBLE);
        sqliteManager = new SqliteManager(context);
        this.nav = new NavDrawerImplementation(context, this, activityEditUser.this, R.id.nav_menu_edit);
        darkMode = new SetDarkMode(context, this);
        this.progressDialog = new ProgressDialog(context);

        SharedPreferences sp = getSharedPreferences("notShowMore", Context.MODE_PRIVATE);
        if(!sp.getBoolean("notMoreShowDialog", false)){
            initialMessage();
        }


        this.editUser.setOnClickListener(view -> uploadUserDataBase());

        this.openGallery.setOnClickListener(view -> openGallery());

        newImageUser.setOnClickListener(view -> popup());

    }

    private void uploadUserDataBase() {

        if(!imageIsSet && !checkInput()){
            startActivity(new Intent(context, MainActivity.class));
            Toast.makeText(context, "SE HA IGNAROADO EL UPDATE", Toast.LENGTH_SHORT).show();
            return;
        }else if(!imageIsSet) {
            checkInput();
            Cursor c = sqliteManager.queryAllRegistersUsers();
            c.moveToLast();
            byte[] image = c.getBlob(2);
            Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0, image.length);
            newDataImage.setImageBitmap(convertImage);
        }else{
            checkInput();
        }

        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progress_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        async a = new async();
        a.execute();


    }

    private void openGallery() {
        ActivityCompat.requestPermissions(activityEditUser.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);

    }

    private boolean checkInput() {
        if(newNameInput.getText().toString().equals("")){
            Cursor c = sqliteManager.queryAllRegistersUsers();
            if (c.moveToFirst()) {
                user = c.getString(1);
            }
        return false;
        }else{
            user = newNameInput.getText().toString();
            return true;
        }
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bit = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG,100,stream);

        return stream.toByteArray();
    }

    private void initialMessage(){
    AlertDialog.Builder build = new AlertDialog.Builder(context);
        build.setTitle("Informacion de la app");
        build.setMessage("Presiona la imagen para ver tus datos actuales \nsi deseas dejar algunos de tus datos actuales solo dejalo vacio");
        build.setCancelable(false);
        build.setNegativeButton("No volver a mostrar", (dialogInterface, i) -> {

            //establece que no se muestre el mensaje del sistema de soporte para intruciones
            // share = notShowMore
            //varible = notMoreShowDialog = true

            SharedPreferences sp = getSharedPreferences("notShowMore", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("notMoreShowDialog", true);
            edit.apply();
            dialogInterface.dismiss();
        });
        build.setPositiveButton("aceptar", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alert = build.create();
        alert.show();
    }

    private void popup(){
        Cursor c = sqliteManager.queryAllRegistersUsers();
        if(c.moveToFirst()){
            byte[] image = c.getBlob(2);
            Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0 ,image.length);
            rdUserIMagen = RoundedBitmapDrawableFactory.create(getResources(), convertImage);
            rdUserIMagen.setCornerRadius(convertImage.getHeight() * convertImage.getWidth());
            user = c.getString(1);
        }
        new popUpUserShow(context, rdUserIMagen, user);
    }

    //TODO: metodos sobre escritos por el sistema
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY);
            } else {
                Snackbar snake = Snackbar.make(nav.getDrawerLayout(),
                        "Se han negado los Permisos Vuelve a intertarlo", Snackbar.LENGTH_SHORT);
                snake.show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try{
                assert uri != null;
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap bit = BitmapFactory.decodeStream(is);
                RoundedBitmapDrawable rd = RoundedBitmapDrawableFactory.create(getResources(),bit);
                rd.setCornerRadius(bit.getHeight());
                newImageUser.setImageDrawable(rd);
                newDataImage.setImageBitmap(bit);
                imageIsSet = true;
            }catch(FileNotFoundException fi){
                System.out.println(fi.getMessage());
            }

        }else{
            imageIsSet = false;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        nav.onNavigationItemSelected(item);
        return true;
    }

    //TODO: fin de los metodos sobre escritos
    @SuppressLint("StaticFieldLeak")
    private class async extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            sqliteManager.updateDataUsers(user, imageViewToByte(newDataImage));
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