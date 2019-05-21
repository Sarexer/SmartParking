package ru.cppinfo.googlemapapi;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.cppinfo.googlemapapi.geohash.GeoHash;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.cppinfo.googlemapapi.RestService.retrofit;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    RecyclerView recyclerView;
    LinearLayout llBottomSheet;
    Adapter adapter;
    ArrayList<ParkingPlace> parkingPlaces = new ArrayList<>();

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
        Call<Object> call = getService.get();
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.body() != null) {
                    final LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) response.body();
                    if (map == null) {
                        return;
                    }
                    ArrayList<LinkedTreeMap<String, Object>> list = (ArrayList<LinkedTreeMap<String, Object>>) map.get("res");

                    for (LinkedTreeMap<String, Object> treeMap : list) {
                        double lat = (double) treeMap.get("latitude");
                        double lng = (double) treeMap.get("longitude");
                        boolean status = (boolean) treeMap.get("status");

                        parkingPlaces.add(new ParkingPlace(lat, lng, status));
                    }


                    mapFragment.getMapAsync(googleMap -> {
                        mMap = googleMap;
                        mMap.setTrafficEnabled(true);
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
                        mMap.setMyLocationEnabled(true);

                        displayParkingPlaces();




/*
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()) )
                                .zoom(30)
                                .build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        mMap.animateCamera(cameraUpdate);
*/





/*
                        if(location != null){
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                                    .zoom(17)
                                    .build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            mMap.animateCamera(cameraUpdate);
                        }
*/

                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                Log.i("coord", latLng.toString());
                            }
                        });




                    });
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
            }
        });


        Disposable disposable = Observable.just(true).repeatWhen(t -> t.delay(3, TimeUnit.SECONDS)).debounce(3, TimeUnit.SECONDS).subscribe(b -> {
            RestService getService1 = retrofit.create(RestService.class);
            Call<Object> call1 = getService1.get();
            call1.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.body() != null) {
                        final LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) response.body();
                        if (map == null) {
                            return;
                        }

                        ArrayList<LinkedTreeMap<String, Object>> list = (ArrayList<LinkedTreeMap<String, Object>>) map.get("res");

                        parkingPlaces.clear();
                        for (LinkedTreeMap<String, Object> treeMap : list) {
                            double lat = (double) treeMap.get("latitude");
                            double lng = (double) treeMap.get("longitude");
                            boolean status = (boolean) treeMap.get("status");

                            parkingPlaces.add(new ParkingPlace(lat, lng, status));
                        }

                        mMap.clear();
                        displayParkingPlaces();
                    }

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

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

        ArrayList<RowType> freeParkingPlaces = new ArrayList<>();

        for (ParkingPlace place : parkingPlaces) {
            if (!place.isStatus()) {
                place.setPeople(3);
                place.setStreet("Мельникайте 70");
                place.setDistance(100);
                freeParkingPlaces.add(place);
            }
        }

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        recyclerView = llBottomSheet.findViewById(R.id.bottom_sheet_recyclerview);
        adapter = new Adapter(freeParkingPlaces, mMap, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);


        llBottomSheet.setVisibility(View.VISIBLE);

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
