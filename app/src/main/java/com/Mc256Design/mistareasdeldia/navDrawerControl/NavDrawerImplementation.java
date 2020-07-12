package com.Mc256Design.mistareasdeldia.navDrawerControl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;
import com.Mc256Design.mistareasdeldia.activities.activityEditUser;
import com.Mc256Design.mistareasdeldia.activities.activityOptions;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.google.android.material.navigation.NavigationView;

public class NavDrawerImplementation implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    Context context;
    NavigationView navigationView;
    AppCompatActivity app;
    Menu menu;
    int idItemSelect;

    SetDarkMode darkMode;

    public NavDrawerImplementation(Context context, AppCompatActivity app, Activity activity, int id){

        this.context = context;
        this.app = app;
        this.idItemSelect = id;
        this.drawerLayout = app.findViewById(R.id.globalDrawerLayout);
        this.navigationView = app.findViewById(R.id.global_navegationView);
        Toolbar toolbar = app.findViewById(R.id.globalToolbar);
        app.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(activity ,drawerLayout,toolbar,R.string.navResourceOpen, R.string.navResourceClose);
        this.navigationView.bringToFront();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        this.menu = this.navigationView.getMenu();
        this.navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) context);
        this.navigationView.setCheckedItem(this.idItemSelect);

        darkMode = new SetDarkMode(context, this.app);
        this.navigationView.setBackgroundDrawable(darkMode.setNavegationDrawer(this.navigationView));

        View headView = navigationView.getHeaderView(0);

        SqliteManager sqliteManager = new SqliteManager(context);
        Cursor cursor = sqliteManager.queryAllRegistersUsers();

        if(cursor.moveToFirst()){
            byte[] image = cursor.getBlob(2);
            Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0 ,image.length);
            RoundedBitmapDrawable rdUserImagen = RoundedBitmapDrawableFactory.create(context.getResources(), convertImage);
            rdUserImagen.setCornerRadius(convertImage.getHeight() * convertImage.getWidth());
            ImageView imageProfile = headView.findViewById(R.id.image_user_nav);
            TextView nameProfile = headView.findViewById(R.id.name_user_nav);
            imageProfile.setImageDrawable(rdUserImagen);
            nameProfile.setText(cursor.getString(1));
        }

    }

    //TODO: retorna el drawerlayout para su uso
    public DrawerLayout getDrawerLayout(){
        return this.drawerLayout;
    }

    //TODO: metodos sobreescritos
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_menu_home:
                if (idItemSelect == R.id.nav_menu_home){
                    Toast.makeText(context, "Estas en el Home pendejo", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intentMain = new Intent(context.getApplicationContext(), MainActivity.class);
                    app.startActivity(intentMain);
                }
                break;

            case R.id.nav_menu_edit:
                if(idItemSelect == R.id.nav_menu_edit){
                    Toast.makeText(context, "Estas en Editar perfil" , Toast.LENGTH_SHORT).show();
                }else{
                    Intent intentEdit = new Intent(context.getApplicationContext(), activityEditUser.class);
                    app.startActivity(intentEdit);
                }
                break;

            case R.id.nav_menu_about:
                Toast.makeText(context, "about nav", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_menu_taskDo:
                //Intent intentTasksDone = new Intent(context,taskDone.class);
                //app.startActivity(intentTasksDone);

                break;

            case R.id.nav_menu_options:
                app.startActivity(new Intent(context.getApplicationContext(), activityOptions.class));
                break;
            default:
                Toast.makeText(context, "Se ha pulsado un navItem", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
