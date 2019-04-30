package ru.cppinfo.googlemapapi;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.cppinfo.googlemapapi.geohash.GeoHash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.internal.PolylineEncoding;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import static ru.cppinfo.googlemapapi.RestService.retrofit;

public class MapsActivity extends FragmentActivity{

    private GoogleMap mMap;
    private PolygonOptions mPolygonOptions;
    private Polygon mPolygon;

    boolean stat1;
    boolean stat2;
    boolean stat3;

    Location location;

    RecyclerView recyclerView;
    LinearLayout llBottomSheet;
    ParkAdapter adapter;
    ArrayList<ParkPlace> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

// настройка состояний нижнего экрана
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

// настройка максимальной высоты
        bottomSheetBehavior.setPeekHeight(340);

// настройка возможности скрыть элемент при свайпе вниз
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
                    final ArrayList<Boolean> list = (ArrayList<Boolean>) map.get("res");

                    stat1 = list.get(0);
                    stat2 = list.get(1);
                    stat3 = list.get(2);
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

                        // Getting LocationManager object from System Service LOCATION_SERVICE
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                        // Creating a criteria object to retrieve provider
                        Criteria criteria = new Criteria();

                        // Getting the name of the best provider
                        String provider = locationManager.getBestProvider(criteria, true);

                        // Getting Current Location
                        location = locationManager.getLastKnownLocation(provider);





                        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                            @Override
                            public void onPolygonClick(Polygon polygon) {

                            }
                        });
                        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                            @Override
                            public void onPolylineClick(Polyline polyline) {

                            }
                        });

                        PolygonOptions polygoneOptions1 = new PolygonOptions()
                                .add(new LatLng(47.202256, 38.935955))
                                .add(new LatLng(47.202270, 38.935953))
                                .add(new LatLng(47.202272, 38.935995))
                                .add(new LatLng(47.202257, 38.935997))
                                .fillColor(stat1 ? Color.RED : Color.GREEN).strokeColor(stat1 ? Color.RED : Color.GREEN);
                        PolygonOptions polygoneOptions2 = new PolygonOptions()
                                .add(new LatLng(47.202236, 38.935956))
                                .add(new LatLng(47.202250, 38.935954))
                                .add(new LatLng(47.202252, 38.935996))
                                .add(new LatLng(47.202237, 38.935998))
                                .fillColor(stat2 ? Color.RED : Color.GREEN).strokeColor(stat2 ? Color.RED : Color.GREEN);
                        PolygonOptions polygoneOptions3 = new PolygonOptions()
                                .add(new LatLng(47.202216, 38.935957))
                                .add(new LatLng(47.202230, 38.935955))
                                .add(new LatLng(47.202232, 38.935997))
                                .add(new LatLng(47.202217, 38.935999))
                                .fillColor(stat3 ? Color.RED : Color.GREEN).strokeColor(stat3 ? Color.RED : Color.GREEN);

                        mMap.addPolygon(polygoneOptions1);
                        mMap.addPolygon(polygoneOptions2);
                        mMap.addPolygon(polygoneOptions3);


                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(47.202250, 38.935954)));

                        if(location != null){
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                                    .zoom(17)
                                    .build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            mMap.animateCamera(cameraUpdate);
                        }


                    });
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
            }
        });



        Observable.just(true).repeatWhen(t -> t.delay(3, TimeUnit.SECONDS)).debounce(3, TimeUnit.SECONDS).subscribe(b -> {
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
                        final ArrayList<Boolean> list = (ArrayList<Boolean>) map.get("res");

                        stat1 = list.get(0);
                        stat2 = list.get(1);
                        stat3 = list.get(2);

                        mMap.clear();
                        PolygonOptions polygoneOptions1 = new PolygonOptions()
                                .add(new LatLng(47.202256, 38.935955))
                                .add(new LatLng(47.202270, 38.935953))
                                .add(new LatLng(47.202272, 38.935995))
                                .add(new LatLng(47.202257, 38.935997))
                                .fillColor(stat1 ? Color.RED : Color.GREEN).strokeColor(stat1 ? Color.RED : Color.GREEN);
                        PolygonOptions polygoneOptions2 = new PolygonOptions()
                                .add(new LatLng(47.202236, 38.935956))
                                .add(new LatLng(47.202250, 38.935954))
                                .add(new LatLng(47.202252, 38.935996))
                                .add(new LatLng(47.202237, 38.935998))
                                .fillColor(stat2 ? Color.RED : Color.GREEN).strokeColor(stat2 ? Color.RED : Color.GREEN);
                        PolygonOptions polygoneOptions3 = new PolygonOptions()
                                .add(new LatLng(47.202216, 38.935957))
                                .add(new LatLng(47.202230, 38.935955))
                                .add(new LatLng(47.202232, 38.935997))
                                .add(new LatLng(47.202217, 38.935999))
                                .fillColor(stat3 ? Color.RED : Color.GREEN).strokeColor(stat3 ? Color.RED : Color.GREEN);


                        mMap.addPolygon(polygoneOptions1);
                        mMap.addPolygon(polygoneOptions2);
                        mMap.addPolygon(polygoneOptions3);



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
        String hash1 = GeoHash.fromLocation(park1).toString().substring(0,4);
        String hash2 = GeoHash.fromLocation(park2).toString().substring(0,4);
        String hash3 = GeoHash.fromLocation(park3).toString().substring(0,4);


        showBottomSheet();





    }

    private void showBottomSheet() {

        places = new ArrayList<>();

        ParkPlace parkPlace1 = new ParkPlace(1, 2,1);
        ParkPlace parkPlace2 = new ParkPlace(2, 5,1);
        ParkPlace parkPlace3 = new ParkPlace(3, 0,1);

        if(!stat1){
            places.add(parkPlace1);
        }
        if(!stat2){
            places.add(parkPlace2);
        }
        if(!stat3){
            places.add(parkPlace3);
        }





// настройка поведения нижнего экрана
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

// настройка состояний нижнего экрана
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

// настройка максимальной высоты




        recyclerView = llBottomSheet.findViewById(R.id.bottom_sheet_recyclerview);
        adapter = new ParkAdapter(places,mMap,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        llBottomSheet.setVisibility(View.VISIBLE);

    }

}
