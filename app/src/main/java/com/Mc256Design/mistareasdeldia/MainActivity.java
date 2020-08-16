package com.Mc256Design.mistareasdeldia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.Mc256Design.mistareasdeldia.RecyclerClass.swipeTasks;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;
import com.Mc256Design.mistareasdeldia.activities.activityAddTask;
import com.Mc256Design.mistareasdeldia.ClassRequired.tarea;
import com.Mc256Design.mistareasdeldia.RecyclerClass.recyclerAdapterItems;
import com.Mc256Design.mistareasdeldia.activities.activityAddUser;
import com.Mc256Design.mistareasdeldia.activities.activityEditTask;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.Mc256Design.mistareasdeldia.navDrawerControl.NavDrawerImplementation;
import com.Mc256Design.mistareasdeldia.service.TimerService;
import com.Mc256Design.mistareasdeldia.service.WorkManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
          swipeTasks.recyclerItemTouchHelperListener{

    //TODO: widgets
    TextView showDia, showUser;
    FloatingActionButton addTaskButton;
    RecyclerView recyclerShowTask;
    ImageView showUserImage;
    SwipeRefreshLayout refreshLayout;
    //TODO: instance of class
    NavDrawerImplementation ImplementsNavegationDrawer;
    SqliteManager sqliteManager;
    SharedPreferences sp;
    //TODO: context
    Context context;
    //TODO: recyclerview variables
    public static List<tarea> task;
    public static recyclerAdapterItems adapterItems;
    //TODO: Strigns variables
    String userName;
    //Calendar
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        initialComponents();
    }

    public void initialComponents(){
        this.showDia = this.findViewById(R.id.mainShowDia);
        this.showUser = this.findViewById(R.id.mainUserShow);
        this.addTaskButton = this.findViewById(R.id.mainAddButtonTask);
        this.recyclerShowTask = this.findViewById(R.id.mainRecyclerTarea);
        this.refreshLayout = this.findViewById(R.id.swipeRefresLayout);
        this.showUserImage = this.findViewById(R.id.mainShowImageUser);
        setDarkMode();
        this.ImplementsNavegationDrawer = new NavDrawerImplementation(context, this, MainActivity.this, R.id.nav_menu_home);
        sqliteManager = new SqliteManager(context);
        sp = getSharedPreferences("MainActivitySettings", Context.MODE_PRIVATE);
        //lolll
        if (checkUserInsertDataBase()){
            requireQuitSaveModeBattery();
            this.recyclerShowTask.setLayoutManager(new LinearLayoutManager(this));
            adapterItems = new recyclerAdapterItems(getTareas(), MainActivity.this);
            recyclerShowTask.setAdapter(adapterItems);
            if (task != null && task.size() > 0) {
                for (int i = 0; i <= task.size() - 1; i++) {
                    checkDataBase(task.get(i).getId(), task.get(i).getTitulo(), task.get(i).getHora(),
                            task.get(i).getMinuto(), task.get(i).getDescripcion(), task.get(i).getHorasDesignadas(), task.get(i).getFecha());
                }
            }
            ItemTouchHelper.SimpleCallback simpleCallback =
                    new swipeTasks(0, ItemTouchHelper.LEFT, MainActivity.this, context);
            new ItemTouchHelper(simpleCallback)
                    .attachToRecyclerView(recyclerShowTask);
            ItemTouchHelper.SimpleCallback simpleCallbackRight =
                    new swipeTasks(0, ItemTouchHelper.RIGHT, MainActivity.this, context);
            new ItemTouchHelper(simpleCallbackRight)
                    .attachToRecyclerView(recyclerShowTask);
            getDaySystem(this.showDia);
            this.addTaskButton.setOnClickListener(view -> openActivityAddTask());
        }
        this.refreshLayout.setOnRefreshListener(() -> {
            initialComponents();
            refreshLayout.setRefreshing(false);
        });
    }

    //TODO: checa y establece los parametro obtenidos de la base de datos de usuarios
    private boolean checkUserInsertDataBase(){
        Cursor c = sqliteManager.queryAllRegistersUsers();
        if(c.moveToFirst()){
            userName = c.getString(1);
            if(!sp.getBoolean("isSetUser", false)){
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("isSetUser", true);
                edit.apply();
                //showAlert("Se ha Añadido El usuario :D", "Se ha añadido correctamente el usuario " + user);
            }
            showUser.setText(userName);

            try {
                Bitmap imageUser =
                        MediaStore.Images.Media.getBitmap(context.getContentResolver(),Uri.fromFile(new File(c.getString(2))));
                RoundedBitmapDrawable rdImage = RoundedBitmapDrawableFactory.create(context.getResources(), imageUser);
                rdImage.setCornerRadius(imageUser.getHeight() * imageUser.getWidth());
                showUserImage.setImageDrawable(rdImage);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return true;
        }else{
            AlertDialog.Builder build = new AlertDialog.Builder(context);
            build.setTitle("Hola Nuevo Usuario¡");
            build.setMessage("Se ha detectado que no tienes registrado tu nombre ni imagen " +
                    "Agregalos para continuar");
            build.setCancelable(false);
            build.setPositiveButton("Ok", (dialog, which) -> {
                Intent intent = new Intent(getApplicationContext(), activityAddUser.class);
                startActivity(intent);
            });
            AlertDialog al = build.create();
            al.show();
            return  false;
        }
    }

    //TODO: abre la actividad para agregar una tarea a la base de datos
    private void openActivityAddTask(){
        Intent intent = new Intent(context, activityAddTask.class);
        this.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);

    }

    @SuppressLint("SetTextI18n")
    //TODO: obtiene el dia de sistema para imprimirlo en un TextView
    private void getDaySystem(TextView view){
        Calendar calendar = Calendar.getInstance();
        String dayName = "";
        int diaN = calendar.get(Calendar.DAY_OF_WEEK);
        switch (diaN) {
            case 2:
                dayName = "Lunes";
                break;
            case 3:
                dayName = "Martes";
                break;
            case 4:
                dayName = "Miercoles";
                break;
            case 5:
                dayName = "Jueves";
                break;
            case 6:
                dayName = "Viernes";
                break;
            case 7:
                dayName = "Sabado";
                break;
            case 1:
                dayName = "Domingo";
                break;
        }
        view.setText("El dia de hoy es " + dayName);
    }

    //TODO: obtiene las tareas de la base de datos
    private List<tarea> getTareas(){
        task = new ArrayList<>();

        Cursor c;

        c = sqliteManager.queryAllRegistersTareas();
        if (c.moveToFirst()) {

            recyclerShowTask.setBackground(null);

            task.add(new tarea(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getString(4)
                    ,c.getString(5), c.getString(6)));

            while (c.moveToNext()) {
                task.add(new tarea(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getString(4)
                        ,c.getString(5), c.getString(6) ));
            }
        }else{
            Toast.makeText(context, "No hay registro", Toast.LENGTH_SHORT).show();
        }

        return task;
    }

    //TODO: generate a new WorkManager
    @SuppressLint("SimpleDateFormat")
    private void checkDataBase(int id, String titulo, int hora, int minuto, String descripcion, String tiempoDesignado, String fecha){

        setCalendar(fecha,hora, minuto);

        String tag = String.valueOf(id);
        long alertTime = cal.getTimeInMillis() - System.currentTimeMillis();
        int random = (int) (Math.random() * 50 + 1);
        Data data = saveData(titulo,descripcion, random, tiempoDesignado);

        WorkManager.saveNoti(alertTime, data, tag, context);
        //Toast.makeText(getApplicationContext(), alertTime + " milis , hourOfdataDase " + hora + ":" + minuto , Toast.LENGTH_SHORT).show();
    }

    //TODO: establece el calendario para que esto no genere errores de tiempo
    private void setCalendar(String fecha, int hora, int minuto){

        String subStrignDay = fecha.substring(0, 1);
        String subStrignMonth = fecha.substring(3,4);

        String subStrignyear = fecha.substring(6,10);

        if(subStrignDay.equals("0")){
            subStrignDay = fecha.substring(1,2);
        }else{
            subStrignDay = fecha.substring(0,2);
        }

        if(subStrignMonth.equals("0")){
            subStrignMonth = fecha.substring(4,5);
        }else{
            subStrignMonth = fecha.substring(3,5);
        }

        int day = Integer.parseInt(subStrignDay);
        int month = Integer.parseInt(subStrignMonth);
        int year = Integer.parseInt(subStrignyear);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);

    }

    //TODO: Create a new Object Data
    private Data saveData(String title, String details, int id, String timeDesignate){
        return new Data.Builder()
                .putString("titleTask", title)
                .putString("descriptionTask",details)
                .putInt("idTask",id)
                .putString("designado", timeDesignate).build();
    }

    //TODO: borra la alerta del workmanager
    public void deleteWorkManagerFromTask(String tag){
        androidx.work.WorkManager.getInstance(this).cancelAllWorkByTag(tag);
    }

    //TODO: establece el darkMode
    private void setDarkMode(){
        SetDarkMode darkMode = new SetDarkMode(context, this);
        ArrayList<TextView> views = new ArrayList<>();
        views.add(showDia);
        views.add(showUser);
        darkMode.setDarkModeTextViews(views);
    }

    //TODO pedir al usuario que desactive la gestion automatica de la app para su correcto funcionamiento
    private void requireQuitSaveModeBattery(){
        SharedPreferences sp = getSharedPreferences("MainActivitySettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("totalAccess", false)){

            Intent[] POWERMANAGER_INTENTS = {
                    new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
                    new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
                    new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
                    new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
                    new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
                    new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                    new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
                    new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
                    new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
                    new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
                    new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
                    new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
                    new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
                    new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
            };

            for (Intent intent : POWERMANAGER_INTENTS){
                if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    AlertDialog.Builder build = new AlertDialog.Builder(context);
                    build.setTitle("Aviso del la aplicacion");
                    build.setMessage("Necesitamos que desabilites la gestion automatica de bateria \n para su correcto funcionamiento de la misma");
                    build.setCancelable(false);
                    build.setNegativeButton("No gestionar", (dialogInterface, i) -> {
                        AlertDialog.Builder build1 = new AlertDialog.Builder(context);
                        build1.setTitle("Aviso del la aplicacion");
                        build1.setCancelable(false);
                        build1.setMessage("Al no gestionarla la aplicacion puede generar una mala experiencia de usuario");
                        build1.setNeutralButton("Esta Bien", (dialogInterface1, i1) -> dialogInterface1.dismiss());
                        AlertDialog dialog = build1.create();
                        dialog.show();
                        dialogInterface.dismiss();
                    });
                    build.setPositiveButton("Ir a ello", (dialog, il) -> {
                        startActivity(intent);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putBoolean("totalAccess", true);
                        edit.apply();
                    });

                    AlertDialog dialog = build.create();
                    dialog.show();

                    break;
                }
            }

        }

    }

    private void filterFromUp(){
        Collections.sort(task, (tarea, t1) -> {
            Integer tarea1 = tarea.getMinuto();
            Integer tarea2 = t1.getMinuto();
            return tarea2.compareTo(tarea1);
        });
        Collections.sort(task, (tarea, t1) -> {
            Integer tarea1 = tarea.getHora();
            Integer tarea2 = t1.getHora();
            return tarea2.compareTo(tarea1);
        });
        adapterItems.setNewDataThree(task);
        adapterItems.notifyDataSetChanged();
    }

    private void filterFromBottom() {
        Collections.sort(task, (tarea, t1) -> tarea.getHora() - t1.getHora() );
        adapterItems.setNewDataThree(task);
        adapterItems.notifyDataSetChanged();
    }

