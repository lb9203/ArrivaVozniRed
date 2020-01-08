package com.example.arrivavoznired;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PathDialog extends AlertDialog {

    RecyclerView pathRecycler;
    List<Bus> mBusList;
    PathAdapter mPathAdapter;

    protected PathDialog(@NonNull Context context, List<Bus> busList) {
        super(context);
        mBusList = busList;
    }

    protected PathDialog(@NonNull Context context, int themeResId, List<Bus> busList) {
        super(context, themeResId);
        mBusList = busList;
    }

    protected PathDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, List<Bus> busList) {
        super(context, cancelable, cancelListener);
        mBusList = busList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_path_dialog);

        pathRecycler = findViewById(R.id.path_recycler);

    }
}
