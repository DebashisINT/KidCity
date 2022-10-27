package com.kcteam.features.login.model

import java.io.Serializable

/**
 * Created by Saikat on 26-Jun-20.
 */
data class AlarmSelfieInput(var user_id: String = "",
                            var session_token: String = "",
                            var lat: String = "",
                            var longs: String = "",
                            var dateTime: String = "",
                            var report_id: String = "") : Serializable