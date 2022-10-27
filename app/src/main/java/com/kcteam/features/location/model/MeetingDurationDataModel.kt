package com.kcteam.features.location.model

import java.io.Serializable

/**
 * Created by Saikat on 15-01-2020.
 */
class MeetingDurationDataModel : Serializable {
    var remarks = ""
    var latitude = "0.0"
    var longitude = "0.0"
    var duration = "00:00:00"
    var meeting_type_id = ""
    var distance_travelled = ""
    var date = ""
    var address = ""
    var pincode = ""
    var date_time = ""
}