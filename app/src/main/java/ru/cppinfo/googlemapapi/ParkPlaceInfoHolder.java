package ru.cppinfo.googlemapapi;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ParkPlaceInfoHolder extends RecyclerView.ViewHolder {
    final int type = 0;
    Button btnStartPark;
    Button btnChooseAnother;
    Button btnStartBooking;

    public ParkPlaceInfoHolder(@NonNull View itemView) {
        super(itemView);
        btnStartPark = itemView.findViewById(R.id.btn_start_park);
        btnChooseAnother = itemView.findViewById(R.id.btn_another_park);
        btnStartBooking = itemView.findViewById(R.id.btn_to_book);

    }

    private void setListeners(){
        btnStartPark.setOnClickListener(v -> {

        });

        btnChooseAnother.setOnClickListener(v -> {

        });

        btnStartBooking.setOnClickListener(v -> {

        });
    }




}
