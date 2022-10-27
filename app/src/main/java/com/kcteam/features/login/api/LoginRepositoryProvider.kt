package com.kcteam.features.login.api

/**
 * Created by Pratishruti on 23-11-2017.
 */
object LoginRepositoryProvider {
    fun provideLoginRepository(): LoginRepository {
        return LoginRepository(LoginApi.create())
    }

    fun provideLoginImgRepository(): LoginRepository {
        return LoginRepository(LoginApi.loginImg())
    }
}