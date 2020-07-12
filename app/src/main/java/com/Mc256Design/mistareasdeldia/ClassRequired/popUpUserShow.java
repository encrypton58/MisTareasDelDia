package com.Mc256Design.mistareasdeldia.ClassRequired;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.fragment.app.DialogFragment;

import com.Mc256Design.mistareasdeldia.R;

import java.util.Objects;

public class popUpUserShow extends DialogFragment {

    public popUpUserShow(Context context, RoundedBitmapDrawable rd, String name){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_fragment_popup_user);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        TextView user = dialog.findViewById(R.id.dialogUserName);
        ImageView image = dialog.findViewById(R.id.dialogUserImage);
        Button isOk = dialog.findViewById(R.id.dialogButtonOk);

        image.setImageDrawable(rd);
        user.setText(name);

        isOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

}
