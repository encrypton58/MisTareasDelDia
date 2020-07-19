package com.Mc256Design.mistareasdeldia.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class activityEditTask extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DialogDesignedTime.interfazDesignado{

    //TODO: Widgets
    TextView showTitle, showHour, showDescription, showDate, showDesigned;
    TextView typeTitle, typeDescription, typeDesigned, typeHour, typeDate;
    EditText newNameTask, newDescriptionTaks;
    Button newSetTime, newSetDesigned, EditTaks;
    //TODO: context
    Context context;
    Bundle datos;
    //TODO: Strings
    String designed = "empty", title, description, date;
    //TODO: integers
    int hour = -1;
    int minute = -1;
    int hourSystem;
    int minuteSystem;
    //TODO: Instancias de clases
    SqliteManager sqliteManager;
    Calendar cal;
    //TODO: booleans
    boolean isSetTomorrow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        context = this;
        datos = getIntent().getExtras();
        initialElements();
    }

    @SuppressLint("SetTextI18n")
    private void initialElements(){
        messageInitial();
        this.showTitle = this.findViewById(R.id.editTaskShowTitleTask);
        this.showDescription = this.findViewById(R.id.editTaskShowDescriptionTaks);
        this.showHour = this.findViewById(R.id.editTaskShowTimeTask);
        this.showDesigned = this.findViewById(R.id.editTaskShowTimeDesigned);
        this.showDate = this.findViewById(R.id.editTaskShowDateTask);
        this.newNameTask = this.findViewById(R.id.editTaskEditTitulo);
        this.newSetDesigned = this.findViewById(R.id.editTaskEditDesigned);
        this.newDescriptionTaks = this.findViewById(R.id.editTaskEditDescription);
        this.newSetTime = this.findViewById(R.id.editTaskEditTime);
        this.EditTaks = this.findViewById(R.id.editTaskEditTask);
        this.typeTitle = this.findViewById(R.id.typeTitleEditarTarea3);
        this.typeDescription = this.findViewById(R.id.typeDescripcionEditarTarea2);
        this.typeDesigned = this.findViewById(R.id.typeDesignadoEditarTarea2);
        this.typeDate = this.findViewById(R.id.typeFechaEditarTarea);
        this.typeHour = this.findViewById(R.id.typeHoraEditarTarea2);
        this.setDarkMode();
        cal = Calendar.getInstance();
        this.showTitle.setText(datos.getString("titulo"));
        this.showDescription.setText(datos.getString("des"));
        this.showHour.setText(datos.getInt("hora") + ":" + datos.getInt("minuto"));
        this.showDate.setText(datos.getString("designado"));
        this.showDate.setText(datos.getString("fecha"));
        sqliteManager = new SqliteManager(context);


        this.newSetTime.setOnClickListener((push) -> {
            timePick();
            Toast.makeText(context, "Time seleccionado", Toast.LENGTH_SHORT).show();

        });

        this.newSetDesigned.setOnClickListener((push) -> new DialogDesignedTime(context, activityEditTask.this));

        this.EditTaks.setOnClickListener((push -> updateTask()));

    }

    private void messageInitial(){
        new SimpleAlertDialog(context, "Aviso De Actualizacion", "si queires que algunos de " +
                "tus datos poesteriores sean guardados simplemente dejalos vacios actualiza con cuidado");
    }

    private void updateTask(){
        checkInputs();
        setDateFormat();
        checkTimeAndDesigned();
        createUpdateInBD();
        startActivity(new Intent(context, MainActivity.class));
        finish();

    }

    private void timePick(){

    }

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
                        timePick();
                    });
            AlertDialog alertDialog = build.create();
            alertDialog.show();
        }else{
            Toast.makeText(context, "No se cumple la condicion establecida", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInputs(){
        if(newNameTask.getText().toString().isEmpty() && newDescriptionTaks.getText().toString().isEmpty()){
            title = datos.getString("titulo");
            description = datos.getString("des");
        }else{
            if(!newNameTask.getText().toString().isEmpty()){
                title = newNameTask.getText().toString();
            }else{
                title = datos.getString("titulo");
            }
            if (!newDescriptionTaks.getText().toString().isEmpty()){
                description = newDescriptionTaks.getText().toString();
            }else{
                description = datos.getString("des");
            }

        }
    }

    private void checkTimeAndDesigned(){
        if(hour <= -1){
            hour = datos.getInt("hora");
        }
        if (minute <= -1){
            minute = datos.getInt("minuto");
        }
        if(designed.equals("empty")){
            designed = datos.getString("designado");
        }

    }

    private void createUpdateInBD(){
        sqliteManager.updateDataTareas(datos.getInt("id_tarea") , title, hour, minute, description, date, designed);
    }

    private void setDateFormat(){

        int day;
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        String dayString;
        String mothString;

        if(isSetTomorrow){
            day = cal.get(Calendar.DAY_OF_MONTH) + 1;
        }else{
            day = cal.get(Calendar.DAY_OF_MONTH);
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

        date = dayString + "-" + mothString +"-"+ year;

    }

    private void setDarkMode(){
        SetDarkMode darkMode = new SetDarkMode(context, activityEditTask.this);
        ArrayList<EditText> arrayText = new ArrayList<>();
        arrayText.add(newDescriptionTaks);
        arrayText.add(newNameTask);
        darkMode.setDarkModeEditText(arrayText);
        ArrayList<TextView> arrayViews = new ArrayList<>();
        arrayViews.add(showTitle);
        arrayViews.add(showDate);
        arrayViews.add(showDescription);
        arrayViews.add(showHour);
        arrayViews.add(showDesigned);
        arrayViews.add(typeTitle);
        arrayViews.add(typeDescription);
        arrayViews.add(typeDesigned);
        arrayViews.add(typeDate);
        arrayViews.add(typeHour);
        darkMode.setDarkModeTextViews(arrayViews);

    }

    //TODO: metodos sobre escritos por la aplicacion

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        SetTomorrowTask(hour, minute, hourSystem , minuteSystem);
        hour = hourOfDay;
       this.minute = minute;
       CharSequence texto = "La nueva hora es: " + hour + ":" + this.minute;
       showHour.setText(texto);
    }

    @Override
    public void getTime(String time) {
        designed = time;
        CharSequence charSequence = "El tiempo designado es: " + designed + "mins.";
        showDesigned.setText(charSequence);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_rigth);
    }
}