package com.Mc256Design.mistareasdeldia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class activityAddUser extends AppCompatActivity {

    //TODO: witgets
    TextInputLayout layoutName;
    Button addUser, openGallery;
    ImageView imageUser;
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
    //TODO: Strings
    String urlImage;
    String IMAGE_FILE_NAME = "IMG_USER_TASK";
    //TODO files
    FileOutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        context = this;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        initialElements();
    }

    private void initialElements(){
        this.layoutName = this.findViewById(R.id.addUserLayoutNameUser);
        this.addUser = this.findViewById(R.id.addUserButton);
        this.openGallery = this.findViewById(R.id.addUserOpenGallery);
        this.imageUser = this.findViewById(R.id.addUserImageVIew);
        this.relativeLayout = this.findViewById(R.id.addUserLayout);
        this.progressDialog = new ProgressDialog(context);
        sqliteManager = new SqliteManager(context);

        openGallery.setOnClickListener(view -> openGallery());

        addUser.setOnClickListener(view -> uploadUserDataBase());
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
            buider.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = buider.create();
            dialog.show();

        }

    }

    private void openGallery(){
        ActivityCompat.requestPermissions(activityAddUser.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
    }

    public boolean checkInput(){
        if(Objects.requireNonNull(layoutName.getEditText()).getText().toString().isEmpty()){
            layoutName.setError("No puedes Dejar el nombre vacio");
            return true;
        }else{
            return false;
        }
    }

    private void startCrop(@NonNull Uri uri){

        String destinationNameFile = "ImageNoConvert";
        destinationNameFile += ".jpg";

        File file = new File(getCacheDir(), destinationNameFile);

            UCrop uCrop = UCrop.of(uri, Uri.fromFile(file));
            uCrop.withAspectRatio(16,9);
            uCrop.withMaxResultSize(450,450);
            uCrop.withOptions(getCropOptions());
            uCrop.start(this);


    }

    private UCrop.Options getCropOptions(){
        UCrop.Options setings = new UCrop.Options();
        setings.setHideBottomControls(false);
        setings.setFreeStyleCropEnabled(true);
        setings.setCircleDimmedLayer(true);
        return setings;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY && data != null) {
            Uri resultUri = data.getData();
            if(resultUri != null){
                startCrop(resultUri);
            }

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode ==  RESULT_OK && data != null) {
            Uri imageUri = UCrop.getOutput(data);

            if(imageUri != null)
                    try {
                        Bitmap imageFromUrl = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                        if(imageFromUrl != null){
                            RoundedBitmapDrawable roundedImage = RoundedBitmapDrawableFactory.create(context.getResources(), imageFromUrl);
                            roundedImage.setCornerRadius(imageFromUrl.getHeight() * imageFromUrl.getWidth());
                            imageUser.setImageDrawable(roundedImage);
                            imageIsSet = true;

                            //save image in device external storage
                            String destinationFileName = this.IMAGE_FILE_NAME;
                            destinationFileName += ".jpg";

                            File file = context.getExternalFilesDir(null);
                            File NewDir = new File(file.getAbsolutePath() + File.separator + "MTD" + File.separator);
                            NewDir.mkdir();
                            File image = new File(NewDir.getAbsolutePath(), destinationFileName);
                            outputStream = new FileOutputStream(image);
                            imageFromUrl.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                            urlImage = image.getAbsolutePath();

                        }

                    }catch (IOException e){
                        Toast.makeText(context, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        System.out.println(e.getMessage());
                    }// TODO Catch

        }else{
            imageIsSet = false;
            AlertDialog.Builder buider = new AlertDialog.Builder(context);
            buider.setTitle("Error en imagen");
            buider.setMessage("No Puedes Dejar La imagen Vacia");
            buider.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
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
            sqliteManager.insertDataUsuarios(
                    Objects.requireNonNull(layoutName.getEditText()).getText().toString(), urlImage);
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