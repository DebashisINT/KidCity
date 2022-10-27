package com.kcteam.features.member.api

/**
 * Created by Saikat on 29-01-2020.
 */
object TeamRepoProvider {
    fun teamRepoProvider(): TeamRepo {
        return TeamRepo(TeamApi.create())
    }
}