package com.mlkitexample.yogachallenge.repository

import com.google.gson.GsonBuilder
import com.mlkitexample.yogachallenge.service.YogaServiceAPI
import com.mlkitexample.yogachallenge.model.Model
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


class YogaRepository {
    val responseHandler = ResponseHandler()
    fun retrofit() : YogaServiceAPI {
        return Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(YogaServiceAPI::class.java)
    }

//    suspend fun fetchListOfYogaPoses(): List<Model.YogaImage>{
//        return retrofit().getYogaPoses().hits
//    }

    suspend fun fetchListOfYogaPoses(): Resource<Model.Result>{
        return try{
            responseHandler.handleSuccess(retrofit().getYogaPoses())
        }catch (e:Exception){
            responseHandler.handleException(e)
        }
    }
}