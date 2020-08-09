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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.Mc256Design.mistareasdeldia.navDrawerControl.NavDrawerImplementation;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import com.Mc256Design.mistareasdeldia.ClassRequired.popUpUserShow;
import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;

public class activityEditUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: widgets
    TextInputLayout newNameInput;
    Button openGallery, editUser;
    ImageView newImageUser;
    TextView title, subTitle;
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
    String urlImage;
    String IMAGE_FILE_NAME = "IMG_EDIT_USER_TASK";
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
        this.newNameInput = this.findViewById(R.id.editUserLayoutNewName);
        this.openGallery = this.findViewById(R.id.editUserSelectNewImage);
        this.title = this.findViewById(R.id.titleEditUser);
        this.subTitle = this.findViewById(R.id.subTitleEditUser);
        this.editUser = this.findViewById(R.id.editUserEdit);
        this.newImageUser = this.findViewById(R.id.editUserShowNewImage);
        sqliteManager = new SqliteManager(context);
        setDarkMode();
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
            urlImage = c.getString(2);
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
        if(Objects.requireNonNull(newNameInput.getEditText()).getText().toString().equals("")){
            Cursor c = sqliteManager.queryAllRegistersUsers();
            if (c.moveToFirst()) {
                user = c.getString(1);
            }
        return false;
        }else{
            user = Objects.requireNonNull(newNameInput.getEditText()).getText().toString();
            return true;
        }
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
            try {
                Bitmap imagenUser =
                        MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(c.getString(2))));
                rdUserIMagen = RoundedBitmapDrawableFactory.create(getResources(), imagenUser);
                rdUserIMagen.setCornerRadius(imagenUser.getHeight() * imagenUser.getWidth());
                user = c.getString(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        new popUpUserShow(context, rdUserIMagen, user);
    }

    private void setDarkMode(){
        SetDarkMode darkMode = new SetDarkMode(context, activityEditUser.this);
        ArrayList<TextView> views = new ArrayList<>();
        views.add(title);
        views.add(subTitle);
        darkMode.setDarkModeTextViews(views);
        Drawable drawable = newImageUser.getDrawable();
        if (darkMode.isEnabledDarkMode){
            ColorFilter color = new LightingColorFilter(Color.rgb(255,255,255), Color.rgb(255,255,255));
            drawable.setColorFilter(color);
            newImageUser.setImageDrawable(drawable);
        }

    }

    private void startCrop(Uri uri){
        String destinationNameFile = "EDITABLE";
        destinationNameFile += ".jpg";

        UCrop crop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationNameFile)));
        crop.withMaxResultSize(400,400);
        crop.withAspectRatio(16,9);
        crop.withOptions(getOptionsCrop());
        crop.start(this);
    }

    private UCrop.Options getOptionsCrop(){
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        return options;
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
        Uri uriImage = data.getData();
        if(uriImage != null){
            startCrop(uriImage);
        }

        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null){
        Uri imageUri = UCrop.getOutput(data);

        if(imageUri != null) {
            try {
                Bitmap imageFromUrl = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                if (imageFromUrl != null) {
                    RoundedBitmapDrawable roundedImage = RoundedBitmapDrawableFactory.create(context.getResources(), imageFromUrl);
                    roundedImage.setCornerRadius(imageFromUrl.getHeight() * imageFromUrl.getWidth());
                    newImageUser.setImageDrawable(roundedImage);

                    String destinationNameImage = this.IMAGE_FILE_NAME;
                    destinationNameImage += ".jpg";

                    FileOutputStream outputStream;

                    File filePath = context.getExternalFilesDir(null);
                    File dirMDT = new File(filePath.getAbsolutePath() + File.separator + "MTD" + File.separator);

                    if(dirMDT.exists()){
                        File image = new File(dirMDT.getAbsolutePath(), destinationNameImage);
                        outputStream = new FileOutputStream(image);
                        imageFromUrl.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        urlImage = image.getAbsolutePath();
                    }else{
                        if(dirMDT.mkdir()){
                            File image = new File(dirMDT.getAbsolutePath(), destinationNameImage);
                            outputStream = new FileOutputStream(image);
                            imageFromUrl.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                            urlImage = image.getAbsolutePath();
                        }
                    }

                    imageIsSet = true;
                }

            } catch (IOException e) {
                Toast.makeText(context, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }// TODO Catch
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
            sqliteManager.updateDataUsers(user, urlImage);
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