package com.rahul.child.Remote;

import com.rahul.child.Model.Child;
import com.rahul.child.Model.ChildFence;
import com.rahul.child.Model.Fence;
import com.rahul.child.Model.Message;
import com.rahul.child.Model.Parent;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IAPI {
    @POST("api/child/login")
    Observable<String> login(@Body Child child);

    @GET("api/fence/find_by_child/{id}")
    Observable<List<Fence>> getFenceByChild(@Path("id")int id);

    @GET("api/fence/find_by_id/{id}")
    Observable<Fence> getFenceById(@Path("id")int id);

    @GET("api/child/by_id/{id}")
    Observable<ChildFence> getChildById(@Path("id")int id);

    @PUT("api/child/update_location")
    Observable<String> updateMyLocation(@Body Child child);

    @PATCH("api/fence/status_update/{id}")
    Observable<String> updateFenceStatus(@Path("id")int id, @Body Fence fence);

    @GET("api/parent/by_id/{id}")
    Observable<Parent> getParent(@Path("id") int id);

    @GET("api/message/{sender}/{receiver}")
    Observable<List<Message>> getMessages(@Path("sender") String sender, @Path("receiver") String receiver);

    @POST("api/message/sendMessage")
    Observable<String> sendMessages(@Body Message message);
}
