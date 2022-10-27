package com.kcteam.features.document.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

data class DocumentTypeResponseModel(var type_list: ArrayList<DocumentTypeDataModel>?= null): Serializable, BaseResponse()

data class DocumentTypeDataModel(var id: String = "",
                                 var type: String = "",
                                 var image: String = "",
                                var IsForOrganization:Boolean,
                                    var IsForOwn:Boolean): Serializable


data class DocumentListResponseModel(var doc_list: ArrayList<DocumentListDataModel>?= null): Serializable, BaseResponse()

data class DocumentListDataModel(var id: String = "",
                                 var type_id: String = "",
                                 var date_time: String = "",
                                 var attachment: String = "",
                                 var document_name: String = ""): Serializable

data class AddEditDocumentInputParams(var session_token: String = "",
                                      var user_id: String = ""): Serializable

data class DocumentAttachmentModel(var link: String = "",
                                   var list_id: String = "",
                                   var type_id: String = "",
                                   var date_time: String = ""): Serializable