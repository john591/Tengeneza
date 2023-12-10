package com.example.tengeneza.models

import kotlin.properties.Delegates

class DataClass {
    private lateinit var userConnected: String
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private lateinit var holeImage: String
    private lateinit var streetAdress: String
    private lateinit var city: String
    private lateinit var countryCode: String
    private lateinit var countryName: String
    private lateinit var postalCode: String

    fun userConnected(): String {
        return userConnected
    }
    fun latitude(): String {
        return latitude.toString()
    }
    fun longitude(): String {
        return longitude.toString()
    }
    fun holeImage(): String {
        return holeImage
    }
    fun streetAdress(): String {
        return streetAdress
    }
    fun city(): String {
        return city
    }
    fun countryCode(): String {
        return countryCode
    }
    fun countryName(): String {
        return countryName
    }
    fun postalCode(): String {
        return postalCode
    }


    constructor(
        userConnected: String,
        latitude: Double,
        longitude: Double,
        holeImage: String,
        streetAdress: String,
        city: String,
        countryCode: String,
        countryName: String,
        postalCode: String
    ) {
        this.userConnected = userConnected
        this.latitude = latitude
        this.longitude = longitude
        this.holeImage = holeImage
        this.streetAdress = streetAdress
        this.city = city
        this.countryCode = countryCode
        this.countryName = countryName
        this.postalCode = postalCode
    }
}