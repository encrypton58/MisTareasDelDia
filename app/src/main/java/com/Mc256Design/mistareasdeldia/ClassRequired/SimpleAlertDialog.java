package com.Mc256Design.mistareasdeldia.ClassRequired;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SimpleAlertDialog  extends AlertDialog.Builder {

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

}
