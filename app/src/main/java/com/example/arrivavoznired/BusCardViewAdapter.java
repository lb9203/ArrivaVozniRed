package com.example.arrivavoznired;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Locale;

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
        busViewHolder.currentBus = busList.get(i);

        busViewHolder.stationNames.setText(
                String.format(Locale.getDefault(),
                context.getResources().getString(R.string.card_station_names_text),
                busViewHolder.currentBus.departureStationName,busViewHolder.currentBus.arrivalStationName)
        );

        busViewHolder.departureArrivalTimes.setText(
                String.format(Locale.getDefault(),
                        context.getResources().getString(R.string.card_departure_arrival_times_text),
                        busViewHolder.currentBus.departureTime,busViewHolder.currentBus.arrivalTime)
        );

        busViewHolder.extraBusData.setText(
                String.format(Locale.getDefault(),
                        context.getResources().getString(R.string.card_extra_bus_data_text),
                        busViewHolder.currentBus.price,busViewHolder.currentBus.length,busViewHolder.currentBus.duration)
        );

    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    class BusViewHolder extends RecyclerView.ViewHolder{
        CardView busCardView;
        MaterialTextView stationNames;
        MaterialTextView departureArrivalTimes;
        MaterialTextView extraBusData;
        MaterialButton shareButton;
        Bus currentBus;
        final Context parentContext;

        BusViewHolder(final View itemView, final Context con){
            super(itemView);
            parentContext           = con;
            busCardView             = itemView.findViewById(R.id.bus_card_view);
            stationNames            = itemView.findViewById(R.id.card_station_names);
            departureArrivalTimes   = itemView.findViewById(R.id.card_departure_arrival_times);
            extraBusData            = itemView.findViewById(R.id.card_extra_bus_data);
            shareButton = itemView.findViewById(R.id.card_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = shareString();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,message);
                    sendIntent.setType("text/plain");
                    con.startActivity(sendIntent);
                }
            });

        }

        String shareString(){

            return String.format(parentContext.getResources().getString(R.string.bus_share_text),
                    currentBus.departureStationName,currentBus.departureTime,
                    currentBus.arrivalStationName,currentBus.departureTime,
                    currentBus.price,currentBus.length,currentBus.duration
            );
        }
    }
}
