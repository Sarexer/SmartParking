package ru.cppinfo.googlemapapi;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.recyclerview.widget.RecyclerView.*;

public class ParkAdapter extends RecyclerView.Adapter<ViewHolder> {

    static public List<ParkPlace> placesList;
    static public List<ParkPlace> savePlacesList;
    GoogleMap map;
    Context context;
    boolean f = false;
    LocationManager mLocationManager;
    Location myLocation;

    ParkAdapter(List<ParkPlace> placesList, GoogleMap map, Context context) {
        this.placesList = placesList;
        this.map = map;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_item, parent, false);

            return new ParkPlaceHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_info_item, parent, false);
            return new ParkPlaceInfoHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParkPlace parkPlace = placesList.get(position);

        if (holder instanceof ParkPlaceHolder) {

            ((ParkPlaceHolder) holder).txtParkNum.setText("Придорожная парковка " + parkPlace.num);
            ((ParkPlaceHolder) holder).txtPeopleNum.setText("Хотят припарковаться: " + parkPlace.peoples);

            boolean find = false;
            for (ParkPlace place : placesList) {
                if (place.type == 2) {
                    find = true;
                    break;
                }
            }
            if (((ParkPlaceHolder) holder).btnShowParkPlace.getVisibility() == VISIBLE) {
                ((ParkPlaceHolder) holder).btnShowParkPlace.setVisibility(GONE);
            }
            if (!find) {
                ((ParkPlaceHolder) holder).btnShowParkPlace.setVisibility(VISIBLE);
            }

            ((ParkPlaceHolder) holder).btnShowParkPlace.setOnClickListener(v -> {

                ParkPlace place = placesList.get(position);
                ParkPlace infopark = new ParkPlace(place.num, place.peoples, 2);

                savePlacesList = new ArrayList<>();
                savePlacesList.addAll(placesList);

                int size = placesList.size();

                for (int i = 0; i < placesList.size(); i++) {

                    placesList.remove(i);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, placesList.size());
                    i--;

                }

                placesList.add(place);
                placesList.add(infopark);


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(47.202217, 38.935999))
                        .zoom(20)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
            });
        } else if (holder instanceof ParkPlaceInfoHolder) {

            ((ParkPlaceInfoHolder) holder).btnChooseAnother.setOnClickListener(v -> {
                int size = placesList.size();
                placesList.clear();
                notifyItemRangeRemoved(0, size);

                placesList.addAll(savePlacesList);
            });

            ((ParkPlaceInfoHolder) holder).btnStartBooking.setOnClickListener(v -> {
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

            });

            ((ParkPlaceInfoHolder) holder).btnStartPark.setOnClickListener(v -> {

                double adressLat = 0;
                double adressLong = 0;

                switch (placesList.get(position).num) {
                    case 1:
                        adressLat = 47.202256;
                        adressLong = 38.935955;
                        break;
                    case 2:
                        adressLat = 47.202236;
                        adressLong = 38.935956;
                        break;
                    case 3:
                        adressLat = 47.202216;
                        adressLong = 38.935957;
                        break;
                }
                myLocation = getLastKnownLocation();
                if (myLocation == null) {
                    Toast.makeText(context, "Включите геолокацию", Toast.LENGTH_SHORT).show();
                    return;
                }
                String uri = "http://maps.google.com/maps?saddr=" + myLocation.getLatitude() + "," + myLocation.getLongitude() + "&daddr=" + adressLat + "," + adressLong;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);

            });

        }

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ParkPlace place = placesList.get(position);
        if (place.type == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    class ParkPlaceHolderOld extends RecyclerView.ViewHolder {
        TextView txtParkNum;
        TextView txtPeopleNum;
        Button btnShowPark;
        TextView txtInfoPeople;
        Button btnStartPark;
        Button btnAnotherPark;
        Button btnBook;


        ParkPlaceHolderOld(View itemView) {
            super(itemView);
            txtParkNum = itemView.findViewById(R.id.txtParkNum);
            txtPeopleNum = itemView.findViewById(R.id.txtPeopleNum);
            btnStartPark = itemView.findViewById(R.id.btn_start_park);
            btnAnotherPark = itemView.findViewById(R.id.btn_another_park);
            btnShowPark = itemView.findViewById(R.id.btn_show_park);
            btnBook = itemView.findViewById(R.id.btn_to_book);

            if (btnStartPark != null) {
                btnStartPark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }

                        // Getting LocationManager object from System Service LOCATION_SERVICE
                        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

                        // Creating a criteria object to retrieve provider
                        Criteria criteria = new Criteria();

                        // Getting the name of the best provider
                        String provider = locationManager.getBestProvider(criteria, true);

                        // Getting Current Location

                        Location location = locationManager.getLastKnownLocation(provider);
                        double adressLat = 0;
                        double adressLong = 0;

                        switch (placesList.get(getAdapterPosition()).num) {
                            case 1:
                                adressLat = 47.202256;
                                adressLong = 38.935955;
                                break;
                            case 2:
                                adressLat = 47.202236;
                                adressLong = 38.935956;
                                break;
                            case 3:
                                adressLat = 47.202216;
                                adressLong = 38.935957;
                                break;
                        }
                        String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude() + "&daddr=" + adressLat + "," + adressLong;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });
            }

            if (btnBook != null) {
                btnBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


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
                    }
                });
            }

            if (btnAnotherPark != null) {
                btnAnotherPark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placesList.clear();
                        notifyItemRangeRemoved(0, 2);

                        placesList.addAll(savePlacesList);

                    }
                });
            }

            if (btnShowPark != null) {


                btnShowPark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParkPlace place = placesList.get(getAdapterPosition());
                        ParkPlace infopark = new ParkPlace(place.num, place.peoples, 2);

                        savePlacesList = new ArrayList<>();
                        for (int i = 0; i < placesList.size(); i++) {
                            savePlacesList.add(placesList.get(i));
                        }
                        int size = placesList.size();
                        placesList.clear();
                        notifyItemRangeRemoved(0, size);
                        placesList.add(place);
                        placesList.add(infopark);


                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(47.202217, 38.935999))
                                .zoom(20)
                                .build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        map.animateCamera(cameraUpdate);


                    }
                });

            }

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