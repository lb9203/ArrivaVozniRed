package com.example.arrivavoznired;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BusCardViewAdapter extends RecyclerView.Adapter<BusCardViewAdapter.BusViewHolder> {
    private List<Bus> busList;
    Context context;

    BusCardViewAdapter(List<Bus> buses, Context con){
        busList = buses;
        context = con;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item,viewGroup,false);
        BusViewHolder bvh = new BusViewHolder(v, context);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder busViewHolder, int i) {
        busViewHolder.deptime.setText(busList.get(i).departure_time);
        busViewHolder.arrtime.setText(busList.get(i).arrival_time);
        busViewHolder.depname.setText(busList.get(i).departure_name);
        busViewHolder.arrname.setText(busList.get(i).arrival_name);
        busViewHolder.price.setText(busList.get(i).price);
        busViewHolder.length.setText(busList.get(i).length);
        busViewHolder.duration.setText(busList.get(i).duration);
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder{
        CardView bus_card_view;
        TextView deptime;
        TextView arrtime;
        TextView depname;
        TextView arrname;
        TextView length;
        TextView price;
        TextView duration;

        BusViewHolder(final View itemView, final Context con){
            super(itemView);
            bus_card_view = (CardView)itemView.findViewById(R.id.bus_card_view);
            deptime = (TextView) itemView.findViewById(R.id.card_departure_time);
            arrtime = (TextView) itemView.findViewById(R.id.card_arrival_time);
            depname = (TextView) itemView.findViewById(R.id.card_departure_name);
            arrname = (TextView) itemView.findViewById(R.id.card_arrival_name);
            length = (TextView) itemView.findViewById(R.id.card_length);
            price = (TextView) itemView.findViewById(R.id.card_price);
            duration = (TextView) itemView.findViewById(R.id.card_duration);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg_depname = depname.getText().toString();
                    String msg_arrname = arrname.getText().toString();
                    String msg_deptime = deptime.getText().toString();
                    String msg_arrtime = arrtime.getText().toString();
                    String msg_price = price.getText().toString();
                    String msg_duration = duration.getText().toString();
                    String msg_length = length.getText().toString();
                    String message = String.format("%s(%s) - %s(%s)\nČas: %s, Cena: %s, Pot:%s",msg_depname,msg_deptime,msg_arrname,msg_arrtime,msg_duration,msg_price,msg_length);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(con);
                    alertBuilder.setMessage(message);
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            });
        }
    }
}
