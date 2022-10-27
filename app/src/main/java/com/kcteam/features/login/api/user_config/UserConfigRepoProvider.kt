package com.kcteam.features.login.api.user_config

/**
 * Created by Saikat on 14-01-2019.
 */
object UserConfigRepoProvider {
    fun provideUserConfigRepository(): UserConfigRepo {
        return UserConfigRepo(UserConfigApi.create())
    }
}