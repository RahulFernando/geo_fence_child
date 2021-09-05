package com.rahul.child;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rahul.child.Model.Child;
import com.rahul.child.Model.ChildFence;
import com.rahul.child.Model.Fence;
import com.rahul.child.Model.StaticRvModel;
import com.rahul.child.Remote.IAPI;
import com.rahul.child.Remote.RetrofitClient;
import com.rahul.child.helper.GeoFenceHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "HomeActivity";
    // views
    private StaticRvAdapter staticRvAdapter;
    private TextView txt_username;
    private RecyclerView menu;
    private ImageView img_exit;

    // static
    private static final String MY_PREFERENCE = "childPref";
    private static final String MY_USERNAME = "UserName";
    private static final String IS_LOGGED = "IsLogged";
    private static final String MY_ID = "Id";
    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 1001;

    // variables
    private GoogleMap map;
    SharedPreferences preferences;
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geoFenceHelper;

    // network
    private IAPI api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // hooks
        api = RetrofitClient.getInstance().create(IAPI.class);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);
        menu = findViewById(R.id.rv_1);
        ArrayList<StaticRvModel> item = new ArrayList<>();
        txt_username = findViewById(R.id.txt_username);
        img_exit = findViewById(R.id.img_exit);
        preferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);

        txt_username.setText(preferences.getString(MY_USERNAME, "User"));
        item.add(new StaticRvModel(R.drawable.speak, "Chat"));
        staticRvAdapter = new StaticRvAdapter(this, item);

        menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        menu.setAdapter(staticRvAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        img_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(IS_LOGGED, false);
                editor.apply();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

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
                        .subscribe(new Consumer<List<ChildFence>>() {
                            @Override
                            public void accept(List<ChildFence> childFence) throws Exception {
                                Child child = childFence.get(0).getChild();
                                child.setLat(location.getLatitude());
                                child.setLng(location.getLongitude());
                                for (Fence fence:childFence.get(0).getFences()
                                ) {
                                    if (fence.getStatus().equals("Exit") || fence.getStatus().equals("Enter")) {
                                        updateFence(fence);
                                    }
                                }
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
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
        compositeDisposable.add(api.getFenceByChild(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Fence>>() {
                    @Override
                    public void accept(List<Fence> fences) throws Exception {
                        for (Fence f:fences
                        ) {
                            double lat = f.getLat();
                            double lng = f.getLng();
                            LatLng latLng = new LatLng(lat, lng);

                            addMarker(latLng);
                            addCricle(latLng, f.getRadius());
                            addGeoFence(latLng, f.getRadius(), f.getId());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }

    // add fence
    private void addGeoFence(LatLng latLng, int radius, int id) {
        String GEOFENCE_ID = String.valueOf(id);
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

    /**
     * Handle fence status update
     * @param fence
     */
    private void updateFence(Fence fence) {
        fence.setStatus("");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                compositeDisposable.add(api.updateFenceStatus(fence.getId(), fence)
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
        }, 120000);
    }
}