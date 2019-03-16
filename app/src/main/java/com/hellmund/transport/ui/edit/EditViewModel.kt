package com.hellmund.transport.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.data.repos.DestinationsRepository
import com.hellmund.transport.data.repos.SuggestionsRepository
import com.hellmund.transport.util.plusAssign
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.lang.Math.abs
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EditViewModel @Inject constructor(
        private val destinationsRepo: DestinationsRepository,
        private val suggestionsRepo: SuggestionsRepository,
        private val destination: Destination?
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var titleInput = destination?.title.orEmpty()
    private var addressInput = destination?.address.orEmpty()

    private val isSaveEligible: Boolean
        get() = titleInput.isNotBlank() && addressInput.isNotBlank()

    private val hasMadeChanges: Boolean
        get() {
            val changedName = destination?.title.orEmpty() != titleInput
            val changedAddress = destination?.address.orEmpty() != addressInput
            return changedName || changedAddress
        }

    private val actionsRelay = PublishRelay.create<Action>()
    private val navigationsRelay = PublishRelay.create<NavigationResult>()

    private val _viewState = MutableLiveData<EditViewState>()
    val viewState: LiveData<EditViewState> = _viewState

    private val _navigation = MutableLiveData<NavigationResult>()
    val navigation: LiveData<NavigationResult> = _navigation

    init {
        val initialViewState = EditViewState.initial(destination)
        compositeDisposable += actionsRelay
                .switchMap(this::processAction)
                .scan(initialViewState, this::reduceState)
                .subscribe(_viewState::postValue)

        compositeDisposable += navigationsRelay.subscribe(_navigation::postValue)
    }

    private fun processAction(action: Action): Observable<Result> {
        return when (action) {
            is Action.TitleChanged -> handleTitleChanged(action.title)
            is Action.AddressChanged -> handleAddressChanged(action.address)
            is Action.LoadSuggestions -> fetchSuggestions(action.input)
            Action.HideSuggestions -> Observable.just(Result.HideSuggestions)
            Action.SaveButtonClicked -> handleSaveAction()
        }
    }

    private fun handleTitleChanged(title: String): Observable<Result> {
        titleInput = title
        return Observable.just(if (isSaveEligible) Result.EnableSave else Result.DisableSave)
    }

    private fun handleAddressChanged(address: String): Observable<Result> {
        addressInput = address
        val saveButtonResult = if (isSaveEligible) Result.EnableSave else Result.DisableSave
        return fetchSuggestions(address).startWith(saveButtonResult)
    }

    private fun reduceState(
            viewState: EditViewState,
            result: Result
    ): EditViewState {
        return when (result) {
            Result.ShowLoading -> viewState.toLoading()
            is Result.SuggestionsLoaded -> viewState.toData(result.suggestions)
            Result.SuggestionsLoadingError -> viewState.toError()
            Result.HideSuggestions -> viewState.toNoData()
            Result.EnableSave -> viewState.toSaveEnabled(true)
            Result.DisableSave -> viewState.toSaveEnabled(false)
            Result.None -> viewState
        }
    }

    private fun fetchSuggestions(input: String): Observable<Result> {
        return suggestionsRepo
                .getSuggestions(input)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .map { it.mapNotNull { suggestion -> suggestion.address } }
                .map { Result.SuggestionsLoaded(it) as Result }
                .onErrorReturn { Result.SuggestionsLoadingError }
                .startWith(Result.ShowLoading)
    }

    fun processTitleChanges(observable: Observable<CharSequence>) {
        compositeDisposable += observable
                .map { it.toString() }
                .map { Action.TitleChanged(it) }
                .subscribe(actionsRelay::accept, Timber::e)
    }

    fun processAddressChanges(observable: Observable<CharSequence>) {
        compositeDisposable += observable
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .filter { input -> input.length >= 3 }
                .filter { abs(it.length - titleInput.length) <= 1 }
                .map { it.toString() }
                .map { Action.AddressChanged(it) }
                .subscribe(actionsRelay::accept, Timber::e)
    }

    fun processSaveClicks(observable: Observable<Any>) {
        compositeDisposable += observable
                .map { Action.SaveButtonClicked }
                .subscribe(actionsRelay::accept, Timber::e)
    }

    fun hideSuggestions() {
        actionsRelay.accept(Action.HideSuggestions)
    }

    fun handleOnBackPressed() {
        val result = if (hasMadeChanges) NavigationResult.ShowDialog else NavigationResult.Close
        navigationsRelay.accept(result)
    }

    private fun handleSaveAction(): Observable<Result> {
        return Observable
                .fromCallable { saveDestination(titleInput, addressInput) }
                .map { Result.None as Result }
                .doOnNext { navigationsRelay.accept(NavigationResult.Close) }
    }

    private fun saveDestination(title: String, address: String) {
        if (destination != null) {
            updateDestination(title, address)
        } else {
            createDestination(title, address)
        }
    }

    private fun updateDestination(title: String, address: String) {
        doAsync {
            val destination = destination ?: throw IllegalStateException()
            val newDestination = destination.updateWithInput(title, address)
            destinationsRepo.updateDestination(newDestination)
        }
    }

    private fun createDestination(title: String, address: String) {
        compositeDisposable += Observable.fromCallable(destinationsRepo::getSize)
                .subscribeOn(Schedulers.io())
                .map { Destination.create(it, title, address) }
                .subscribe(destinationsRepo::insertDestination)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
