package com.kcteam.features.micro_learning.presentation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NewFileUtils
//import com.kcteam.app.utils.AppCompactPPTViewer
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.micro_learning.model.MicroLearningDataModel
//import com.github.barteksc.pdfviewer.PDFView
//import com.itsrts.pptviewer.PPTViewer
import com.pnikosis.materialishprogress.ProgressWheel
import java.io.File
import java.lang.Exception


class MicroLearningWebViewFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var webview: WebView
    private lateinit var rl_webview_main: RelativeLayout
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var ll_loader: LinearLayout
   /* private lateinit var ppt_viewer: PPTViewer
    private lateinit var pdfView: PDFView*/
    private var isOnPageStarted = false

    private var mMicroLearning: MicroLearningDataModel? = null

    companion object {
        fun newInstance(microLearning: Any): MicroLearningWebViewFragment {
            val fragment = MicroLearningWebViewFragment()

            if (microLearning is MicroLearningDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("learning", microLearning)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        mMicroLearning = arguments?.getSerializable("learning") as MicroLearningDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragemnt_micro_learning_webview, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        view.apply {
            webview = findViewById(R.id.webview)
            rl_webview_main = findViewById(R.id.rl_webview_main)
            progress_wheel = findViewById(R.id.progress_wheel)
            ll_loader = findViewById(R.id.ll_loader)
            /*ppt_viewer = findViewById(R.id.ppt_viewer)
            pdfView = findViewById(R.id.pdfView)*/
        }
        progress_wheel.stopSpinning()


        /*val file = File(mMicroLearning?.url!!)
        val extension = NewFileUtils.getExtension(file)*/

        /*if (extension.equals("pptx", ignoreCase = true) || extension.equals("ppt", ignoreCase = true)) {
            pdfView.visibility = View.GONE
            ppt_viewer.visibility = View.VISIBLE
            webview.visibility = View.GONE

            ppt_viewer.setNext_img(R.drawable.next).setPrev_img(R.drawable.prev)
                    .setSettings_img(R.drawable.settings)
                    .setZoomin_img(R.drawable.zoomin)
                    .setZoomout_img(R.drawable.zoomout)
            ppt_viewer.loadPPT(mContext as DashboardActivity, mMicroLearning?.url!!)
        }
        else if (extension.equals("pdf", ignoreCase = true)) {
            pdfView.visibility = View.VISIBLE
            ppt_viewer.visibility = View.GONE
            webview.visibility = View.GONE

            pdfView.fromFile(file)
                    .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    // allows to draw something on the current page, usually visible in the middle of the screen
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(0)
                    .load()
        }
        else {*/
            /*pdfView.visibility = View.GONE
            ppt_viewer.visibility = View.GONE*/
            webview.visibility = View.VISIBLE

            webview.settings.run {
                javaScriptEnabled = true
                setSupportZoom(true)
                //useWideViewPort = true
                //cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                domStorageEnabled = true
                pluginState = WebSettings.PluginState.ON
                //loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                webview
            }.let {
                it.webChromeClient = WebChromeClient()
                it.setLayerType(View.LAYER_TYPE_HARDWARE, null)

                val extension = NewFileUtils.getExtension(File(mMicroLearning?.url!!))
                if (extension.equals("doc", ignoreCase = true) || extension.equals("docx", ignoreCase = true) || extension.equals("pdf", ignoreCase = true))
                    it.loadUrl("http://docs.google.com/gview?embedded=true&url=${mMicroLearning?.url}")
                else if (extension.equals("pptx", ignoreCase = true) || extension.equals("ppt", ignoreCase = true))
                    it.loadUrl("http://docs.google.com/viewer?url=${mMicroLearning?.url}")

                it.webViewClient = object : WebViewClient() {
//                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                        /*super.shouldOverrideUrlLoading(view, url)
//                        return false*/
//                        view?.loadUrl(url)
//                        return true
//                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        progress_wheel.spin()
                        super.onPageStarted(view, url, favicon)
                        Log.e("Webview", "======================page started===================")
                        isOnPageStarted = true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (isOnPageStarted) {
                            progress_wheel.stopSpinning()
                            ll_loader.visibility = View.GONE
                        }
                        else
                            view?.loadUrl(url!!)
                        Log.e("Webview", "======================page finished===================")
                        /*try {
                            if (url?.contains(mMicroLearning?.url!!)!!) {
                                CookieManager.getInstance().apply {
                                    removeAllCookies(null)
                                    flush()
                                }
                                view?.stopLoading()
                            }
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                        }
                        catch (e: Throwable) {
                            e.printStackTrace()
                        }*/
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        view?.loadUrl("about:blank")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Log.e("LearningWebView", error?.description.toString())
                        }
                        (mContext as DashboardActivity).showSnackMessage("Sorry, we are unable to load file.")
                    }
                }
                //it.loadUrl("http://docs.google.com/gview?embedded=true&url=${mMicroLearning?.url}")
            }
        //}
        rl_webview_main.setOnClickListener(null)
    }
}