package com.example.tengeneza.models
import com.google.firebase.firestore.GeoPoint

data class TengenezaData(
    var currentUser: String,
    var currentDateTimeString: String,
    var potholeImage: String,
    var name: String,
    var geoPoint: GeoPoint,
    var streetAddress: String,
    var city: String,
    var countryCode: String,
    var countryName: String,
    var postalCode: String){

}
