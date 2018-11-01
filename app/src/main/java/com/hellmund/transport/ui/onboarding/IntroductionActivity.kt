package com.hellmund.transport.ui.onboarding

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import com.hellmund.transport.R
import com.hellmund.transport.ui.destinations.MainActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : AppCompatActivity() {

    private var dispoable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        window.setBackgroundDrawable(null)

        allowAccessButton.setOnClickListener { requestLocationAccess() }
        whyButton.setOnClickListener { showLocationRequestRationale() }

        val htmlImageCredit = getString(R.string.introduction_header_image_credit)
        val imageCredit = Html.fromHtml(htmlImageCredit)
        imageCreditTextView.apply {
            text = imageCredit
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun requestLocationAccess() {
        dispoable = RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { granted ->
                    if (granted!!) {
                        finishOnboarding()
                    } else {
                        showLocationRequestRationale()
                    }
                }
    }

    private fun showLocationRequestRationale() {
        AlertDialog.Builder(this)
                .setTitle(R.string.why_location_access)
                .setMessage(R.string.why_location_access_explanation)
                .setPositiveButton(R.string.got_it) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun finishOnboarding() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispoable?.dispose()
    }

}