package com.example.leafy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leafy.adapter.TrendingAdapter
import com.example.leafy.api.RetrofitClient
import com.example.leafy.api.TheMealDBApi
import com.example.leafy.model.MealResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var trendingAdapter: TrendingAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        try {
            recyclerView = findViewById(R.id.rvTrending)
            recyclerView.layoutManager = LinearLayoutManager(this)
            trendingAdapter = TrendingAdapter { meal ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("MEAL", meal)
                startActivity(intent)
            }
            recyclerView.adapter = trendingAdapter

            fetchMeals("a")
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error initializing views: ${e.message}")
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchMeals(query: String) {
        val theMealDBApi = RetrofitClient.getClient().create(TheMealDBApi::class.java)
        theMealDBApi.searchMeals(query).enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                if (response.isSuccessful) {
                    val meals = response.body()?.meals ?: emptyList()
                    trendingAdapter.updateMeals(meals)
                } else {
                    showError("Response not successful")
                }
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                showError("API call failed: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Log.e("HomeActivity", message)
        Toast.makeText(this, "Failed to fetch meals: $message", Toast.LENGTH_SHORT).show()
    }
}
