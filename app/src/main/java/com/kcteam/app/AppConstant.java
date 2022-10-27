package com.kcteam.app;

/**
 * Created by sayantan.sarkar on 2/11/17.
 */

public interface AppConstant {
    String DBNAME = "fts_db";
    String LOCATION_TABLE = "location_db";
    String SHOP_TABLE = "shop_detail";
    String SHOP_TABLE_ALL_TEAM = "shop_detail_all_team";
    String ATTENDANCE_TABLE = "attendance";
    String SHOP_ACTIVITY = "shop_activity";
    String GPS_STATUS = "gps_status";
    String STATE_TABLE = "state_list";
    String MARKETING_CATEGORY_TABLE = "marketing_category";
    String MARKETING_CATEGORY_MASTER_TABLE = "marketing_category_master_table";
    String MARKETING_IMAGE = "marketing_image";
    String CITY_TABLE = "city_list";

    /* Added for new implementation */
    String TA_TABLE = "ta_list";
    boolean DATA_ADDED = false;
    String ASSIGNED_TO_PP_TABLE = "assignedto_pp";
    String ASSIGNED_TO_DD_TABLE = "assignedto_dd";
    String WORK_TYPE_TABLE = "work_type";
    String ORDER_LIST_TABLE = "order_list";
    String ORDER_DETAILS_LIST_TABLE = "order_details_list";
    String SHOP_VISIT_IMAGE_TABLE = "shop_visit_image";
    String SHOP_VISIT_COMPETETOR_IMAGE_TABLE = "shop_visit_competetor_image";
    String SHOP_ORDER_STATUS_REMARKS_TABLE = "shop_order_status_remarks";
    String SHOP_CURRENT_STOCK_TABLE = "shop_current_stock_list";
    String SHOP_CURRENT_STOCK_PRODUCTS_TABLE = "shop_current_stock_products_list";
    String SHOP_COMTETETOR_STOCK_TABLE = "shop_competetor_stock_list";
    String SHOP_COMTETETOR_STOCK_PRODUCTS_TABLE = "shop_competetor_stock_products_list";
    String SHOP_TYPE_STOCK_VIEW_STATUS = "shop_type_stock_view_status";
    String UPDATE_STOCK_TABLE = "update_stock";
    String PERFORMANCE_TABLE = "performance";
    String GPS_STATUS_TABLE = "gps_status";
    String COLLECTION_LIST_TABLE = "collection_list";
    String INACCURATE_LOCATION_TABLE = "inaccurate_location_db";
    String LEAVE_TYPE_TABLE = "leave_type_table";
    String ROUTE_TABLE = "route_table";
    String ROUTE_SHOP_LIST_TABLE = "route_shop_list_table";
    String PRODUCT_LIST_TABLE = "product_list";
    String ORDER_PRODUCT_LIST_TABLE = "order_product_list";
    String STOCK_LIST_TABLE = "stock_list";
    String SELECTED_WORK_TYPE_TABLE = "selected_work_type";
    String SELECTED_ROUTE_LIST_TABLE = "selected_route_list";
    String SELECTED_ROUTE_TYPE_SHOP_LIST_TABLE = "selected_route_shop_list";
    String UPDATE_OUTSTANDING_TABLE = "update_outstanding";
    String ALL_LOCATION_TABLE = "all_location_table";
    String IDEAL_LOCATION_TABLE = "ideal_location";
    String BILLING_TABLE = "billing_list";
    String STOCK_DETAILS_LIST = "stock_details_list";
    String STOCK_PRODUCT_LIST = "stock_product_list";
    String BILL_PRODUCT_LIST_TABLE = "bill_product_list";
    String MEETING = "meeting_list";
    String MEETING_TYPE = "meeting_type";
    String PRODUCT_RATE_TABLE = "product_rate";
    String AREA_LIST_TABLE = "area_list";
    String SHOP_TYPE = "shop_type_list";
    String PJP_LIST_TABLE = "pjp_list";
    String MODEL_TABLE = "model_list";
    String PRIMARY_APPLICATION_TABLE = "primary_application_list";
    String SECONDARY_APPLICATION_TABLE = "secondary_application_list";
    String LEAD_TABLE = "lead_list";
    String STAGE_TABLE = "stage_list";
    String FUNNEL_STAGE_TABLE = "funnel_stage_list";
    String BSLIST_TABLE = "bs_list";
    String QUOTATION_TABLE = "quotation_list";
    String TYPE_TABLE = "type_list";
    String MEMBER_TABLE = "member_list";
    String MEMBER_SHOP_TABLE = "member_shop_list";
    String MEMBER_AREA_TABLE = "member_area_list";
    String TIMESHEET_LIST = "timesheet_list";
    String CLIENT_LIST = "client_list";
    String PROJECT_LIST = "project_list";
    String ACTIVITY_LIST = "activity_list";
    String PRODUCT_LIST = "timsheet_product_list";
    String SHOP_VISIT_AUDIO_TABLE = "shop_visit_audio";
    String TASK_TABLE = "task_list";
    String BATTERY_NET_TABLE = "battery_net_status_list";
    String ACTIVITY_DROPDOWN_TABLE = "activity_dropdown";
    String TYPE = "type";
    String PRIORITY_TABLE = "priority_list";
    String Activity = "activity";
    String CHEMIST_VISIT_LIST_TABLE = "chemist_visit_list";
    String CHEMIST_VISIT_PRODUCT_TABLE = "chemist_visit_product_list";
    String DOCTOR_VISIT_LIST_TABLE = "doctor_visit_list";
    String DOCTOR_VISIT_PRODUCT_TABLE = "doctor_visit_product_list";
    String DOCUMENT_TYPE_TABLE = "document_type";
    String DOCUMENT_LIST_TABLE = "document_list";
    String PAYMENT_MODE_TABLE = "payment_mode";
    String ENTITY_LIST_TABLE = "entity_list";
    String PARTY_STATUS_TABLE = "party_status";
    String RETAILER_TABLE = "retailer_list";
    String DEALER_TABLE = "dealer_list";
    String BEAT_TABLE = "beat_list";
    String ASSIGNED_TO_SHOP_TABLE = "assignedto_shop";
    String VISIT_REMARKS_TABLE = "visit_remarks";

