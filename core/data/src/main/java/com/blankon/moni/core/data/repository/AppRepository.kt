package com.blankon.moni.core.data.repository

import com.blankon.moni.core.common.utils.state.ApiState
import com.blankon.moni.core.data.model.local.SampleModelEntity
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getListData(): Flow<ApiState<List<SampleModelEntity>>>
}