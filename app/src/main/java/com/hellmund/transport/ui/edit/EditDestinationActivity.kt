package com.hellmund.transport.ui.edit

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.systemService
import com.hellmund.transport.R
import com.hellmund.transport.data.model.Suggestion
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.util.*
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_destination.*
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

class EditDestinationActivity : AppCompatActivity() {

    private lateinit var viewModel: EditDestinationActivityViewModel
    private var autocompleteSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_destination)

        val destination: Destination? = intent.getParcelableExtra(Constants.INTENT_DESTINATION)
        val viewModelFactory = EditDestinationActivityViewModelFactory(application, destination)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(EditDestinationActivityViewModel::class.java)

        setupToolbar(destination)
        initViews()

        populateTextFields(destination)
        invalidateNameSuggestions()

    }

    private fun initViews() {
        titleInputView.onChange { input ->
            viewModel.titleInput = input
            invalidateSaveButton()
            invalidateNameSuggestions()
        }

        addressInputView.onChange { input ->
            if (autocompleteSubscription == null) {
                initAutocomplete()
            }

            viewModel.addressInput = input
            invalidateSaveButton()
        }

        addressInputView.onDone {
            autocompleteSubscription?.dispose()
        }

        saveButton.setOnClickListener {
            onSaveButtonClick()
        }

        if (viewModel.isAddAction) {
            titleInputView.requestFocus()
        } else {
            addressInputView.requestFocus()
        }

        showKeyboard()
    }

    private fun initAutocomplete() {
        autocompleteSubscription = RxTextView
                .textChanges(addressInputView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map { characters -> characters.toString() }
                .filter { input -> input.length >= 3 }
                .subscribe { input -> downloadSuggestions(input) }
    }

    private fun invalidateSaveButton() {
        TransitionManager.beginDelayedTransition(scrollView)
        saveButton.isEnabled = viewModel.isSaveButtonEnabled
    }

    private fun invalidateNameSuggestions() {
        val showSuggestions = titleInputView.text.isEmpty()
        if (showSuggestions) {
            initNameSuggestions()
        }

        TransitionManager.beginDelayedTransition(scrollView)
        nameSuggestionChips.visibility = if (showSuggestions) View.VISIBLE else View.GONE
    }

    private fun initNameSuggestions() {
        val suggestions = resources.getStringArray(R.array.name_suggestions)
        suggestions.forEach { suggestion ->
            val chip = Chip(this).apply {
                text = suggestion
                isCheckable = true
                isCheckedIconVisible = false
                textColor = Color.WHITE
                setChipBackgroundColorResource(R.color.chip_color)
            }
            nameSuggestionChips.addView(chip)
        }

        nameSuggestionChips.setOnCheckedChangeListener { _, index ->
            // Index is one-indexed
            val suggestion = suggestions[index - 1]
            titleInputView.setText(suggestion)
            titleInputView.setSelection(suggestion.length)

            invalidateNameSuggestions()
            addressInputView.requestFocus()
        }
    }

    private fun onNewSuggestions(suggestions: List<Suggestion>) {
        val addresses = suggestions.map { it.address }
        val adapter =
                ArrayAdapter(this, R.layout.list_item_simple_text, addresses)

        addressInputView.setAdapter(adapter)
        addressInputView.showDropDown()
        addressInputView.setOnItemClickListener { _, _, position, _ ->
            val suggestion = suggestions[position]
            onSuggestionSelected(suggestion)
            invalidateOptionsMenu()
        }
    }

    private fun downloadSuggestions(input: String) {
        progressBar.show()
        viewModel.getSuggestions(input).observe(this) { results ->
            progressBar.hide()
            onNewSuggestions(results)
        }
    }

    private fun onSaveButtonClick() {
        viewModel.saveDestination()
        hideKeyboard()
        finish()
    }

    private fun showKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun hideKeyboard() {
        val inputMethodManager = systemService<InputMethodManager>()
        inputMethodManager.hideSoftInputFromWindow(scrollView.windowToken, 0)

        titleInputView.clearFocus()
        addressInputView.clearFocus()
    }

    private fun displayDiscardInputDialog() {
        AlertDialog.Builder(this)
                .setMessage(getString(R.string.discard_alert_text))
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.discard) { _, _ -> finish() }
                .setCancelable(true)
                .show()
    }

    private fun setupToolbar(destination: Destination?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)

        destination?.let {
            supportActionBar?.title = it.title
        }
    }

    private fun populateTextFields(destination: Destination?) {
        titleInputView.apply {
            destination?.let {
                setText(it.title)
                setSelection(it.title.length)
            }
        }

        addressInputView.apply {
            destination?.let {
                setText(it.address)
                setSelection(it.address.length)
            }
        }
    }

    private fun onSuggestionSelected(suggestion: Suggestion) {
        suggestion.address?.let {
            viewModel.addressInput = it
            addressInputView.setText(it)
            addressInputView.setSelection(it.length)
        }

        autocompleteSubscription?.dispose()
        addressInputView.dismissDropDown()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            hideKeyboard()
            onBackButtonPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun onBackButtonPressed() {
        val handled = viewModel.handleOnBackPressed()
        if (handled) {
            finish()
        } else {
            displayDiscardInputDialog()
        }
    }

    override fun onBackPressed() {
        onBackButtonPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        autocompleteSubscription?.dispose()
    }

}