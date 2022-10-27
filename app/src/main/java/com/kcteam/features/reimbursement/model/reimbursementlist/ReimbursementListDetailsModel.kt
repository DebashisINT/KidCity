package com.kcteam.features.reimbursement.model.reimbursementlist

/**
 * Created by Saikat on 28-01-2019.
 */
class ReimbursementListDetailsModel {
    var visit_type_id: String? = null
    var visit_type: String? = null
    var applied_date: String? = null
    var travel_mode_id: String? = null
    var travel_mode: String? = null
    var amount: String? = null
    var hotel_name: String? = null
    var food_type: String? = null
    var remarks: String? = null
    var from_location: String? = null
    var to_location: String? = null
    var hotel_location: String? = null
    var start_date_time: String? = null
    var end_date_time: String? = null
    var distance: String? = null
    var fuel_id: String? = null
    var fuel_type: String? = null
    var maximum_rate: String? = null
    var maximum_allowance: String? = null
    var maximum_distance: String? = null
    var status: String? = null
    var Expense_mapId: String? = null
    var Subexpense_MapId: String? = null
    var approved_amount: String? = null
    var isEditable: String? = null
    var image_list: ArrayList<ReimbursementListImageData>? = null
}