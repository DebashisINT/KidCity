package com.kcteam.features.addshop.api.assignToPPList

/**
 * Created by Saikat on 03-10-2018.
 */
object AssignToPPListRepoProvider {
    fun provideAssignPPListRepository(): AssignToPPListRepo {
        return AssignToPPListRepo(AssignToPPListApi.create())
    }
}