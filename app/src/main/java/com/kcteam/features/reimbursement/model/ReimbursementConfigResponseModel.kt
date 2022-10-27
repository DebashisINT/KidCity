package com.kcteam.features.reimbursement.model

import com.kcteam.base.BaseResponse

/**
 * Created by Saikat on 24-01-2019.
 */
class ReimbursementConfigResponseModel : BaseResponse() {
    var reimbursement_past_days: String? = null
    var visittype_details: ArrayList<ReimbursementConfigVisitTypeDataModel>? = null
    var expense_types: ArrayList<ReimbursementConfigExpenseTypeModel>? = null
    var mode_of_travel: ArrayList<ReimbursementConfigModeOfTravelDataModel>? = null
    var fuel_types: ArrayList<ReimbursementConfigFuelTypeModel>? = null
    var isEditable: Boolean? = null
    var isAttachmentMandatoryForLocal: Boolean? = null
    var isAttachmentMandatoryForOutstation: Boolean? = null
}