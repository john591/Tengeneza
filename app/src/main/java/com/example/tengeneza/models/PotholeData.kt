package com.example.tengeneza.models

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class PotholeData(
    var id: String? = null,
    var name: String ?= null,
    var countryCode: String ?= null,
    var countryName: String ?= null,
    var postalCode: String ?= null,
    var city: String ?= null,
    var streetAddress: String ?= null,
    var currentDateTimeString: String ?= null,
    var geoPoint: GeoPoint ?= null,
    var potholeImage: String ?= null
): Serializable
