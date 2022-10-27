package com.kcteam.features.location.model

import java.io.Serializable

/**
 * Created by Saikat on 15-01-2020.
 */
class MeetingDurationInputParams : Serializable {
    var session_token = ""
    var user_id = ""
    var meeting_list = ArrayList<MeetingDurationDataModel>()
}