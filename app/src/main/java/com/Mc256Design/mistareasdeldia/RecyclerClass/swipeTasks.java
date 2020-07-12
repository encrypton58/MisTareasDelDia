package com.Mc256Design.mistareasdeldia.RecyclerClass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.Mc256Design.mistareasdeldia.R;

import java.util.Objects;

public class swipeTasks extends ItemTouchHelper.SimpleCallback {
    recyclerItemTouchHelperListener listener;
    Context context;

    public swipeTasks(int dragDirs, int swipeDirs,
                       recyclerItemTouchHelperListener listener, Context context) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){
            View foregroudView = ((recyclerAdapterItems.ViewHolder) viewHolder).layoutABorrar;
            getDefaultUIUtil().onSelected(foregroudView);

        }
    }

    boolean swiping = false;

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX,
                                float dY, int actionState, boolean isCurrentlyActive) {

        View foregroundView  = ((recyclerAdapterItems.ViewHolder) viewHolder).layoutABorrar;
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX,dY,actionState,isCurrentlyActive);

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0){

            Drawable dr = context.getDrawable(R.drawable.item_view_backgroud_swipe);
            int colorRGB = Color.rgb(255, 0, 0);
            ColorFilter color = new LightingColorFilter(colorRGB, colorRGB);
            Objects.requireNonNull(dr).setColorFilter(color);
            ((recyclerAdapterItems.ViewHolder) viewHolder).borrarEditar.setBackground(dr);
            ((recyclerAdapterItems.ViewHolder) viewHolder).editarTextItem.setVisibility(View.INVISIBLE);
            ((recyclerAdapterItems.ViewHolder) viewHolder).editarIcon.setVisibility(View.INVISIBLE);
            swiping = isCurrentlyActive;

        }else if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX > 0){
            Drawable dr = context.getDrawable(R.drawable.item_view_backgroud_swipe);
            int colorRGB = Color.rgb(0, 255, 0);
            ColorFilter color = new LightingColorFilter(colorRGB, colorRGB);
            Objects.requireNonNull(dr).setColorFilter(color);
            ((recyclerAdapterItems.ViewHolder) viewHolder).borrarEditar.setBackground(dr);
            ((recyclerAdapterItems.ViewHolder) viewHolder).eliminarTextoItem.setVisibility(View.INVISIBLE);
            ((recyclerAdapterItems.ViewHolder) viewHolder).borrarIcon.setVisibility(View.INVISIBLE);
            swiping = isCurrentlyActive;
        }else if(!swiping){
            ((recyclerAdapterItems.ViewHolder) viewHolder).editarTextItem.setVisibility(View.VISIBLE);
            ((recyclerAdapterItems.ViewHolder) viewHolder).editarIcon.setVisibility(View.VISIBLE);
            ((recyclerAdapterItems.ViewHolder) viewHolder).eliminarTextoItem.setVisibility(View.VISIBLE);
            ((recyclerAdapterItems.ViewHolder) viewHolder).borrarIcon.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foregroundView  = ((recyclerAdapterItems.ViewHolder) viewHolder).layoutABorrar;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull
            RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView  = ((recyclerAdapterItems.ViewHolder) viewHolder).layoutABorrar;
        getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwipe(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    public interface recyclerItemTouchHelperListener{
        void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

}
