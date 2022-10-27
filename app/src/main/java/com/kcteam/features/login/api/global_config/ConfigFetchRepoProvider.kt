package com.kcteam.features.login.api.global_config

/**
 * Created by Saikat on 14-01-2019.
 */
object ConfigFetchRepoProvider {
    fun provideConfigFetchRepository(): ConfigFetchRepo {
        return ConfigFetchRepo(ConfigFetchApi.create())
    }
}