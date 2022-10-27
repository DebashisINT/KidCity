package com.kcteam.features.dymanicSection.model

import android.widget.CheckBox
import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 19-Aug-20.
 */
data class DynamicResponseModel(var view_list: ArrayList<DynamicDataModel>? = null) : BaseResponse(), Serializable

data class DynamicDataModel(var type: String = "",
                            var id: String = "",
                            var header: String = "",
                            var value: String = "",
                            var text_type: String = "",
                            var max_length: String = "",
                            var item_list: ArrayList<DynamicItemModel>? = null) : Serializable

data class DynamicItemModel(var items: String = "",
                            var isSelected: Boolean = false,
                            var id: String = "") : Serializable

data class DynamicSubmitInputParams(var session_token: String = "",
                                    var user_id: String = "",
                                    var dynamicFormName: String = "",
                                    var id: String = "",
                                    var view_list: ArrayList<DynamicSaveDataModel>? = null) : Serializable

data class EditDynamicInputParams(var session_token: String = "",
                                  var user_id: String = "",
                                  var dynamicFormName: String = "",
                                  var id: String = "",
                                  var view_list: ArrayList<DynamicSaveDataModel>? = null,
                                  var attachments: String = "") : Serializable

data class DynamicSaveDataModel(var grp_id: String = "",
                                var id: String = "",
                                var value: String = "")

data class checkBoxModel(var grp_id: String = "",
                         var checkBox: CheckBox? = null)

data class AllDynamicListResponseModel(var form_list: ArrayList<AllDynamicDataModel>? = null) : BaseResponse(), Serializable

data class AllDynamicDataModel(var id: String = "",
                               var name: String = "") : Serializable

data class DynamicListResponseModel(var info_list: ArrayList<DynamicListDataModel>? = null) : BaseResponse(), Serializable

data class DynamicListDataModel(var id: String = "",
                                var date: String = "",
                                var super_id: String = "",
                                var field_list: ArrayList<DynamicFieldModel>? = null) : Serializable

data class DynamicFieldModel(var key: String = "") : Serializable
