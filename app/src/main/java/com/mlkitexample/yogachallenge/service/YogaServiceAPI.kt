package com.mlkitexample.yogachallenge.service

import com.mlkitexample.yogachallenge.model.Model
import com.mlkitexample.yogachallenge.repository.Resource
import retrofit2.http.GET
import retrofit2.http.Query

interface YogaServiceAPI {
    @GET("api/")
    suspend fun getYogaPoses(
        @Query("key") key:String = "17646234-8e375c24fa2d4289403d6b1fa",
        @Query("q") q:String = "yoga+pose",
        @Query("image_type") image_type:String = "photo",
        @Query("per_page") per_page:Int = 150,
        @Query("min_width") min_width:Int = 400,
        @Query("min_height") min_height:Int = 400
    ): Model.Result
}