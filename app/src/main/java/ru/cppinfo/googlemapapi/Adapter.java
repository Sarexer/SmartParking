package ru.cppinfo.googlemapapi;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.LOCATION_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Adapter extends RecyclerView.Adapter {

    public ArrayList<RowType> dataSet;
    public ArrayList<RowType> savedDataSet;
    public GoogleMap map;
    public Context context;
    LocationManager mLocationManager;
    Location myLocation;


    public Adapter(ArrayList<RowType> dataSet, GoogleMap map, Context context) {
        this.dataSet = dataSet;
        this.map = map;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSet.get(position) instanceof ParkingPlace) {
            return RowType.ITEM_TYPE;
        } else if (dataSet.get(position) instanceof ItemInfoRowType) {
            return RowType.ITEM_INFO_TYPE;
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
            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {

            ParkingPlace place = (ParkingPlace) dataSet.get(position);

            ((ItemViewHolder) holder).txtParkNum.setText("Парковочное место 1");
            ((ItemViewHolder) holder).txtStreet.setText(place.getStreet());
            ((ItemViewHolder) holder).txtPeople.setText("Хотят припарковаться: " + place.getPeople());
            ((ItemViewHolder) holder).txtDistance.setText(place.getDistance() + "м");

            /*boolean find = false;
            for (RowType row : dataSet) {
                if (row instanceof ItemInfoRowType) {
                    find = true;
                    break;
                }
            }
            if (((ItemViewHolder) holder).btnShow.getVisibility() == VISIBLE) {
                ((ItemViewHolder) holder).btnShow.setVisibility(GONE);
            }
            if (!find) {
                ((ItemViewHolder) holder).btnShow.setVisibility(VISIBLE);
            }*/

        } else if (holder instanceof ItemInfoViewHolder) {

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
                ParkingPlace place = (ParkingPlace) dataSet.get(getAdapterPosition());
                ItemInfoRowType itemInfo = new ItemInfoRowType();

                savedDataSet = dataSet;

                dataSet.clear();
                notifyDataSetChanged();
                dataSet.add(place);
                dataSet.add(itemInfo);


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(place.getLatitude(), place.getLongitude()))
                        .zoom(20)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
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

                double adressLat = 0;
                double adressLong = 0;


                ParkingPlace place = (ParkingPlace) dataSet.get(0);
                adressLat = place.getLatitude();
                adressLong = place.getLongitude();


                String uri = "http://maps.google.com/maps?saddr=" + myLocation.getLatitude() + "," + myLocation.getLongitude() + "&daddr=" + adressLat + "," + adressLong;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);

            };
        }

        private View.OnClickListener getBtnChooseAnotherListener(){
            return v -> {
                dataSet.clear();
                dataSet.addAll(savedDataSet);
                notifyDataSetChanged();
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

    private int checkSelfPermission(String accessFineLocation) {
        return 0;
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }




}
