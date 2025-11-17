package com.example.kesi.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kesi.data.model.MainTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

): ViewModel() {
    private val _tabState = MutableLiveData<MainTab>()
    val tabState: LiveData<MainTab> get() = _tabState


    fun move(tabState: MainTab) = viewModelScope.launch {
        _tabState.value = tabState
    }
}