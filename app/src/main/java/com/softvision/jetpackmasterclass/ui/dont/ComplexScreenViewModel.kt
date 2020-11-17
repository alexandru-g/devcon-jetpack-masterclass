package com.softvision.jetpackmasterclass.ui.dont

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softvision.jetpackmasterclass.utils.UiState

class ComplexScreenViewModel @ViewModelInject constructor(
    private val otherViewModel: OtherViewModel
) : ViewModel() {

    val state: LiveData<UiState> = MutableLiveData()

    val somethingHappened: LiveData<Unit> = MutableLiveData()

    init {
        // ...
        somethingHappened.observeForever {
            otherViewModel.notifySomethingHappened()
        }
    }
}