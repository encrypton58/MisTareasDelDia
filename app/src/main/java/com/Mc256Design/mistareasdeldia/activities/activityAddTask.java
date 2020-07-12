package com.Mc256Design.mistareasdeldia.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;

import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.Mc256Design.mistareasdeldia.ClassRequired.DialogDesignedTime;
import com.Mc256Design.mistareasdeldia.ClassRequired.SimpleAlertDialog;
import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;

import java.util.Calendar;

public class activityAddTask extends AppCompatActivity implements DialogDesignedTime.interfazDesignado
, TimePickerDialog.OnTimeSetListener{

    //TODO: widgets
    EditText title, description;
    TextView showTime, showDesigned;
    Button pickTime, pickDesigned, addTask;
    //TODO: context
    Context context;
    //TODO: Strings
    String designed= "empty", titleStrign, fecha, descriptionStrign;
    //TODO: integers
    int hourSet = -1;
    int minutoSet = -1;
    int minuteSystem;
    int hourSystem;
    //TODO: intancia de clases
    SqliteManager sqliteManager;
    SetDarkMode darkMode;
    Calendar c;
    //TODO: boleanos
    boolean isSetTomorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        this.context = this;
        initialElements();

    }

    //TODO: inicia todos los componetes de la UI
    private void initialElements(){
        this.title = this.findViewById(R.id.addTaskTitle);
        this.description = this.findViewById(R.id.addTaskDescription);
        this.showTime = this.findViewById(R.id.addTaskShowTime);
        this.showDesigned = this.findViewById(R.id.addTaskshowDesigned);
        this.pickTime = this.findViewById(R.id.addTaskPickTime);
        this.pickDesigned = this.findViewById(R.id.addTaskPickDesigned);
        this.addTask = this.findViewById(R.id.addTaskAdd);
        this.darkMode = new SetDarkMode(context, this);
        sqliteManager = new SqliteManager(context);
        c = Calendar.getInstance();
        this.pickTime.setOnClickListener(view -> TimePicker());

        this.pickDesigned.setOnClickListener(view -> dialogDesigned());

        this.addTask.setOnClickListener(view -> {
            if(!checkInputsNoEmpty()){
                setDateFormat();
                if(!checkTimeAndDesigned()){
                    sqliteManager.insertDataTareas(titleStrign,hourSet,
                            minutoSet,descriptionStrign,fecha,designed);
                    Toast.makeText(context, "se creo el registro", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_rigth);
                }
            }
        });

    }

    //TODO: escoje la hora a la cual iniciara la tarea
    @SuppressLint("SetTextI18n")
    private void TimePicker(){

        Calendar cal = Calendar.getInstance();

         hourSystem = cal.get(Calendar.HOUR_OF_DAY);
         minuteSystem = cal.get(Calendar.MINUTE);


        Cursor c = sqliteManager.queryAllRegistersTareas();
         if(c.moveToLast()){
             int hora = c.getInt(2);
             int minuto = c.getInt(3);
             int designado = Integer.parseInt(c.getString(6));

             minuto = minuto + designado;

             if (minuto >= 60){
                 hora += 1;
                 minuto = minuto - 60;
             }

             TimePickerDialog tp = TimePickerDialog.newInstance(activityAddTask.this,hora, minuto,false);
             tp.setMinTime(hora, minuto, 0);
             tp.show(getSupportFragmentManager(), "Restringido");

         }else{
             TimePickerDialog tp = TimePickerDialog.newInstance(activityAddTask.this, hourSystem, minuteSystem, false);
             tp.show(getSupportFragmentManager(),"Chido ");
         }



    }

    //TODO: establece si el dia de mañana se ejecutara la tarea
    private void SetTomorrowTask(int hour, int minute, int hourSystem, int minuteSystem){
        if(hour <= hourSystem && minute <= minuteSystem){
            AlertDialog.Builder build = new AlertDialog.Builder(context)
                    .setTitle("Aviso")
                    .setMessage("la alarma se establecera a esa hora mañana")
                    .setPositiveButton("Aceptar", (dialogInterface, i) -> {
                        isSetTomorrow = true;
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Denegar", (dialogInterface, i) -> {
                        isSetTomorrow = false;
                        dialogInterface.dismiss();
                        TimePicker();
                    });
            AlertDialog alertDialog = build.create();
            alertDialog.show();
        }else{
            Toast.makeText(context, "No se cumple la condicion establecida", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: establece la fecha de alarma de la tarea
    private void setDateFormat(){

        int day;
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String dayString;
        String mothString;

        if(isSetTomorrow){
            day = c.get(Calendar.DAY_OF_MONTH) + 1;
        }else{
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        if(!(day >= 10)){
            dayString = "0"+day;
        }else {
            dayString = String.valueOf(day);
        }
        if(!(month >= 10)){
            mothString = "0"+ month;
        }else{
            mothString = String.valueOf(month);
        }

        fecha = dayString + "-" + mothString +"-"+ year;


    }

    //TODO: crea el dialog para mostrar el designed dialog
    private void dialogDesigned(){
        new DialogDesignedTime(context,activityAddTask.this);
    }

    //TODO: checa los inputs y devuelbe un booleano segun su estado
    private boolean checkInputsNoEmpty(){
        if(this.title.getText().toString().isEmpty() || this.description.getText().toString().isEmpty()){
            if (this.title.getText().toString().isEmpty()){
                new SimpleAlertDialog(context,"Error de entrada","No puedes dejar vacio el titulo");
            }
            if(this.description.getText().toString().isEmpty()){
                new SimpleAlertDialog(context,"Error de entrada","No puedes dejar Vacio la descripcion");
            }
            return true;

        }else{
            this.titleStrign = this.title.getText().toString();
            this.descriptionStrign = description.getText().toString();
            return false;
        }

    }

    //TODO: checa la hora y el tiempo designado que no sean vacios
    private boolean checkTimeAndDesigned(){
        if(designed.equals("empty") || hourSet == -1 ){
            if(designed.equals("empty")){
                new SimpleAlertDialog(context, "Error de Tiempo Designado", "No puede dejar el tiempo designado vacio");
            }
            if(hourSet == -1 ){
                new SimpleAlertDialog(context,"Error de Tiempo", "No pudes dejar vacio el tiempo de inicio");
            }
            return true;
        }else{
            return false;
        }
    }

    //TODO: Metodos sobre escritos por las clases

    @SuppressLint("SetTextI18n")
    @Override
    public void getTime(String time) {
        this.designed = time;
        this.showDesigned.setText("tiempo designado: " + this.designed + "mins");
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        hourSet = hourOfDay;
        minutoSet = minute;
        SetTomorrowTask(hourOfDay,minute, hourSystem, minuteSystem);
        activityAddTask.this.showTime.setText("Hora de inicio: " +  hourSet + ":" + minutoSet);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_rigth);
    }
}