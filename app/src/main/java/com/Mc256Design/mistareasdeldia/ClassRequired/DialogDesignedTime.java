package com.Mc256Design.mistareasdeldia.ClassRequired;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.Mc256Design.mistareasdeldia.R;

import java.util.Objects;

public class DialogDesignedTime extends DialogFragment {

    public interface interfazDesignado{
        void getTime(String time);
    }

    private interfazDesignado intefaz;
    private int time = 0;
    private Context context;
    private int ID_BUTTON_ENABLED = 5555;
    private Button selected5, selected10;

    public DialogDesignedTime(Context context, interfazDesignado activist){

        this.context = context;
        this.intefaz = activist;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_fragment_designed);
        final EditText dato = dialog.findViewById(R.id.showDato);
        final Button subir = dialog.findViewById(R.id.mayor);
        final Button bajar = dialog.findViewById(R.id.menor);
        final Button aceptar = dialog.findViewById(R.id.aceptar);
        this.selected5 = dialog.findViewById(R.id.selected5);
        this.selected10 = dialog.findViewById(R.id.selected10);
        checkStateOfButtons();

        selected5.setOnClickListener((push) ->{
           ID_BUTTON_ENABLED = 5555;
           checkStateOfButtons();
        });

        selected10.setOnClickListener( (push) ->{
            ID_BUTTON_ENABLED = 1010;
            checkStateOfButtons();
        });

        subir.setOnClickListener(v -> {
            if(ID_BUTTON_ENABLED == 5555){
                time = time + 5;
            }else if(ID_BUTTON_ENABLED == 1010){
                time = time + 10;
            }
            dato.setText(String.valueOf(time));
        });

        bajar.setOnClickListener(v -> {
            if(time <= 0){
                time = 0;
            }else{
                if(ID_BUTTON_ENABLED == 5555){
                    time = time - 5;
                }else if(ID_BUTTON_ENABLED == 1010){
                    time = time - 10;
                    if(time < 0){
                        time = 0;
                    }
                }
            }

            dato.setText(String.valueOf(time));

        });
        dialog.show();

        aceptar.setOnClickListener(v -> {
            if(dato.getText().toString().isEmpty()){
                intefaz.getTime("0");
            }else{
                intefaz.getTime(dato.getText().toString());
            }
            dialog.dismiss();
        });
    }

    private void checkStateOfButtons(){
        if(ID_BUTTON_ENABLED == 5555){
            Drawable background = ContextCompat.getDrawable(context,R.drawable.dialog_designed_background_button);
            ColorFilter color = new
                    LightingColorFilter(Color.parseColor("#00B398"), 0);
            Objects.requireNonNull(background).setColorFilter(color);
            selected10.setBackground(ContextCompat.getDrawable(context,R.drawable.dialog_designed_background_button));
            selected10.setElevation(20f);
            selected10.setScaleY(1);
            selected10.setScaleX(1);

            selected5.setScaleX(0.8f);
            selected5.setScaleY(0.8f);
            selected5.setBackground(background);
        }else if(ID_BUTTON_ENABLED == 1010){
            Drawable background = ContextCompat.getDrawable(context,R.drawable.dialog_designed_background_button);
            ColorFilter color = new
                    LightingColorFilter(Color.parseColor("#00B398"), 0);
            Objects.requireNonNull(background).setColorFilter(color);
            selected5.setBackground(ContextCompat.getDrawable(context,R.drawable.dialog_designed_background_button));
            selected5.setElevation(20f);
            selected5.setScaleX(1.0f);
            selected5.setScaleY(1);

            selected10.setScaleX(0.8f);
            selected10.setScaleY(0.8f);
            selected10.setBackground(background);
        }
    }

}
