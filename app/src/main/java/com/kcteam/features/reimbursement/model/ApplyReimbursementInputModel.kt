package com.kcteam.features.reimbursement.model

/**
 * Created by Saikat on 25-01-2019.
 */
class ApplyReimbursementInputModel {
    var session_token = ""
    var user_id = ""
    var state_id = ""
    var date = ""
    var visit_type_id = ""
    var Expense_mapId = ""
    var expense_details = ArrayList<ApplyReimbursementDataInputModel>()
}