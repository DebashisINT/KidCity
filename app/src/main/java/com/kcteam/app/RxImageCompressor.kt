package com.kcteam.app

import com.kcteam.features.dashboard.presentation.DashboardActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.observers.ResourceObserver
import io.reactivex.schedulers.Schedulers


/**
 * Created on : June 18, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
class RxImageCompressor {

/*    fun compressToBitmapAsFlowable(imagePath: String): Flowable<String> {
        return defer(Callable {
            try {
                return@Callable Flowable.just(compressToBitmap(imagePath))
            } catch (e: IOException) {
                return@Callable Flowable.error(e)
            }
        })
    }

    @Throws(IOException::class)
    private fun compressToBitmap(imagePath: String): String {
        return ImageCompressionUtils.getRightAngleImage(imagePath)
    }

    fun getCompressedFilePath(context:Context,uri: Uri){
        val subscription = Observable.fromArray(ImageCompressionUtils.getRealPathFromURI(context,uri))
                .map({ path -> ImageCompressionUtils.getRightAngleImage(ImageCompressionUtils.getRealPathFromURI(context,uri)) })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : ResourceObserver<String>() {
                    override fun onNext(t: String) {
                        ImageCompressionUtils.performCrop((context as DashboardActivity),uri,2)
                    }
                    override fun onError(@NonNull e: Throwable) {
//                        ShowFailAlertDialog()
                    }

                    override fun onComplete() {
//                        ShowSuccessAlertDialog()
                    }
                })
    }*/


}
