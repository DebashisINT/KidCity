package com.kcteam.features.dashboard.presentation.api.submithomeloc

/**
 * Created by Saikat on 13-03-2019.
 */
object SubmitHomeLocationRepoProvider {
    fun submitHomeLocRepo(): SubmitHomeLocationRepo {
        return SubmitHomeLocationRepo(SubmitHomeLocationApi.create())
    }
}