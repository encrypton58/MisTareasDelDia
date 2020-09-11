package com.Mc256Design.mistareasdeldia.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.Mc256Design.mistareasdeldia.navDrawerControl.NavDrawerImplementation;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class activityOptions extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: widgets
    CheckBox activeMode;
    CardView cardView;
    //Const
    private static final int SELECT_MUSIC = 10;
    Button selectSong;
    //TODO: context
    Context context;
    SharedPreferences sharedPreferences;
    //TODO: instancia de clases
    NavDrawerImplementation navegation;
    TextView showDataSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sharedPreferences = getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        context = this;
        initialElements();
    }

    private void initialElements() {
        this.activeMode = this.findViewById(R.id.optionsActiveDarkMode);
        this.cardView = this.findViewById(R.id.cardView);
        this.selectSong = this.findViewById(R.id.selectAudio);
        this.showDataSong = this.findViewById(R.id.showDataAudio);
        this.navegation = new NavDrawerImplementation(context, this, activityOptions.this, R.id.nav_menu_options);
        if (sharedPreferences.getBoolean("isEnableDarkMode", false)) {
            this.activeMode.setChecked(true);
            setDarkModeActivity();
        } else {
            this.activeMode.setChecked(false);
        }
        this.activeMode.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                Toast.makeText(context, "Hijo de perra me activaste", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("isEnableDarkMode", true);
                edit.apply();
            } else {
                Toast.makeText(context, "Me desactivaste maldito", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("isEnableDarkMode", false);
                edit.apply();
            }
            restartActivity();
        });
        this.selectSong.setOnClickListener(view -> {
            SelectFileGallery();
        });

    }

    private void setDarkModeActivity(){
        SetDarkMode darkMode = new SetDarkMode(context, this);
        ArrayList<CardView> card = new ArrayList<>();
        ArrayList<CheckBox> check = new ArrayList<>();
        card.add(cardView);
        check.add(activeMode);
        darkMode.setDarkModeCards(card);
        darkMode.setDarkModeCheckBox(check);
        navegation = null;
        navegation = new NavDrawerImplementation(context,this,activityOptions.this, R.id.nav_menu_options);
    }

    protected void restartActivity(){

        CountDownTimer timer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                setDarkModeActivity();
            }
        };
        timer.start();
    }

    public void SelectFileGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && data != null) {
            Cursor cursor = null;
            Uri uriSong = data.getData();
            MediaPlayer media = MediaPlayer.create(context, uriSong);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String[] proj = {MediaStore.Audio.Media.TITLE};
                    cursor = context.getContentResolver().query(uriSong, proj, null, null);
                    int index = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    cursor.moveToFirst();
                    showDataSong.setText(cursor.getString(index));
                }
                Snackbar snake = Snackbar.make(navegation.getDrawerLayout(), "Se ha agregado el audio", Snackbar.LENGTH_LONG);
                snake.setAction("reproducir", view -> {
                    media.start();
                    Snackbar snackbar = Snackbar.make(navegation.getDrawerLayout(), "Detener", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Detener", views -> {
                        media.stop();
                    });
                    snackbar.show();
                });
                snake.show();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            SharedPreferences prefAudio = getSharedPreferences("audio", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefAudio.edit();
            editor.putString("path", uriSong.toString());
            editor.apply();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navegation.onNavigationItemSelected(item);
        return true;
    }


}