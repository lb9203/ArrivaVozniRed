package com.example.arrivavoznired;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BusCardViewAdapter extends RecyclerView.Adapter<BusCardViewAdapter.BusViewHolder> {
    private List<Bus> busList;
    private Context context;

    BusCardViewAdapter(List<Bus> buses, Context con){
        busList = buses;
        context = con;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item,viewGroup,false);
        return new BusViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder busViewHolder, int i) {
        busViewHolder.deptime.setText(busList.get(i).departureTime);
        busViewHolder.arrtime.setText(busList.get(i).arrivalTime);
        busViewHolder.depname.setText(busList.get(i).departureStationName);
        busViewHolder.arrname.setText(busList.get(i).arrivalStationName);
        busViewHolder.price.setText(busList.get(i).price);
        busViewHolder.length.setText(busList.get(i).length);
        busViewHolder.duration.setText(busList.get(i).duration);
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    class BusViewHolder extends RecyclerView.ViewHolder{
        CardView busCardView;
        TextView deptime;
        TextView arrtime;
        TextView depname;
        TextView arrname;
        TextView length;
        TextView price;
        TextView duration;
        ImageView shareButton;

        BusViewHolder(final View itemView, final Context con){
            super(itemView);
            busCardView = itemView.findViewById(R.id.bus_card_view);
            deptime     = itemView.findViewById(R.id.card_departure_time);
            arrtime     = itemView.findViewById(R.id.card_arrival_time);
            depname     = itemView.findViewById(R.id.card_departure_name);
            arrname     = itemView.findViewById(R.id.card_arrival_name);
            length      = itemView.findViewById(R.id.card_length);
            price       = itemView.findViewById(R.id.card_price);
            duration    = itemView.findViewById(R.id.card_duration);
            shareButton = itemView.findViewById(R.id.card_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msgDepname  = depname.getText().toString();
                    String msgArrname  = arrname.getText().toString();
                    String msgDeptime  = deptime.getText().toString();
                    String msgArrtime  = arrtime.getText().toString();
                    String msgPrice    = price.getText().toString();
                    String msgDuration = duration.getText().toString();
                    String msgLength   = length.getText().toString();
                    String message = String.format("%s(%s) - %s(%s)%nČas: %s, Cena: %s, Pot: %s",msgDepname,msgDeptime,msgArrname,msgArrtime,msgDuration,msgPrice,msgLength);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,message);
                    sendIntent.setType("text/plain");
                    con.startActivity(sendIntent);
                }
            });

        }
    }
}
