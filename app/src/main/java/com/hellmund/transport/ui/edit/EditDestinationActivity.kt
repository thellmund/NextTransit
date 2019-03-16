package com.hellmund.transport.ui.edit

import android.os.Bundle
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.hellmund.transport.R
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.di.ViewModelFactory
import com.hellmund.transport.di.injector
import com.hellmund.transport.ui.edit.di.EditModule
import com.hellmund.transport.util.Constants
import com.hellmund.transport.util.observe
import com.hellmund.transport.util.onCheckedChanged
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.activity_edit_destination.*
import org.jetbrains.anko.inputMethodManager
import javax.inject.Inject
import javax.inject.Provider

class EditDestinationActivity : AppCompatActivity() {

    private val destination: Destination? by lazy {
        intent.getParcelableExtra<Destination?>(Constants.INTENT_DESTINATION)
    }

    @Inject
    lateinit var viewModelProvider: Provider<EditViewModel>

    private val viewModel: EditViewModel by lazy {
        val factory = ViewModelFactory(viewModelProvider)
        ViewModelProviders.of(this, factory).get(EditViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_destination)

        injector.editComponent()
                .editModule(EditModule(this))
                .destination(destination)
                .build()
                .inject(this)

        setupToolbar()
        setupEventProcessing()
        initViews()

        viewModel.viewState.observe(this, this::render)
        viewModel.navigation.observe(this, this::navigate)
    }

    private fun setupEventProcessing() {
        val titleChanges = RxTextView.textChanges(titleInputView)
        viewModel.processTitleChanges(titleChanges)

        val addressChanges = RxTextView.textChanges(addressInputView)
        viewModel.processAddressChanges(addressChanges)

        val saveClicks = RxView.clicks(saveButton)
        viewModel.processSaveClicks(saveClicks)
    }

    private fun render(viewState: EditViewState) {
        if (viewState.showSuggestions) {
            showSuggestions(viewState.suggestions)
        } else {
            addressInputView.dismissDropDown()
        }

        destination?.let {
            supportActionBar?.title = it.title
        }

        if (titleInputView.text.isEmpty() && addressInputView.text.isEmpty()) {
            viewState.title?.let {
                titleInputView.setText(it)
                titleInputView.setSelection(it.length)
            }

            viewState.address?.let {
                addressInputView.setText(it)
                addressInputView.setSelection(it.length)
            }
        }

        if (viewState.showNameSuggestions) {
            initNameSuggestions()
            invalidateNameSuggestions()
        }

        TransitionManager.beginDelayedTransition(scrollView)
        progressIndicator.isVisible = viewState.isLoading
        saveButton.isEnabled = viewState.isSaveEnabled
    }

    private fun navigate(navigationResult: NavigationResult) {
        hideKeyboard()
        when (navigationResult) {
            NavigationResult.Close -> finish()
            NavigationResult.ShowDialog -> displayDiscardInputDialog()
        }
    }

    private fun initViews() {
        titleInputView.requestFocus()
        showKeyboard()
    }

    private fun invalidateNameSuggestions() {
        if (nameSuggestionChips.childCount == 0) {
            initNameSuggestions()
        }

        nameSuggestionChipsContainer.isVisible = titleInputView.text.isEmpty()
        nameSuggestionChipsContainer.scrollTo(0, 0)
    }

    private fun initNameSuggestions() {
        nameSuggestionChips.removeAllViews()
        resources
                .getStringArray(R.array.name_suggestions)
                .map { NameSuggestionChip(this, it) }
                .forEach { nameSuggestionChips.addView(it) }

        nameSuggestionChips.onCheckedChanged { suggestion ->
            titleInputView.setText(suggestion)
            titleInputView.setSelection(suggestion.length)
            addressInputView.requestFocus()
        }
    }

    private fun showSuggestions(suggestions: List<String>) {
        val adapter = ArrayAdapter(this, R.layout.list_item_simple_text, suggestions)
        addressInputView.setAdapter(adapter)
        addressInputView.showDropDown()
        addressInputView.setOnItemClickListener { _, _, position, _ ->
            val suggestion = suggestions[position]
            onSuggestionSelected(suggestion)
        }
    }

    private fun showKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(scrollView.windowToken, 0)
        titleInputView.clearFocus()
        addressInputView.clearFocus()
    }

    private fun displayDiscardInputDialog() {
        AlertDialog.Builder(this)
                .setMessage(getString(R.string.discard_alert_text))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.discard) { _, _ -> finish() }
                .setCancelable(true)
                .show()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp)
    }

    private fun onSuggestionSelected(suggestion: String) {
        viewModel.hideSuggestions()
        addressInputView.setText(suggestion)
        addressInputView.setSelection(suggestion.length)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.handleOnBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        viewModel.handleOnBackPressed()
    }

}