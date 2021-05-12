package com.hrb.googlereview

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hrb.googlereview.databinding.ActivityCompleteBinding

class CompleteActivity : AppCompatActivity() {
    private var binding: ActivityCompleteBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_complete)

        binding?.btnGoBack?.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}