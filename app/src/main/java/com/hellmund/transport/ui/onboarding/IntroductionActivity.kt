package com.hellmund.transport.ui.onboarding

import android.Manifest
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hellmund.transport.R
import com.hellmund.transport.di.injector
import com.hellmund.transport.ui.shared.Navigator
import com.hellmund.transport.util.plusAssign
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_introduction.*
import javax.inject.Inject

class IntroductionActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        injector.inject(this)

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
        compositeDisposable += RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { granted ->
                    if (granted) {
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
                .setPositiveButton(R.string.got_it, null)
                .show()
    }

    private fun finishOnboarding() {
        navigator.finishOnboarding(this)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

}