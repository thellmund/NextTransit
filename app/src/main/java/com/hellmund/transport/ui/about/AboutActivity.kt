package com.hellmund.transport.ui.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.hellmund.transport.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        window.setBackgroundDrawable(null)
        setVersionNumber()
    }

    private fun setVersionNumber() {
        try {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            val text = String.format(getString(R.string.version), version)
            versionTextView.text = text
        } catch (e: PackageManager.NameNotFoundException) {
            versionTextView.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
