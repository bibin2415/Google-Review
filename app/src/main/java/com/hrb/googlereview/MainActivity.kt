package com.hrb.googlereview

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.hrb.googlereview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val ACTIVITY_CALLBACK = 1
    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        //Create the ReviewManager instance
        reviewManager = ReviewManagerFactory.create(this)

        //Request a ReviewInfo object ahead of time (Pre-cache)
        val requestFlow = reviewManager.requestReviewFlow()
        requestFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                //Received ReviewInfo object
                reviewInfo = request.result
            } else {
                //Problem in receiving object
                reviewInfo = null
            }
        }

        Handler().postDelayed({
            reviewInfo?.let {
                val flow = reviewManager.launchReviewFlow(this@MainActivity, it)
                flow.addOnSuccessListener {
                    //Showing toast is only for testing purpose, this shouldn't be implemented
                    //in production app.
                }
                flow.addOnFailureListener {
                    //Showing toast is only for testing purpose, this shouldn't be implemented
                    //in production app.
                }
                flow.addOnCompleteListener {
                    //Showing toast is only for testing purpose, this shouldn't be implemented
                    //in production app.
                }
            }
        }, 3000)
    }


}