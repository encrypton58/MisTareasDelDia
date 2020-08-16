package com.Mc256Design.mistareasdeldia.ClassRequired;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;

public class SimpleAlertDialog  extends AlertDialog.Builder {

    public interface setonClickListener{
        void positiveDialog();
    }

    public setonClickListener listener;

    public SimpleAlertDialog(Context context,String title, String message){
        super(context);
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);
        this.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog al = this.create();
        al.show();

    }

    public SimpleAlertDialog(Context context, setonClickListener listener1, String title, String message,
                             String textPositiveButton, @Nullable String textNegativeButton){
        super(context);
        this.setTitle(title);
        this.listener = listener1;
        this.setMessage(message);
        this.setCancelable(false);
        this.setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.positiveDialog();
                dialogInterface.dismiss();
            }
        });

    }


}
