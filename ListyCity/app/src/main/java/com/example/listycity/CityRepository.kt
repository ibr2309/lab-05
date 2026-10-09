package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class CityRepository {

    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")
    private val _cities = mutableStateListOf<City>()

    val cities: List<City>
        get() = _cities

    init {
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _cities.clear()

            snapshot?.documents?.forEach { document ->
                val city = document.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }

        citiesRef.get().addOnSuccessListener { result ->
            if (result.isEmpty) {
                listOf(
                    City("Edmonton", "AB"),
                    City("Vancouver", "BC"),
                    City("Toronto", "ON")
                ).forEach { citiesRef.document(it.name).set(it) }
            }
        }
    }

    fun addCity(city: City) {
        citiesRef.document(city.name).set(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        if (oldCity.name == updatedCity.name) {
            citiesRef.document(oldCity.name).set(updatedCity)
        } else {
            db.batch()
                .delete(citiesRef.document(oldCity.name))
                .set(citiesRef.document(updatedCity.name), updatedCity)
                .commit()
        }
    }

    fun deleteCity(city: City) {
        citiesRef.document(city.name).delete()
    }
}
