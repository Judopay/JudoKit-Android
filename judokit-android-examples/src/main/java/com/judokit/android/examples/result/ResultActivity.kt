package com.judokit.android.examples.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.judokit.android.examples.common.parcelable
import com.judokit.android.examples.common.startResultActivity
import com.judokit.android.examples.databinding.ActivityResultBinding
import com.judokit.android.examples.model.Result
import com.judokit.android.examples.result.adapter.ResultActivityAdapter

const val RESULT = "com.judopay.judokit.android.examples.result"

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.parcelable<Result>(RESULT)
        if (result != null) {
            setupRecyclerView(result)
        } else {
            Snackbar
                .make(binding.coordinatorLayout, "Result object not provided", Snackbar.LENGTH_SHORT)
                .show()
            finish()
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupRecyclerView(result: Result) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsets.bottom)
            insets
        }

        binding.toolbar.title = result.title

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL,
            ),
        )

        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        binding.recyclerView.adapter =
            ResultActivityAdapter(result.items) { item ->
                if (item.subResult != null) {
                    startResultActivity(item.subResult)
                } else {
                    val data = ClipData.newPlainText("text", item.value)
                    clipboardManager?.setPrimaryClip(data)
                    Snackbar
                        .make(binding.coordinatorLayout, "Value of '${item.title}' copied to clipboard.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
