package com.rahul.child;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.rahul.child.Model.Child;
import com.rahul.child.Model.Fence;
import com.rahul.child.Remote.IAPI;
import com.rahul.child.Remote.RetrofitClient;
import com.rahul.child.helper.GeoFenceHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    // views
    private ImageView bell;

    // static
    private static final String TAG = "HomeFragment";
    private static final String MY_ID = "Id";
    private static final String MY_PREFERENCE = "childPref";
    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 1001;
    private String GEOFENCE_ID = "F";

    // google & variables
    private GoogleMap map;
    private List<LatLng> latLngList = new ArrayList<>();
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geoFenceHelper;
    private IAPI api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        api = RetrofitClient.getInstance().create(IAPI.class);
        preferences = getActivity().getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geoFenceHelper = new GeoFenceHelper(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mMap);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        enableUserLocation();
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                int id = preferences.getInt(MY_ID, 0);
                compositeDisposable.add(api.getChildById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Gson gson = new Gson();
                            Child child = gson.fromJson(s, Child.class);
                            child.setLat(location.getLatitude());
                            child.setLng(location.getLongitude());
                            compositeDisposable.add(api.updateMyLocation(child)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(geoFenceHelper, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }));
            }
        });
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.setMyLocationEnabled(true);
            }else {

            }
        }
    }

    public void getChildFences() {
        int id = preferences.getInt(MY_ID,0);
        Log.d(TAG, "Id" + id);
        compositeDisposable.add(api.getFenceByChild(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<Fence>>() {
                @Override
                public void accept(List<Fence> fences) throws Exception {
                    for (Fence f:fences
                         ) {
                        Log.d(TAG, f.getFenceName());
                        double lat = f.getLat();
                        double lng = f.getLng();
                        LatLng latLng = new LatLng(lat, lng);

                        addMarker(latLng);
                        addCricle(latLng, f.getRadius());
                        addGeoFence(latLng, f.getRadius());
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            }));
    }

    // add fence
    private void addGeoFence(LatLng latLng, int radius) {
        GEOFENCE_ID = GEOFENCE_ID + 1;
        Geofence geofence = geoFenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER |
                Geofence.GEOFENCE_TRANSITION_DWELL |
                Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geoFenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: GeoFence Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMsg = geoFenceHelper.getError(e);
                        Log.e(TAG, errorMsg);
                    }
                });
    }

    // function to add marker option
    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        map.addMarker(markerOptions);
    }

    // function to add circle
    private void addCricle(LatLng latLng, int radius) {
        System.out.println(latLng);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);

        map.addCircle(circleOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        getChildFences();
    }
}