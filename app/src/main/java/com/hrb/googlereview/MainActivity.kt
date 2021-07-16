package com.hrb.googlereview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.hrb.googlereview.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val ACTIVITY_CALLBACK = 1
    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager
    private lateinit var appUpdateManager: AppUpdateManager

    private val listener: InstallStateUpdatedListener? =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                showSnackBarForCompleteUpdate()
            }
        }

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

        /*Handler().postDelayed({
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
        }, 3000)*/
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appInfo = appUpdateManager.appUpdateInfo
    }

    private fun showSnackBarForCompleteUpdate() {
        val snackbar = Snackbar
            .make(
                findViewById(R.id.parent),
                "Update ready for install", Snackbar.LENGTH_LONG
            )
            .setAction("Install", View.OnClickListener {
                appUpdateManager!!.completeUpdate()
            })
        snackbar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (listener != null) {
            appUpdateManager.unregisterListener(listener)
        }
    }

    override fun onResume() {
        super.onResume()
        if (listener != null) {
            appUpdateManager.registerListener(listener)
        }
        appUpdateManager
            .appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        100
                    )
                } else if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        100
                    );
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(this, "RESULT_OK", Toast.LENGTH_LONG).show()
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this, "RESULT_CANCELED", Toast.LENGTH_LONG).show()
                    finish()
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Toast.makeText(this, "RESULT_IN_APP_UPDATE_FAILED", Toast.LENGTH_LONG).show()
                    finish()
                }
                else -> {
                    Toast.makeText(this, "No result", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}