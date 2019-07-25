package ru.cppinfo.googlemapapi.ui;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.cppinfo.googlemapapi.ui.adapter.Adapter;
import ru.cppinfo.googlemapapi.BuildConfig;
import ru.cppinfo.googlemapapi.ui.adapter.ItemDividerRowType;
import ru.cppinfo.googlemapapi.R;
import ru.cppinfo.googlemapapi.service.RestService;
import ru.cppinfo.googlemapapi.ui.adapter.RowType;
import ru.cppinfo.googlemapapi.geohash.GeoHash;
import ru.cppinfo.googlemapapi.model.Parking;
import ru.cppinfo.googlemapapi.model.ParkingPlace;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.johnnylambada.location.LocationProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static ru.cppinfo.googlemapapi.service.RestService.retrofit;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    RecyclerView recyclerView;
    LinearLayout llBottomSheet;
    Adapter adapter;
    HashMap<Integer, Parking> parkings = new HashMap<>();
    LocationProvider locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.setPeekHeight(340);
        bottomSheetBehavior.setHideable(true);

        RestService getService = retrofit.create(RestService.class);
        Call<HashMap<Integer,Parking>> call = getService.get();
        call.enqueue(new Callback<HashMap<Integer,Parking>>() {
            @Override
            public void onResponse(Call<HashMap<Integer, Parking>> call, Response<HashMap<Integer,Parking>> response) {
                if (response.body() != null) {
                    parkings = response.body();

                    mapFragment.getMapAsync(googleMap -> {
                        mMap = googleMap;
                        mMap.setTrafficEnabled(true);
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mMap.setMyLocationEnabled(true);

                        displayParkingPlaces();


                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                Log.i("coord", latLng.toString());
                            }
                        });


                        locationProvider = new LocationProvider.Builder(MapsActivity.this)
                                .locationObserver(location -> {
                                    final String updated = "\n"+"(" + location.getLatitude() + ", " + location.getLongitude() + ")";
                                    Log.i("curr", updated);
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(location.getLatitude(),location.getLongitude()))
                                            .zoom(17)
                                            .build();
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                    mMap.animateCamera(cameraUpdate);
                                    locationProvider.stopTrackingLocation();
                                })
                                .onPermissionDeniedFirstTime(() -> {
                                    Snackbar.make(
                                            findViewById(android.R.id.content),
                                            "Denied: This app needs location permission.",
                                            Snackbar.LENGTH_LONG)
                                            .setAction("OK", __ -> {})
                                            .show();
                                })
                                .onPermissionDeniedAgain(()->{
                                    Snackbar.make(
                                            findViewById(android.R.id.content),
                                            "Rationale: This app can't show location without permission.",
                                            Snackbar.LENGTH_LONG)
                                            .setAction("OK", __ -> {})
                                            .show();

                                })
                                .onPermissionDeniedForever(()->{
                                    Snackbar.make(
                                            findViewById(android.R.id.content),
                                            "Settings: This app can't show location without permission. Please update settings.",
                                            Snackbar.LENGTH_INDEFINITE)
                                            .setAction("Settings", __ -> locationProvider.startAppSettings(BuildConfig.APPLICATION_ID))
                                            .show();

                                })
                                .build();

                        locationProvider.startTrackingLocation();
                    });
                }
            }

            @Override
            public void onFailure(Call<HashMap<Integer,Parking>> call, Throwable t) {
                t.printStackTrace();
            }
        });


        Disposable disposable = Observable.just(true).repeatWhen(t -> t.delay(3, TimeUnit.SECONDS)).debounce(3, TimeUnit.SECONDS).subscribe(b -> {
            RestService getService1 = retrofit.create(RestService.class);
            Call<HashMap<Integer,Parking>> call1 = getService1.get();
            call1.enqueue(new Callback<HashMap<Integer,Parking>>() {
                @Override
                public void onResponse(Call<HashMap<Integer,Parking>> call, Response<HashMap<Integer,Parking>> response) {
                    if (response.body() != null) {
                        parkings = response.body();

                        mMap.clear();
                        displayParkingPlaces();
                    }

                }

                @Override
                public void onFailure(Call<HashMap<Integer,Parking>> call, Throwable t) {

                }
            });
        });


    }

    public void onClickBtn(View view) {
        //GeoHash currentHash = GeoHash.fromLocation(location);

        Location park1 = new Location("geohash");
        park1.setLatitude(47.202256);
        park1.setLongitude(38.935955);

        Location park2 = new Location("geohash");
        park2.setLatitude(47.202236);
        park2.setLongitude(38.935956);

        Location park3 = new Location("geohash");
        park3.setLatitude(47.202216);
        park3.setLongitude(38.935957);

        //String curHash = currentHash.toString().substring(0,4);
        String hash1 = GeoHash.fromLocation(park1).toString().substring(0, 4);
        String hash2 = GeoHash.fromLocation(park2).toString().substring(0, 4);
        String hash3 = GeoHash.fromLocation(park3).toString().substring(0, 4);


        showBottomSheet();
    }

    private void showBottomSheet() {

        ArrayList<RowType> freeParkingPlaces = findFreeParkings();

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        recyclerView = llBottomSheet.findViewById(R.id.bottom_sheet_recyclerview);
        adapter = new Adapter(freeParkingPlaces, mMap, MapsActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);


        llBottomSheet.setVisibility(View.VISIBLE);
    }

    private ArrayList<RowType> findFreeParkings(){
        ArrayList<RowType> freeParkings = new ArrayList<>();
        int counter = 1;

        for (Parking parking : parkings.values()) {
            ArrayList<ParkingPlace> parkingPlaces = parking.getParkingPlaces();

            for (ParkingPlace parkingPlace : parkingPlaces) {
                if(!parkingPlace.isStatus()){
                    parking.setNumber(counter++);
                    if(parking.getPid() == 1){
                        parking.setDistance(100);
                        parking.setPeoples(8);
                    }else if(parking.getPid() == 3){
                        parking.setDistance(250);
                        parking.setPeoples(1);
                    }

                    freeParkings.add(parking);
                    freeParkings.add(new ItemDividerRowType());
                    break;
                }
            }
        }

        if(freeParkings.get(freeParkings.size()-1) instanceof ItemDividerRowType){
            freeParkings.remove(freeParkings.size() - 1);
        }



        return freeParkings;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void displayParkingPlaces() {
        Drawable drawableGreen = getDrawable(R.drawable.place_image_green);
        Drawable drawableRed = getDrawable(R.drawable.place_image_red);

        for (Parking parking : parkings.values()) {
            ArrayList<ParkingPlace> parkingPlaces = parking.getParkingPlaces();

            for (ParkingPlace place : parkingPlaces) {
                double lat = place.getLatitude();
                double lng = place.getLongitude();
                boolean status = place.isStatus();
                GroundOverlayOptions image = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(!status ? drawableGreen : drawableRed)))
                        .position(new LatLng(lat, lng), 3.2f, 1.5f)
                        .bearing(40);

                mMap.addGroundOverlay(image);
            }
        }

    }


}
