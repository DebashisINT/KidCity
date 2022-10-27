package com.kcteam.features.activities.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

data class ActivityDropdownListResponseModel(var activity_list: ArrayList<ActivityDropdownListDataModel>? = null) : BaseResponse(), Serializable

data class ActivityDropdownListDataModel(var id: String = "",
                                         var name: String = "") : Serializable

data class TypeListResponseModel(var type_list: ArrayList<TypeDataModel>? = null) : BaseResponse(), Serializable

data class TypeDataModel(var id: String = "",
                         var name: String = "",
                         var activityId: String = "") : Serializable

data class PriorityListResponseModel(var priority_list: ArrayList<ActivityDropdownListDataModel>? = null) : BaseResponse(), Serializable

data class AddActivityInputModel(var session_token: String = "",
                                 var user_id: String = "",
                                 var id: String = "",
                                 var party_id: String = "",
                                 var date: String = "",
                                 var time: String = "",
                                 var name: String = "",
                                 var activity_id: String = "",
                                 var type_id: String = "",
                                 var product_id: String = "",
                                 var subject: String = "",
                                 var details: String = "",
                                 var duration: String = "",
                                 var priority_id: String = "",
                                 var due_date: String = "",
                                 var due_time: String = "") : Serializable

data class ActivityListResponseModel(var activity_list: ArrayList<ActivityListDataModel>? = null) : BaseResponse(), Serializable

data class ActivityListDataModel(var session_token: String = "",
                                 var user_id: String = "",
                                 var id: String = "",
                                 var party_id: String = "",
                                 var date: String = "",
                                 var time: String = "",
                                 var name: String = "",
                                 var activity_id: String = "",
                                 var type_id: String = "",
                                 var product_id: String = "",
                                 var subject: String = "",
                                 var details: String = "",
                                 var duration: String = "",
                                 var priority_id: String = "",
                                 var due_date: String = "",
                                 var due_time: String = "",
                                 var attachments: String = "",
                                 var image: String = "") : Serializable

data class ActivityImage(var link: String = "",
                         var type: String = "") : Serializable

data class DoctorListResponseModel(var doc_visit_list: ArrayList<DoctorListDataModel>? = null) : BaseResponse(), Serializable

class DoctorListDataModel : Serializable {
    var shop_id = ""
    var doc_visit_id = ""
    var doc_remarks = ""
    var is_prescriber = -1
    var is_qty = -1
    var qty_vol_text = ""
    var is_sample = -1
    var is_crm = -1
    var is_money = -1
    var next_visit_date = ""
    var remarks_mr = ""
    var product_list = ArrayList<AddChemistProductModel>()
    var amount = ""
    var what = ""
    var from_cme_date = ""
    var to_crm_date = ""
    var crm_volume = ""
    var is_gift = -1
    var qty_product_list = ArrayList<AddChemistProductModel>()
    var sample_product_list = ArrayList<AddChemistProductModel>()
    var which_kind = ""
}

class AddChemistProductModel : Serializable {
    var product_id = ""
    var product_name = ""
}

class AddChemistVisitInputModel : Serializable {
    var user_id = ""
    var session_token = ""
    var shop_id = ""
    var chemist_visit_id = ""
    var volume = ""
    var remarks = ""
    var next_visit_date = ""
    var remarks_mr = ""
    var product_list = ArrayList<AddChemistProductModel>()
    var isPob = -1
    var pob_product_list = ArrayList<AddChemistProductModel>()
}

class AddDoctorVisitInputModel : Serializable {
    var user_id = ""
    var session_token = ""
    var shop_id = ""
    var doc_visit_id = ""
    var doc_remarks = ""
    var is_prescriber = -1
    var is_qty = -1
    var qty_vol_text = ""
    var is_sample = -1
    var is_crm = -1
    var is_money = -1
    var next_visit_date = ""
    var remarks_mr = ""
    var product_list = ArrayList<AddChemistProductModel>()
    var amount = ""
    var what = ""
    var from_cme_date = ""
    var to_crm_date = ""
    var crm_volume = ""
    var is_gift = -1
    var qty_product_list = ArrayList<AddChemistProductModel>()
    var sample_product_list = ArrayList<AddChemistProductModel>()
    var which_kind = ""
}

data class ChemistVisitResponseModel(var chemist_visit_list: ArrayList<ChemistVisitDataModel>? = null) : Serializable, BaseResponse()

class ChemistVisitDataModel : Serializable {
    var shop_id = ""
    var chemist_visit_id = ""
    var volume = ""
    var remarks = ""
    var next_visit_date = ""
    var remarks_mr = ""
    var product_list = ArrayList<AddChemistProductModel>()
    var isPob = -1
    var pob_product_list = ArrayList<AddChemistProductModel>()
}


class ProductListModel : Serializable {

    var id: Int = 0

    var date: String? = null

    var product_name: String? = null

    var brand_id: String? = null

    var brand: String? = null

    var category_id: String? = null

    var category: String? = null

    var watt_id: String? = null

    var watt: String? = null

    var isChecked = false
}