package com.Mc256Design.mistareasdeldia.ClassRequired;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.Mc256Design.mistareasdeldia.R;

public class DialogDesignedTime extends DialogFragment {

    public interface interfazDesignado{

        void getTime(String time);

    }

    private interfazDesignado intefaz;

    private int time = 0;



    public DialogDesignedTime(Context context, interfazDesignado activist){


        intefaz = activist;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_fragment_designed);

        final EditText dato = dialog.findViewById(R.id.showDato);

        final Button subir = dialog.findViewById(R.id.mayor);
        final Button bajar = dialog.findViewById(R.id.menor);
        final Button aceptar = dialog.findViewById(R.id.aceptar);

        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time = time + 30;
                dato.setText(String.valueOf(time));
            }
        });

        bajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time == 0){
                    time = 0;
                }else{
                    time = time - 30;
                    dato.setText(String.valueOf(time));

                }
            }
        });
        dialog.show();

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intefaz.getTime(dato.getText().toString());
                dialog.dismiss();
            }
        });




    }

}
