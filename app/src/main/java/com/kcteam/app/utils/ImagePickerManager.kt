package com.kcteam.app.utils

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog

/**
 * Created by sandip on 29-11-2017.
 */
class ImagePickerManager {
    companion object {
        private var onImagePickerCallback: OnImagePickerCallback? = null
        val REQUEST_GET_GALLERY_PHOTO = 11112

        fun openImagePicker(context: Context, _ImagePickerCallback: OnImagePickerCallback) {
            onImagePickerCallback = _ImagePickerCallback

            val items = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Add Photo")
            builder.setItems(items) { dialog, item ->
                if (items[item] == "Take Photo") {
                    if (onImagePickerCallback != null)
                        onImagePickerCallback!!.onTakeCamera()
                } else if (items[item] == "Choose from Gallery") {
                    if (onImagePickerCallback != null)
                        onImagePickerCallback!!.onTakeGallery()
                } else if (items[item] == "Cancel") {
                    try {
                        dialog?.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            builder.show()
        }

        interface OnImagePickerCallback {
            fun onTakeCamera()

            fun onTakeGallery()
        }

        /**
         * Get Image path from Intent Data
         *
         * @param data
         * @param context
         * @return
         */
        fun getImagePathFromData(data: Intent?, context: Context): String {
            val selectedImage = data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            return picturePath
        }
    }
}