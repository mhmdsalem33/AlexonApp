package com.mhmd.alexon.domain.repository

import com.mhmd.alexon.data.newtwork.DummyApi
import com.mhmd.alexon.domain.models.UserModel
import javax.inject.Inject

class LoginRepository @Inject constructor(private val dummyApi: DummyApi) {
    suspend fun login(userModel: UserModel) = dummyApi.login(userModel)
}