    /*App Intent Action*/
    String STARTFOREGROUND_ACTION = "start_foreground";
    String STOPFOREGROUND_ACTION = "stop_foreground";
    String MAIN_ACTION = "main_actionable";


    /*Foreground service intent*/
    int FOREGROUND_SERVICE = 911;

    //03-09-2021
    String NEW_ORDER_GENDER = "new_order_gender";
    String NEW_ORDER_PRODUCT = "new_order_product";
    String NEW_ORDER_COLOR = "new_order_color";
    String NEW_ORDER_SIZE = "new_order_size";
    String NEW_ORDER_ENTRY = "new_order_entry";


    boolean isImageNotFound = false;

    String PROSPECT_TABLE_MASTER = "prospect_list_master";
    String QUESTION_TABLE_MASTER = "question_list_master";
    String QUESTION_TABLE_SUBMIT = "question_list_submit";

    String ADDSHOP_SECONDARY_IMG_TABLE = "tbl_addShop_Secondary_Img";



    String RETURN_DETAILS_TABLE = "tbl_return_details";
    String RETURN_PRODUCT_LIST_TABLE = "return_product_list";

    String TBL_USER_WISE_LEAVE_LIST = "tbl_user_wise_leave_list";

    String TBL_SHOP_FEEDBACK = "tbl_shop_deefback";
    String TBL_SHOP_FEEDBACK_TEMP = "tbl_shop_deefback_temp";
    String TBL_LEAD_ACTIVITY = "tbl_lead_activity";

    String SHOP_DTLS_TEAM = "shop_dtls_team";
    String ORDER_DTLS_TEAM = "order_dtls_team";
    String COLL_DTLS_TEAM = "coll_dtls_team";
    String BILL_DTLS_TEAM = "bill_dtls_team";

    String TBL_DIST_WISE_ORD_REPORT = "tbl_dist_wise_ord_report";

    String NEW_GPS_STATUS = "new_gps_status";


}
