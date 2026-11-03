package com.blankon.moni.feature.home.viewmodel

import com.blankon.moni.core.common.base.BaseViewModel
import com.blankon.moni.core.common.utils.state.UiState
import com.blankon.moni.core.common.utils.state.UiState.StateInitial
import com.blankon.moni.core.data.model.local.SampleModelEntity
import com.blankon.moni.core.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeFetchApiViewModel @Inject constructor(
    private val repo: AppRepository
) : BaseViewModel() {

    private val _sampleResponse = MutableStateFlow<UiState<List<SampleModelEntity>>>(StateInitial)
    val sampleResponse = _sampleResponse.asStateFlow()

    fun getSampleData() = collectApiAsUiState(
        response = repo.getListData(),
        updateState = { _sampleResponse.value = it }
    )
}