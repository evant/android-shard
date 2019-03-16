package me.tatarka.shard.sample

import android.os.Bundle
import android.view.View
import me.tatarka.shard.app.ShardActivity

class ResultActivity : ShardActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        findViewById<View>(R.id.ok).setOnClickListener { setResult(RESULT_OK); finish() }
        findViewById<View>(R.id.cancel).setOnClickListener { setResult(RESULT_CANCELED); finish() }
    }
}