//TODO: --------metodos que se sobreescriben para el uso de la app-------------

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        this.ImplementsNavegationDrawer.onNavigationItemSelected(item);
        return true;
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof recyclerAdapterItems.ViewHolder) {

            if(direction == ItemTouchHelper.LEFT){
                String id = String.valueOf(getTareas().get(viewHolder.getAdapterPosition()).getId());
                adapterItems.removeItem(viewHolder.getAdapterPosition());
                deleteWorkManagerFromTask(id);
                sqliteManager.deleteTareas(Integer.parseInt(id));
                if(TimerService.instancia != null){
                    new TimerService().stopBecauseDeleteTask(context);
                }
                stopService(new Intent(getApplicationContext(), TimerService.class));
                getTareas();

            }else if(direction == ItemTouchHelper.RIGHT){

                int id_tarea = getTareas().get(viewHolder.getAdapterPosition()).getId();
                String titulo_tarea = getTareas().get(viewHolder.getAdapterPosition()).getTitulo();
                int hora = getTareas().get(viewHolder.getAdapterPosition()).getHora();
                int minuto = getTareas().get(viewHolder.getAdapterPosition()).getMinuto();
                String des_tarea = getTareas().get(viewHolder.getAdapterPosition()).getDescripcion();
                String designado_tarea = getTareas().get(viewHolder.getAdapterPosition()).getHorasDesignadas();
                String fecha_tarea = getTareas().get(viewHolder.getAdapterPosition()).getFecha();
                deleteWorkManagerFromTask(String.valueOf(id_tarea));
                Intent i = new Intent(context, activityEditTask.class);
                i.putExtra("id_tarea", id_tarea);
                i.putExtra("titulo", titulo_tarea);
                i.putExtra("hora", hora);
                i.putExtra("minuto", minuto);
                i.putExtra("des", des_tarea);
                i.putExtra("fecha", fecha_tarea);
                i.putExtra("designado", designado_tarea);
                i.putExtra("position", position);
                if(TimerService.instancia != null){
                    new TimerService().stopBecauseDeleteTask(context);
                }
                stopService(new Intent(getApplicationContext(), TimerService.class));
                this.finish();
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(this.ImplementsNavegationDrawer.getDrawerLayout().isDrawerOpen(GravityCompat.START)){
            ImplementsNavegationDrawer.getDrawerLayout().closeDrawer(GravityCompat.START);
        }else{
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.three_dots_main, menu);
        String typeFilter = sp.getString("typeFilter", "NONE");
        if(typeFilter != null){
            switch (typeFilter) {
                case "+A-":
                    menu.findItem(R.id.filtMenu).setChecked(true);
                    filterFromUp();
                    break;
                case "-A+":
                    menu.findItem(R.id.filtrarMenor).setChecked(true);
                    filterFromBottom();
                    break;
                case "NONE":
                    menu.findItem(R.id.filtrarNinguno).setChecked(true);
                    task = getTareas();
                    adapterItems.setNewDataThree(task);
                    adapterItems.notifyDataSetChanged();
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = sp.edit();
        switch (item.getItemId()){
            case R.id.filtMenu:
                item.setChecked(true);
                filterFromUp();
                editor.putString("typeFilter","+A-");
                break;
            case R.id.filtrarMenor:
                item.setChecked(true);
                filterFromBottom();
                adapterItems.setNewDataThree(task);
                adapterItems.notifyDataSetChanged();
                editor.putString("typeFilter","-A+");
                break;
            case R.id.filtrarNinguno:
                item.setChecked(true);
                task = getTareas();
                adapterItems.setNewDataThree(task);
                adapterItems.notifyDataSetChanged();
                editor.putString("typeFilter", "NONE");
                break;
        }
        editor.apply();
        return super.onOptionsItemSelected(item);
    }
    //TODO: fina lde los metodos sobre escritos
}