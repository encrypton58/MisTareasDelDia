package com.Mc256Design.mistareasdeldia.controlDarkMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.Mc256Design.mistareasdeldia.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;


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
    private ArrayList<TextInputLayout> layoutArrayList;

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

    public void setDarkModeCards(ArrayList<CardView> cardArray){
        if(isEnabledDarkMode){
            for(int i = 0; i <= cardArray.size() - 1; i++){
                cardArray.get(i).setCardBackgroundColor(colorBlack);
            }
        }else{
            for (int i = 0; i == cardArray.size()-1; i++) {
                cardArray.get(i).setCardBackgroundColor(colorWhite);
            }
        }

    }

    public void setDarkModeCheckBox(ArrayList<CheckBox> checkList){
        if(isEnabledDarkMode){
            for(int i = 0; i == checkList.size() - 1; i++){
                checkList.get(i).setTextColor(colorWhite);
            }
        }else{
            for (int i = 0; i == checkList.size()-1; i++) {
                checkList.get(i).setTextColor(colorBlack);
            }
        }
    }

    public void setDarkModeTextViews(ArrayList<TextView> textViewArrayList){
        if(isEnabledDarkMode){
            for (int i = 0; i <= textViewArrayList.size() - 1; i++) {
                textViewArrayList.get(i).setTextColor(colorWhite);
            }
        }else {
            for (int i = 0; i <= textViewArrayList.size()-1; i++) {
                textViewArrayList.get(i).setTextColor(colorBlack);
            }
        }

    }

    public void setDarkModeEditText(ArrayList<EditText> editTextArrayList){
        if(isEnabledDarkMode){
            for (int i = 0; i <= editTextArrayList.size() - 1; i++) {
                editTextArrayList.get(i).setTextColor(colorWhite);
                editTextArrayList.get(i).setHintTextColor(colorWhite);
                editTextArrayList.get(i).setBackgroundTintList(colorWhenIsEnabled);
            }
        }else {
            for (int i = 0; i <= editTextArrayList.size()-1; i++) {
                editTextArrayList.get(i).setTextColor(colorBlack);
                editTextArrayList.get(i).setHintTextColor(colorBlack);
                editTextArrayList.get(i).setBackgroundTintList(colorWhenIsNoEnabled);
            }
        }

    }

    public void setDarkModeLayout(RelativeLayout layout){
        if (isEnabledDarkMode){
            layout.setBackground(app.getDrawable(R.drawable.item_view_backgroud_swipe));
        }else{
            layout.setBackground(app.getDrawable(R.drawable.item_view_background));
        }

    }

    public void setDarkModeLayout(ConstraintLayout layout){
        if (isEnabledDarkMode){
            layout.setBackground(app.getDrawable(R.drawable.item_view_backgroud_swipe));
        }else {
            layout.setBackground(app.getDrawable(R.drawable.item_view_background));
        }
    }

    public void setDarkModeNavegationBar(Window w){
        if(isEnabledDarkMode){
            w.setNavigationBarColor(colorBlack);
            w.setStatusBarColor(colorBlack);
        }else{
            w.setNavigationBarColor(colorWhite);
            w.setStatusBarColor(Color.parseColor("#00B398"));
        }

    }

}
