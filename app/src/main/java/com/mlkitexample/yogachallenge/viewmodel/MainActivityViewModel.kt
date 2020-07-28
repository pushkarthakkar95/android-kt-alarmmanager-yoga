package com.mlkitexample.yogachallenge.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlkitexample.yogachallenge.repository.YogaRepository
import com.mlkitexample.yogachallenge.model.Model
import com.mlkitexample.yogachallenge.repository.Resource
import com.mlkitexample.yogachallenge.repository.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel: ViewModel() {

    val yogaImageMutableLiveData = MutableLiveData<Resource<Model.Result>>()
    fun setup(){
        yogaImageMutableLiveData.postValue(Resource.loading(null))
        viewModelScope.launch(Dispatchers.IO){
            val response = YogaRepository().fetchListOfYogaPoses()
            yogaImageMutableLiveData.postValue(response)
        }
    }

    fun getRandomYogaPose(listOfYogaPoses: List<Model.YogaImage>) : String{
        return listOfYogaPoses.shuffled()[0].imageURL
    }

    fun getDifferenceInTime(hours:Int,min:Int): Long{
        val timeOfNotification = Calendar.getInstance()
        timeOfNotification.set(Calendar.HOUR_OF_DAY,hours)
        timeOfNotification.set(Calendar.MINUTE,min)
        val currenttime = Calendar.getInstance()
        val difference = timeOfNotification.timeInMillis - currenttime.timeInMillis
        if(difference>0){
            return difference
        }else{
            timeOfNotification.add(Calendar.HOUR_OF_DAY,24)
            return timeOfNotification.timeInMillis - currenttime.timeInMillis
        }
    }
}