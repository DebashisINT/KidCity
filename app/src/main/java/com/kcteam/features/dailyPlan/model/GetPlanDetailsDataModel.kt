package com.kcteam.features.dailyPlan.model

import java.io.Serializable

/**
 * Created by Saikat on 24-12-2019.
 */
class GetPlanDetailsDataModel : Serializable {
    var details_id = ""
    var plan_date = ""
    var plan_value = ""
    var plan_remarks = ""
    var achievement_value = ""
    var achievement_remarks = ""
    var percnt = ""
}