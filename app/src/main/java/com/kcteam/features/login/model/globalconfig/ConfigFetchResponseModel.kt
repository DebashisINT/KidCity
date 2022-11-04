package com.kcteam.features.login.model.globalconfig

import com.kcteam.base.BaseResponse

/**
 * Created by Saikat on 14-01-2019.
 */
class ConfigFetchResponseModel : BaseResponse() {
    var min_accuracy: String? = "200"
    var max_accuracy: String? = "1500"
    var min_distance: String? = null
    var max_distance: String? = "1000"
    var idle_time: String? = null
    var willStockShow: Boolean? = null
    var maxFileSize: String? = "400"
    var willKnowYourStateShow: Boolean? = null
    var willAttachmentCompulsory: Boolean? = null
    var canAddBillingFromBillingList: Boolean? = null
    var willShowUpdateDayPlan: Boolean? = null
    var updateDayPlanText: String? = "Update Day Plan"
    var dailyPlanListHeaderText: String? = "List of Party"
    var allPlanListHeaderText: String? = "Plan/Achievement Details"
    var willSetYourTodaysTargetVisible: Boolean? = null
    var attendenceAlertHeading: String? = ""
    var attendenceAlertText: String? = ""
    var meetingText: String? = ""
    var meetingDistance: String? = ""
    var updateBillingText: String? = ""
    var isRateOnline: Boolean? = null
    var ppText: String = ""
    var ddText: String = ""
    /*var isReplaceShopText: Boolean? = null
    var isQuotationShow: Boolean? = null*/
    var shopText: String = ""
    var isCustomerFeatureEnable: Boolean? = null
    var isAreaVisible: Boolean? = null
    var cgstPercentage: String = ""
    var sgstPercentage: String = ""
    var tcsPercentage: String = ""
    var docAttachmentNo: String = ""
    var chatBotMsg: String = ""
    var contactMail: String = ""
    var isVoiceEnabledForAttendanceSubmit: Boolean? = null
    var isVoiceEnabledForOrderSaved: Boolean? = null
    var isVoiceEnabledForInvoiceSaved: Boolean? = null
    var isVoiceEnabledForCollectionSaved: Boolean? = null
    var isVoiceEnabledForHelpAndTipsInBot: Boolean? = null


    //From Hahnemann
    var isRevisitCaptureImage: Boolean? = null
    var isShowAllProduct: Boolean? = null
    var isPrimaryTargetMandatory: Boolean? = null
    var isStockAvailableForAll: Boolean? = null
    var isStockAvailableForPopup: Boolean? = null
    var isOrderAvailableForPopup: Boolean? = null
    var isCollectionAvailableForPopup: Boolean? = null
    var isDDFieldEnabled: Boolean? = null
    var isActivatePJPFeature: Boolean? = null
    var willReimbursementShow: Boolean? = null

    var GPSAlert: Boolean? = null

    //02-11-2021
    var IsDuplicateShopContactnoAllowedOnline: Boolean? = null

    //26-11-2021
    var BatterySetting: Boolean? = null
    var PowerSaverSetting: Boolean? = null
    /*1-12-2021*/
    var IsnewleadtypeforRuby: Boolean? = null

    /*16-12-2021 return features*/
    var IsReturnActivatedforPP: Boolean? = null
    var IsReturnActivatedforDD: Boolean? = null
    var IsReturnActivatedforSHOP: Boolean? = null


    var FaceRegistrationFrontCamera: Boolean? = null
    var MRPInOrder: Boolean? = null
    var SqMtrRateCalculationforQuotEuro: Double? = null

    var NewQuotationRateCaption: String = ""
    var NewQuotationShowTermsAndCondition: Boolean? = null
    var IsCollectionEntryConsiderOrderOrInvoice: Boolean? = null
    var contactNameText: String = ""
    var contactNumberText: String = ""
    var emailText: String = ""
    var dobText: String = ""
    var dateOfAnniversaryText: String = ""
    var ShopScreenAftVisitRevisit:Boolean? = null
    var IsSurveyRequiredforNewParty:Boolean? = null
    var IsSurveyRequiredforDealer:Boolean? = null
    var IsShowHomeLocationMap:Boolean? = null

    var IsBeatRouteAvailableinAttendance:Boolean? = null
    var IsAllBeatAvailable:Boolean? = null
    var BeatText: String = ""
    var TodaysTaskText:String = ""
    var IsDistributorSelectionRequiredinAttendance:Boolean? = null
    var IsAllowNearbyshopWithBeat:Boolean? = null







}