package com.example.tengeneza.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PotholeRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference
    private val firebaseCollection = firestore.collection("johnkalume0@gmail.com")

   private var INSTANCE : PotholeRepository ?= null

}