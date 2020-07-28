package com.mlkitexample.yogachallenge.model

import com.google.gson.annotations.SerializedName
import com.mlkitexample.yogachallenge.repository.Resource

object Model {
    data class Result(@SerializedName("hits") var hits: List<YogaImage>)
    data class YogaImage(@SerializedName("largeImageURL") var imageURL: String)
}