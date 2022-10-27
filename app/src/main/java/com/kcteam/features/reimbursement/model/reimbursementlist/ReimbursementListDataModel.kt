package com.kcteam.features.reimbursement.model.reimbursementlist

/**
 * Created by Saikat on 28-01-2019.
 */
class ReimbursementListDataModel {
    var expense_type_id: String? = null
    var expense_type: String? = null
    var expense_type_image: String? = null
    var total_amount: String? = null
    var isSelected = false
    var expense_list_details: ArrayList<ReimbursementListDetailsModel>? = null
}