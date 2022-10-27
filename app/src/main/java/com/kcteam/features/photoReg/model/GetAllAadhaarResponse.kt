package com.kcteam.features.photoReg.model

class GetAllAadhaarResponse {
    var status:String ? = null
    var message:String ? = null
    var all_aadhaar_list :ArrayList<AllUserAadhaarList>? = null
}

data class AllUserAadhaarList(var user_id:Int,var user_login_id:String,var RegisteredAadhaarNo:String)