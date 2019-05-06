package ru.cppinfo.googlemapapi;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ParkPlaceHolder extends RecyclerView.ViewHolder {
    public final int type = 1;

    TextView txtParkNum;
    TextView txtPeopleNum;
    Button btnShowParkPlace;

    public ParkPlaceHolder(@NonNull View itemView) {
        super(itemView);
        txtParkNum = itemView.findViewById(R.id.txtParkNum);
        txtPeopleNum = itemView.findViewById(R.id.txtPeopleNum);
        btnShowParkPlace = itemView.findViewById(R.id.btn_show_park);

        setListeners();
    }

    private void setListeners(){
        btnShowParkPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
