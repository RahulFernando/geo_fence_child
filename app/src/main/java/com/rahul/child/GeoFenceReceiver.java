package com.rahul.child;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;
import com.rahul.child.Model.Fence;
import com.rahul.child.Remote.IAPI;
import com.rahul.child.Remote.RetrofitClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class GeoFenceReceiver extends BroadcastReceiver {
    private static final String TAG = "Notification";
    private IAPI api = RetrofitClient.getInstance().create(IAPI.class);
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Triggered", Toast.LENGTH_SHORT).show();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "Error");
            return;
        }
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d("Notification", geofence.getRequestId());
            int id = Integer.parseInt(geofence.getRequestId());

//            Fence(int id, String fenceName, double lat, double lng, int radius, int childId, String status, String type)

            int transitionType = geofencingEvent.getGeofenceTransition();
            switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                compositeDisposable.add(api.getFenceById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Fence>() {
                            @Override
                            public void accept(Fence fence) throws Exception {
                                fence.setStatus("Enter");
                                compositeDisposable.add(api.updateFenceStatus(id, fence)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String s) throws Exception {
                                                Gson gson = new Gson();
                                                System.out.println(gson.fromJson(s, String.class));
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                System.out.println(throwable.getMessage());
                                            }
                                        }));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.out.println(throwable.getMessage());
                            }
                        }));
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                compositeDisposable.add(api.getFenceById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Fence>() {
                            @Override
                            public void accept(Fence fence) throws Exception {
                                fence.setStatus("Exit");
                                compositeDisposable.add(api.updateFenceStatus(id, fence)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String s) throws Exception {
                                                Gson gson = new Gson();
                                                System.out.println(gson.fromJson(s, String.class));
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                System.out.println(throwable.getMessage());
                                            }
                                        }));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.out.println(throwable.getMessage());
                            }
                        }));
                break;

        }

        }
    }
}