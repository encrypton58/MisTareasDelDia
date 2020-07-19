package com.Mc256Design.mistareasdeldia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.Mc256Design.mistareasdeldia.navDrawerControl.NavDrawerImplementation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class activityOptions extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: widgets
    CheckBox activeMode;
    CardView cardView;
    //TODO: context
    Context context;
    SharedPreferences sharedPreferences;
    //TODO: instancia de clases
    NavDrawerImplementation navegation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sharedPreferences = getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        context = this;
        initialElements();
    }

    private void initialElements(){
        this.activeMode = this.findViewById(R.id.optionsActiveDarkMode);
        this.cardView = this.findViewById(R.id.cardView);
        this.navegation = new NavDrawerImplementation(context,this,activityOptions.this, R.id.nav_menu_options);
        if (sharedPreferences.getBoolean("isEnableDarkMode", false)){
            this.activeMode.setChecked(true);
            setDarkModeActivity();
        }else{
            this.activeMode.setChecked(false);
        }
        this.activeMode.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked){
                Toast.makeText(context, "Hijo de perra me activaste", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("isEnableDarkMode", true);
                edit.apply();
            }else {
                Toast.makeText(context, "Me desactivaste maldito", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("isEnableDarkMode", false);
                edit.apply();
            }
            restartActivity();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navegation.onNavigationItemSelected(item);
        return true;
    }


}