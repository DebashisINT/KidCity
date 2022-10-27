package com.kcteam.features.dailyPlan.model

import java.io.Serializable

/**
 * Created by Saikat on 24-12-2019.
 */
class GetPlanListDataModel : Serializable {
    var plan_id = ""
    var party_name = ""
    var contact_no = ""
    var location = ""
    var last_plan_date = ""
    var last_plan_value = ""
    var last_achv_amount = ""
    var last_plan_feedback = ""
    var last_achv_feedback = ""
}