package com.softvision.jetpackmasterclass.ui.navigation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softvision.jetpackmasterclass.utils.UiState

class FirstViewModel @ViewModelInject constructor() : ViewModel() {

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    fun onViewResumed() {
        _state.value = UiState()
    }
}