package com.kcteam.features.addshop.api.assignedToDDList

/**
 * Created by Saikat on 03-10-2018.
 */
object AssignToDDListRepoProvider {
    fun provideAssignDDListRepository(): AssignToDDListRepo {
        return AssignToDDListRepo(AssignToDDListApi.create())
    }
}