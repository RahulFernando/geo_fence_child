package com.rahul.child.Remote;

import com.rahul.child.Model.Child;
import com.rahul.child.Model.Fence;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IAPI {
    @POST("api/child/login")
    Observable<String> login(@Body Child child);

    @GET("api/fence/find_by_child/{id}")
    Observable<List<Fence>> getFenceByChild(@Path("id")int id);

    @GET("api/child/by_id/{id}")
    Observable<String> getChildById(@Path("id")int id);

    @PUT("api/child/update_location")
    Observable<String> updateMyLocation(@Body Child child);
}
