package com.judopay.samples.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.judopay.samples.R
import com.judopay.samples.common.startResultActivity
import com.judopay.samples.model.Result
import com.judopay.samples.result.adapter.ResultActivityAdapter
import kotlinx.android.synthetic.main.activity_result.*

const val RESULT = "com.judopay.samples.result"

class ResultActivity : AppCompatActivity() {

    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val result = intent.getParcelableExtra<Result>(RESULT)
        if (result != null) {
            setupRecyclerView(result)
        } else {
            Snackbar.make(coordinatorLayout, "Result object not provided", Snackbar.LENGTH_SHORT)
                .show()
            finish()
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupRecyclerView(result: Result) {
        title = result.title

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        recyclerView.adapter = ResultActivityAdapter(result.items) { item ->
            if (item.subResult != null) {
                startResultActivity(item.subResult)
            } else {
                val data = ClipData.newPlainText("text", item.value)
                clipboardManager.setPrimaryClip(data)
                Snackbar.make(coordinatorLayout, "Value of '${item.title}' copied to clipboard.", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}