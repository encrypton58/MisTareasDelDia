package com.Mc256Design.mistareasdeldia.RecyclerClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.Mc256Design.mistareasdeldia.ClassRequired.tarea;
import com.Mc256Design.mistareasdeldia.R;
import com.Mc256Design.mistareasdeldia.controlDarkMode.SetDarkMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class recyclerAdapterItems extends RecyclerView.Adapter<recyclerAdapterItems.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tituloView, descripView,horaView,designadoView,fechaView,idView, positionView;
        public TextView eliminarTextoItem, editarTextItem;
        public CardView card;
        private View view;
        public ConstraintLayout layoutABorrar;
        public RelativeLayout borrarEditar, parentLayout;
        ImageView editarIcon, borrarIcon;
        String titulo,descipcion ,designado, fecha;
        int hora, minuto;
        int id;
        AppCompatActivity app;

        ViewHolder(final View itemView , AppCompatActivity app){
            super(itemView);
            this.app = app;
            this.parentLayout = itemView.findViewById(R.id.item_recycler_parent_layout);
            view = itemView.findViewById(R.id.changeColor);
            card = itemView.findViewById(R.id.item_recycler_card);
            borrarEditar = itemView.findViewById(R.id.editar_borrar);
            editarTextItem = itemView.findViewById(R.id.editarTextoItem);
            eliminarTextoItem = itemView.findViewById(R.id.eliminarTextoItem);
            borrarIcon = itemView.findViewById(R.id.imagen_eliminar_swipe);
            editarIcon = itemView.findViewById(R.id.imagen_editar_swipe);
            tituloView = itemView.findViewById(R.id.titulo_item2);
            descripView = itemView.findViewById(R.id.descripcion_item2);
            horaView = itemView.findViewById(R.id.hora_item2);
            designadoView = itemView.findViewById(R.id.tiempoDesignadoItem);
            fechaView = itemView.findViewById(R.id.fecha_item2);
            idView = itemView.findViewById(R.id.id_tarea);
            positionView = itemView.findViewById(R.id.positionView);
            layoutABorrar = itemView.findViewById(R.id.layoutABorrar);
            setDarkMode();
            final ImageButton opcionesTarea = itemView.findViewById(R.id.opcionesItems);

            idView.setVisibility(View.INVISIBLE);
            positionView.setVisibility(View.INVISIBLE);

            Drawable dview = itemView.getContext().getDrawable(R.drawable.item_view_borde_color);
            Drawable dboton = itemView.getContext().getDrawable(R.drawable.item_view_backgroud_icons);
            int r = (int) (Math.random() * 255); int g = (int) (Math.random() * 255); int b = (int) (Math.random() * 255);
            int color1 = Color.rgb(r,g,b);
            int r2 = (int) (Math.random() * 255);  int g2  = (int) (Math.random() * 255); int b2 = (int) (Math.random() * 255);
            int color2 = Color.rgb(r2,g2,b2);
            ColorFilter c = new LightingColorFilter(color1 , color2);
            Objects.requireNonNull(dview).setColorFilter(c);
            Objects.requireNonNull(dboton).setColorFilter(c);
            view.setBackground(dview);
            opcionesTarea.setBackground(dboton);



            opcionesTarea.setOnClickListener(v -> {
                //int position_view = Integer.parseInt(positionView.getText().toString());
              //  new optionManagerDialog(itemView.getContext(), position_view, id,titulo, hora,minuto,designado,fecha,designado);

            });

        }

        private void setDarkMode(){
            SetDarkMode darkMode = new SetDarkMode(app.getApplicationContext(),app);
            darkMode.setDarkModeLayout(layoutABorrar);
            darkMode.setDarkModeLayout(parentLayout);
            ArrayList<CardView> card = new ArrayList<>();
            card.add(this.card);
            ArrayList<TextView> textViews = new ArrayList<>();
            textViews.add(tituloView);
            textViews.add(descripView);
            textViews.add(horaView);
            textViews.add(designadoView);
            textViews.add(fechaView);
            textViews.add(idView);
            textViews.add(positionView);
            darkMode.setDarkModeCards(card);
            darkMode.setDarkModeTextViews(textViews);
        }

    }

    public void removeItem(int position){
        tareaList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public List<tarea> tareaList;
    public Context context;
    public AppCompatActivity app;

    public recyclerAdapterItems(List<tarea> tareaList, AppCompatActivity app){
        this.tareaList = tareaList;
        this.context = app.getApplicationContext();
        this.app = app;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_items,parent,false);
        return new ViewHolder(view, app);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.positionView.setText(String.valueOf(position));
        holder.idView.setText(String.valueOf(tareaList.get(position).getId()));
        holder.tituloView.setText(tareaList.get(position).getTitulo());
        holder.descripView.setText(tareaList.get(position).getDescripcion());
        holder.fechaView.setText(tareaList.get(position).getFecha());
        holder.horaView.setText( "Inicia: " +
                tareaList.get(position).getHora() + ":" + tareaList.get(position).getMinuto());
        holder.designadoView.setText("Tiempo Designado\n" + (tareaList.get(position).getHorasDesignadas()) + " mins.");

        holder.id = tareaList.get(position).getId();
        holder.titulo = tareaList.get(position).getTitulo();
        holder.hora = tareaList.get(position).getHora();
        holder.minuto = tareaList.get(position).getMinuto();
        holder.descipcion = tareaList.get(position).getDescripcion();
        holder.designado = tareaList.get(position).getHorasDesignadas();
        holder.fecha = tareaList.get(position).getFecha();
    }

    @Override
    public int getItemCount() {
        return tareaList.size();
    }

}
