package com.Mc256Design.mistareasdeldia.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Mc256Design.mistareasdeldia.R;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class activityEditTask extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    //TODO: Widgets
    TextView showTitle, showHour, showDescription, showDate, showDesigned;
    EditText newNameTask, newDescriptionTaks;
    Button newSetTime, newSetDesigned, EditTaks;
    //TODO: context
    Context context;
    Bundle datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        context = this;
        datos = getIntent().getExtras();
        initialElements();
    }

    private void initialElements(){
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

        this.showTitle.setText(datos.getString("titulo"));
        this.showDescription.setText(datos.getString("des"));
        this.showHour.setText(datos.getInt("hora") + ":" + datos.getInt("minuto"));
        this.showDate.setText(datos.getString("designado"));
        this.showDate.setText(datos.getString("fecha"));



        this.newSetTime.setOnClickListener((marc) -> {
            timePick();
            Toast.makeText(context, "Time seleccionado", Toast.LENGTH_SHORT).show();
                });

    }

    private void timePick(){
        Calendar cal = Calendar.getInstance();
        TimePickerDialog tp = TimePickerDialog.newInstance(activityEditTask.this, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE) , false);
        tp.show(getSupportFragmentManager(), "");

    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }
}