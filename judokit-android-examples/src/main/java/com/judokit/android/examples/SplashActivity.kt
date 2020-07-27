package com.judokit.android.examples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.judokit.android.examples.feature.DemoFeatureListActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, DemoFeatureListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
