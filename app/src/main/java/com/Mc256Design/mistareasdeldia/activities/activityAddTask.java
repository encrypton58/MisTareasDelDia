package com.Mc256Design.mistareasdeldia.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.google.android.material.textfield.TextInputLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.Mc256Design.mistareasdeldia.ClassRequired.DialogDesignedTime;
import com.Mc256Design.mistareasdeldia.ClassRequired.SimpleAlertDialog;
import com.Mc256Design.mistareasdeldia.MainActivity;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.SqliteControl.SqliteManager;
import com.wdullaer.materialdatetimepicker.time.Timepoint;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class activityAddTask extends AppCompatActivity implements DialogDesignedTime.interfazDesignado
        , TimePickerDialog.OnTimeSetListener{

    //TODO: widgets
    TextView showTime, showDesigned, titleAddTask;
    TextInputLayout layoutTitle, layoutDescription;
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
        this.titleAddTask = this.findViewById(R.id.addTasktTtle);
        this.layoutTitle = this.findViewById(R.id.addTaskLayoutTitle);
        this.layoutDescription = this.findViewById(R.id.addTaskLayoutDescription);
        this.showTime = this.findViewById(R.id.addTaskShowTime);
        this.showDesigned = this.findViewById(R.id.addTaskshowDesigned);
        this.pickTime = this.findViewById(R.id.addTaskPickTime);
        this.pickDesigned = this.findViewById(R.id.addTaskPickDesigned);
        this.addTask = this.findViewById(R.id.addTaskAdd);
        setDarkMode();
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


        TimePickerDialog picker = TimePickerDialog.newInstance(activityAddTask.this, hourSystem, minuteSystem, false);
        Timepoint[] times = generateTimePoints();
        if(times != null){
            picker.setDisabledTimes(times);
        }
        picker.show(this.getSupportFragmentManager(), "picker");


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

        if(Objects.requireNonNull(this.layoutTitle.getEditText()).getText().toString().isEmpty()
                || Objects.requireNonNull(this.layoutDescription.getEditText()).getText().toString().isEmpty()){
            if (this.layoutTitle.getEditText().getText().toString().isEmpty()){
                this.layoutTitle.setError("No puedes dejar vacio el titulo");
            }
            if(Objects.requireNonNull(this.layoutDescription.getEditText()).getText().toString().isEmpty()){
                this.layoutDescription.setError("No puedes dejar Vacio la descripcion");
            }
            return true;

        }else{
            this.titleStrign = this.layoutTitle.getEditText().getText().toString();
            this.descriptionStrign = this.layoutDescription.getEditText().getText().toString();
            return false;
        }

    }

    //TODO: checa la hora y el tiempo designado que no sean vacios y devuelve un boleano que representa un error
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

    //TODO: setea la vista en DarkMode
    private void setDarkMode(){
        SetDarkMode darkMode = new SetDarkMode(context, this);
        ArrayList<TextView> views = new ArrayList<>();
        views.add(0, showDesigned);
        views.add(1,showTime);
        darkMode.setDarkModeTextViews(views);
        darkMode.setDarkModeNavegationBar(getWindow());
    }

    //TODO: genera los timePoints que obtiene de las tareas obtenidas
    private Timepoint[] generateTimePoints(){
        Cursor c = sqliteManager.queryAllRegistersTareas();

        if(c.getCount() >= 1){
            int[] horas = new int[c.getCount()];
            int[] minutos = new int[c.getCount()];
            int[] designados = new int[c.getCount()];

            if(c.moveToFirst()){
                horas[0] = c.getInt(2);
                minutos[0] = c.getInt(3);
                designados[0] = Integer.parseInt(c.getString(6)) + 1;
            }
            for (int i = 1; c.moveToNext(); i++){
                horas[i] = c.getInt(2);
                minutos[i] = c.getInt(3);
                designados[i] = Integer.parseInt(c.getString(6)) + 1;
            }
            int sumaDesignados = 0;
            for(int k = 0; k <= designados.length - 1; k++){
                sumaDesignados = sumaDesignados + designados[k];
            }
            Timepoint[] times = new Timepoint[sumaDesignados];
            int indexPosition = 0;
            int index = 0;
            int indexTimes = 0;
            int minutosValue = -1;

            while(indexTimes != times.length){
                if(indexPosition <= horas.length - 1){
                    while(minutosValue != minutos[indexPosition] + designados[indexPosition]){
                        if(minutosValue == -1 || index != indexPosition){
                            minutosValue = minutos[indexPosition];
                            times[indexTimes] = new Timepoint(horas[indexPosition], minutosValue);
                            index = indexPosition;
                        }else{
                            times[indexTimes] = new Timepoint(horas[indexPosition], minutosValue);
                        }
                        indexTimes++;
                        minutosValue++;
                    }
                    index = indexPosition;
                    indexPosition++;
                }
            }
            return times;
        }else{
            return null;
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