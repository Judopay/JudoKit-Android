package com.judokit.android.examples.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.judokit.android.examples.R
import com.judokit.android.examples.common.startResultActivity
import com.judokit.android.examples.model.Result
import com.judokit.android.examples.result.adapter.ResultActivityAdapter
import kotlinx.android.synthetic.main.activity_result.*

const val RESULT = "com.judokit.android.examples.result"

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
