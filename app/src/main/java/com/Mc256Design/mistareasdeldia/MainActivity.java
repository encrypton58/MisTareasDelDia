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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
          swipeTasks.recyclerItemTouchHelperListener{

    //TODO: widgets
    TextView showDia, showUser, showNoTask;
    FloatingActionButton addTaskButton;
    RecyclerView recyclerShowTask;
    ImageView showUserImage;
    SwipeRefreshLayout refreshLayout;
    //TODO: instance of class
    NavDrawerImplementation ImplementsNavegationDrawer;
    SqliteManager sqliteManager;
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
        this.showNoTask = this.findViewById(R.id.mainNoWorksText);
        setDarkMode();
        this.ImplementsNavegationDrawer = new NavDrawerImplementation(context, this, MainActivity.this, R.id.nav_menu_home);
        sqliteManager = new SqliteManager(context);

        if (checkUserInsertDataBase()){
            this.recyclerShowTask.setLayoutManager(new LinearLayoutManager(this));
            adapterItems = new recyclerAdapterItems(getTareas(), MainActivity.this);
            recyclerShowTask.setAdapter(adapterItems);
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
            SharedPreferences sp = getSharedPreferences("userIsSet", Context.MODE_PRIVATE);
            if(!sp.getBoolean("isSetUser", false)){
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("isSetUser", true);
                edit.apply();
                //showAlert("Se ha Añadido El usuario :D", "Se ha añadido correctamente el usuario " + user);
            }
            showUser.setText(userName);
            byte[] image = c.getBlob(2);
            Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0 ,image.length);
            RoundedBitmapDrawable rdUserIMagen = RoundedBitmapDrawableFactory.create(getResources(), convertImage);
            rdUserIMagen.setCornerRadius(convertImage.getHeight() * convertImage.getWidth());
            showUserImage.setImageDrawable(rdUserIMagen);
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
            task.add(new tarea(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getString(4)
                    ,c.getString(5), c.getString(6)));

            //TODO: uso del metodo checkData base
            checkDataBase(c.getInt(0), c.getString(1), c.getInt(2),
                    c.getInt(3), c.getString(4), c.getString(6), c.getString(5));
            showNoTask.setVisibility(View.INVISIBLE);

            while (c.moveToNext()) {
                task.add(new tarea(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getString(4)
                        ,c.getString(5), c.getString(6) ));
                //TODO: uso del metodo checkData base
                checkDataBase(c.getInt(0), c.getString(1), c.getInt(2),
                        c.getInt(3), c.getString(4), c.getString(6), c.getString(5));
            }
        } else {
            Toast.makeText(context, "No hay Registros", Toast.LENGTH_SHORT).show();
            showNoTask.setVisibility(View.VISIBLE);
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
        WorkManager.saveNoti(alertTime, data, tag);
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
        views.add(showNoTask);
        views.add(showUser);
        darkMode.setDarkModeTextViews(views);
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
                int pos = position;
                deleteWorkManagerFromTask(String.valueOf(id_tarea));
                Intent i = new Intent(context, activityEditTask.class);
                i.putExtra("id_tarea", id_tarea);
                i.putExtra("titulo", titulo_tarea);
                i.putExtra("hora", hora);
                i.putExtra("minuto", minuto);
                i.putExtra("des", des_tarea);
                i.putExtra("fecha", fecha_tarea);
                i.putExtra("designado", designado_tarea);
                i.putExtra("position", pos);
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
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_rigth);
    }

    //TODO: fina lde los metodos sobre escritos

}