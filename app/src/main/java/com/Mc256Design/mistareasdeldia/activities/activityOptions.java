package com.Mc256Design.mistareasdeldia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.Mc256Design.mistareasdeldia.navDrawerControl.NavDrawerImplementation;
import com.google.android.material.navigation.NavigationView;

public class activityOptions extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //TODO: widgets
    TextView showTypeMode;
    Switch activeMode;
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
        SetDarkMode darkMode = new SetDarkMode(context, this);
        this.showTypeMode = this.findViewById(R.id.optionsShowTypeMode);
        this.activeMode = this.findViewById(R.id.optionsActiveDarkMode);
        this.cardView = this.findViewById(R.id.cardView);
        this.navegation = new NavDrawerImplementation(context,this,activityOptions.this, R.id.nav_menu_options);
        if (sharedPreferences.getBoolean("isEnableDarkMode", false)){
            this.activeMode.setChecked(true);
            darkMode.setDarkModeCards(cardView);
            darkMode.setDarkModeTextViews(showTypeMode);

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
            this.navegation = null;
            this.navegation = new NavDrawerImplementation(context, this, activityOptions.this, R.id.nav_menu_options);
            restartActivity();
        });

    }

    private void restartActivity(){
        Intent intent = new Intent(context, activityOptions.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_up_transition, R.anim.activity_fade_transition);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navegation.onNavigationItemSelected(item);
        return true;
    }
}