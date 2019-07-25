package ru.cppinfo.googlemapapi.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.johnnylambada.location.LocationProvider;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.cppinfo.googlemapapi.R;
import ru.cppinfo.googlemapapi.model.Parking;

public class Adapter extends RecyclerView.Adapter {

    public ArrayList<RowType> dataSet;
    public ArrayList<RowType> savedDataSet;
    public GoogleMap map;
    public Activity context;
    LocationProvider locationProvider;

    private int status = 0;


    public Adapter(ArrayList<RowType> dataSet, GoogleMap map, Activity context) {
        this.dataSet = dataSet;
        this.map = map;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSet.get(position) instanceof Parking) {
            return RowType.ITEM_TYPE;
        } else if (dataSet.get(position) instanceof ItemInfoRowType) {
            return RowType.ITEM_INFO_TYPE;
        }else if(dataSet.get(position) instanceof ItemDividerRowType){
            return RowType.ITEM_DIVIDER_TYPE;
        } else {
            return -1;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case RowType.ITEM_TYPE:
                View itemView = LayoutInflater.from(context)
                        .inflate(R.layout.recycler_view_item, parent, false);
                return new ItemViewHolder(itemView);
            case RowType.ITEM_INFO_TYPE:
                View itemInfoView = LayoutInflater.from(context)
                        .inflate(R.layout.recycler_view_info_item, parent, false);
                return new ItemInfoViewHolder(itemInfoView);
            case RowType.ITEM_DIVIDER_TYPE:
                View itemDividerView = LayoutInflater.from(context)
                        .inflate(R.layout.recycler_view_divider_item, parent, false);
                return new ItemDividerViewHolder(itemDividerView);
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {

            Parking parking = (Parking) dataSet.get(position);

            ((ItemViewHolder) holder).txtParkNum.setText("Придорожная парковка "+parking.getNumber());
            ((ItemViewHolder) holder).txtStreet.setText(parking.getAddress());
            ((ItemViewHolder) holder).txtPeople.setText("Хотят припарковаться: " + parking.getPeoples());
            ((ItemViewHolder) holder).txtDistance.setText(parking.getDistance() + "м");

        } else if (holder instanceof ItemInfoViewHolder) {

        }else if(holder instanceof ItemDividerViewHolder){

        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtParkNum;
        TextView txtStreet;
        TextView txtPeople;
        TextView txtDistance;
        Button btnShow;
        LinearLayout layout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtParkNum = itemView.findViewById(R.id.txtParkNum);
            txtStreet = itemView.findViewById(R.id.txtStreet);
            txtPeople = itemView.findViewById(R.id.txtPeopleNum);
            txtDistance = itemView.findViewById(R.id.txtDistance);

            btnShow = itemView.findViewById(R.id.btn_show_park);
            btnShow.setOnClickListener(getBtnShowListener());

            layout = itemView.findViewById(R.id.item_layout);
            layout.setOnClickListener(getBtnShowListener());
        }

        private View.OnClickListener getBtnShowListener() {
            return v -> {
                if(status == 0){
                    Parking parking = (Parking) dataSet.get(getAdapterPosition());
                    ItemInfoRowType itemInfo = new ItemInfoRowType();

                    savedDataSet = new ArrayList<>();
                    savedDataSet.addAll(dataSet);

                    dataSet.clear();
                    notifyDataSetChanged();
                    dataSet.add(parking);
                    dataSet.add(itemInfo);


                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(parking.getLatitude(), parking.getLongitude()))
                            .zoom(19)
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    map.animateCamera(cameraUpdate);

                    status = 1;
                }
            };
        }
    }

    public class ItemInfoViewHolder extends RecyclerView.ViewHolder {
        Button btnStartPark;
        Button btnChooseAnother;
        Button btnToBook;

        public ItemInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            btnStartPark = itemView.findViewById(R.id.btn_start_park);
            btnChooseAnother = itemView.findViewById(R.id.btn_another_park);
            btnToBook = itemView.findViewById(R.id.btn_to_book);

            btnStartPark.setOnClickListener(getBtnStartParkListener());
            btnChooseAnother.setOnClickListener(getBtnChooseAnotherListener());
            btnToBook.setOnClickListener(getBtnToBookListener());
        }

        private View.OnClickListener getBtnStartParkListener() {
            return v -> {




                Parking parking = (Parking) dataSet.get(0);
                final double adressLat = parking.getLatitude();
                final double adressLong = parking.getLongitude();

                locationProvider = new LocationProvider.Builder(context)
                        .locationObserver(location -> {
                            locationProvider.stopTrackingLocation();
                            String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude() + "&daddr=" + adressLat + "," + adressLong;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            context.startActivity(intent);
                        })
                        .onPermissionDeniedFirstTime(() -> {

                        })
                        .onPermissionDeniedAgain(() -> {})
                        .onPermissionDeniedForever(() -> {})
                        .build();
                locationProvider.startTrackingLocation();



            };
        }

        private View.OnClickListener getBtnChooseAnotherListener(){
            return v -> {
                dataSet.clear();
                notifyItemRangeRemoved(0,2);
                Log.i("curr", savedDataSet.toString());
                dataSet.addAll(savedDataSet);

                status = 0;
            };
        }

        private View.OnClickListener getBtnToBookListener(){
            return v -> {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_booking_layout);
                dialog.setTitle("Title...");

                EditText editTextNumber = dialog.findViewById(R.id.etxt_dialog_booking);
                Button btnOk = dialog.findViewById(R.id.btn_dialog_booking_ok);
                Button btnCancel = dialog.findViewById(R.id.btn_dialog_booking_cancel);

                btnOk.setOnClickListener(view -> {
                    String number = editTextNumber.getText().toString();
                    if (number.trim().isEmpty()) {
                        Toast.makeText(context, "Вы не ввели номер!", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(context, "Место забронировано", Toast.LENGTH_SHORT).show();
                    }

                });
                btnCancel.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });
                dialog.show();

                // set the custom dialog components - text, image and button

            };
        }
    }

    public class ItemDividerViewHolder extends RecyclerView.ViewHolder{

        public ItemDividerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
