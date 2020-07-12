package com.Mc256Design.mistareasdeldia.controlDarkMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.Mc256Design.mistareasdeldia.R;
import com.google.android.material.navigation.NavigationView;


public class SetDarkMode{

    //TODO: widgets
    Drawable DARK_MODE;
    Drawable LIGHT_MODE;
    ColorFilter LIGHT_COLOR;
    AppCompatActivity app;
    //TODO: integers
    int color = Color.rgb(255,255,255);
    int colorWhite = Color.rgb(255,255,255);
    int colorBlack = Color.rgb(0,0,0);
    //TODO: context
    Context context;
    SharedPreferences sharedPreferences;
    // TODO: COLORS
    ColorStateList colorWhenIsEnabled = ColorStateList.valueOf(colorWhite);
    ColorStateList colorWhenIsNoEnabled = ColorStateList.valueOf(colorBlack);
    //TODO: boleans
    public boolean isEnabledDarkMode;

    public SetDarkMode(Context context, AppCompatActivity app){
        this.context = context;
        this.app = app;
        this.sharedPreferences = this.app.getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        this.DARK_MODE =  this.context.getDrawable(R.drawable.activity_mode_dark_background);
        this.LIGHT_MODE = this.context.getDrawable(R.drawable.activity_mode_dark_background);
        this.LIGHT_COLOR = new LightingColorFilter(color,color);
        this.LIGHT_MODE.setColorFilter(this.LIGHT_COLOR);


        if(sharedPreferences.getBoolean("isEnableDarkMode", false)){
            isEnabledDarkMode = true;
            this.app.getWindow().setBackgroundDrawable(this.DARK_MODE);
        }else{
            this.app.getWindow().setBackgroundDrawable(this.LIGHT_MODE);
            isEnabledDarkMode = false;
        }


    }

    public Drawable setNavegationDrawer(NavigationView navigationView){
        this.sharedPreferences = app.getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        this.DARK_MODE =  this.context.getDrawable(R.drawable.activity_mode_dark_background);
        this.LIGHT_MODE = this.context.getDrawable(R.drawable.activity_mode_dark_background);
        this.LIGHT_COLOR = new LightingColorFilter(color,color);
        this.LIGHT_MODE.setColorFilter(this.LIGHT_COLOR);

        if(this.sharedPreferences.getBoolean("isEnableDarkMode", false)){
            navigationView.setItemTextColor(colorWhenIsEnabled);
            navigationView.setItemIconTintList(colorWhenIsEnabled);
            return  DARK_MODE;

        }else{
            navigationView.setItemTextColor(colorWhenIsNoEnabled);
            navigationView.setItemIconTintList(colorWhenIsNoEnabled);
            return LIGHT_MODE;
        }
    }

    public void setDarkModeCards(CardView card){
        if(isEnabledDarkMode){
            card.setCardBackgroundColor(Color.rgb(71,68,68));
        }else{
            card.setCardBackgroundColor(colorWhite);
        }

    }

    public void setDarkModeTextViews(TextView view){
        if(isEnabledDarkMode){
            view.setTextColor(colorWhite);
        }else {
            view.setTextColor(colorBlack);
        }

    }

}
