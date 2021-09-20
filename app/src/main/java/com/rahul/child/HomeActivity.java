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
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rahul.child.Model.Child;
import com.rahul.child.Model.ChildFence;
import com.rahul.child.Model.Fence;
import com.rahul.child.Model.StaticRvModel;
import com.rahul.child.Remote.IAPI;
import com.rahul.child.Remote.RetrofitClient;
import com.rahul.child.helper.GeoFenceHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    // variables
    private GoogleMap map;
    SharedPreferences preferences;
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geoFenceHelper;
    private List<Fence> fenceList;

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
        fenceList = new ArrayList<>();

        txt_username.setText(preferences.getString(MY_USERNAME, "User"));
        item.add(new StaticRvModel(R.drawable.speak, "Chat"));
        staticRvAdapter = new StaticRvAdapter(this, item);

        menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        menu.setAdapter(staticRvAdapter);

        getChildFences();


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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("Triggered");
                for (Fence fence: fenceList
                     ) {
                    fence.setStatus("");
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
            }
        }, 5000);

    }

//    private void checkSettingAndStartLocationUpdate() {
//        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest).build();
//
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
//        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                startLocationUpdate();
//            }
//        });
//        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    ResolvableApiException apiException = (ResolvableApiException) e;
//                    try {
//                        apiException.startResolutionForResult(HomeActivity.this, 1001);
//                    } catch (IntentSender.SendIntentException sendIntentException) {
//                        sendIntentException.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    private void startLocationUpdate(){
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void stopLocationUpdate() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }

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
                        .subscribe(new Consumer<ChildFence>() {
                            @Override
                            public void accept(ChildFence childFence) throws Exception {
                                Child child = childFence.getChild();
                                child.setLat(location.getLatitude());
                                child.setLng(location.getLongitude());

                                updateUserLocation(child);
                                fenceList.addAll(childFence.getFences());

                                for (Fence fence:childFence.getFences()
                                ) {
                                    if (fence.getStatus().equals("Exit") || fence.getStatus().equals("Enter")) {

                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
//                                System.out.println(throwable.getMessage());
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

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is necessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
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

                            if (Build.VERSION.SDK_INT >= 29) {
                                //We need background permission
                                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    handleFence(latLng, f.getRadius(), f.getType(), f.getId());
                                } else {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                                        //We show a dialog and ask for permission
                                        ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                                    } else {
                                        ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                                    }
                                }

                            } else {
                                handleFence(latLng, f.getRadius(), f.getType(), f.getId());
                            }

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
    private void addCircle(LatLng latLng, int radius, String type) {
        System.out.println(latLng);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        if (type.equals("danger")) {
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        } else if (type.equals("safe")) {
            circleOptions.strokeColor(Color.argb(255, 0, 255, 0));
            circleOptions.fillColor(Color.argb(64, 0, 255, 0));
        }
        circleOptions.strokeWidth(4);

        map.addCircle(circleOptions);
    }

    /**
     * Handle fence status update
     * @param fence
     */
    private void updateFence(Fence fence) {
        System.out.println(fence.getFenceName());
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
                                Date currentTime = Calendar.getInstance().getTime();
                                Log.d(TAG, currentTime.toString());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(geoFenceHelper, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        }, 10000);
    }

    private void updateUserLocation(Child child) {
        System.out.println("Hello");
        compositeDisposable.add(api.updateMyLocation(child)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println(throwable.getMessage());
                        Toast.makeText(geoFenceHelper, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void handleFence(LatLng latLng, int radius, String type, int id) {
        addMarker(latLng);
        addCircle(latLng, radius, type);
        addGeoFence(latLng, radius, id);
    }
}