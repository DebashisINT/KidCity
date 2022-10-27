package com.kcteam.features.damageProduct.api



object GetDamageProductRegProvider {

    fun provideSaveButton(): GetDamageProductListRegRepository {
        return GetDamageProductListRegRepository(GetDamageProductListApi.create())
    }
    fun provideDel(): GetDamageProductListRegRepository {
        return GetDamageProductListRegRepository(GetDamageProductListApi.createFacePic())
    }

}