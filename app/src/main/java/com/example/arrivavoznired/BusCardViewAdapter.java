package com.example.arrivavoznired;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Locale;

public class BusCardViewAdapter extends RecyclerView.Adapter<BusCardViewAdapter.BusViewHolder> {

    private static final String TAG = BusCardViewAdapter.class.getSimpleName();

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
        Bus currentBus = busList.get(i);

        busViewHolder.stationNames.setText(
                String.format(Locale.getDefault(),
                context.getResources().getString(R.string.card_station_names_text),
                currentBus.departureStationName,currentBus.arrivalStationName)
        );

        busViewHolder.departureArrivalTimes.setText(
                String.format(Locale.getDefault(),
                        context.getResources().getString(R.string.card_departure_arrival_times_text),
                        currentBus.departureTime,currentBus.arrivalTime)
        );

        busViewHolder.extraBusData.setText(
                String.format(Locale.getDefault(),
                        context.getResources().getString(R.string.card_extra_bus_data_text),
                        currentBus.price,currentBus.length,currentBus.duration)
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
        RecyclerView pathRecycler;

        final Context parentContext;

        BusViewHolder(final View itemView, final Context con){
            super(itemView);
            busCardView             = itemView.findViewById(R.id.bus_card_view);
            stationNames            = itemView.findViewById(R.id.card_station_names);
            departureArrivalTimes   = itemView.findViewById(R.id.card_departure_arrival_times);
            extraBusData            = itemView.findViewById(R.id.card_extra_bus_data);
            pathRecycler            = itemView.findViewById(R.id.path_recycler);
            parentContext           = con;

            pathRecycler.setLayoutManager(new LinearLayoutManager(parentContext));
        }
    }
}
