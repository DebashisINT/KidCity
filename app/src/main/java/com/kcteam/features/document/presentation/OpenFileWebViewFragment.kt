package com.kcteam.features.document.presentation

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NewFileUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.pnikosis.materialishprogress.ProgressWheel
import java.io.File


class OpenFileWebViewFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var webview: WebView
    private lateinit var rl_webview_main: RelativeLayout
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var ll_loader: LinearLayout

    private var isOnPageStarted = false

//    private var mMicroLearning: MicroLearningDataModel? = null

    lateinit var url_Str:String

    companion object {
        var uurl:String=""
        fun newInstance(fileurl: Any): OpenFileWebViewFragment {
            val fragment = OpenFileWebViewFragment()

            if (!TextUtils.isEmpty(fileurl.toString())) {
                val bundle = Bundle()
                bundle.putString("file_url", fileurl as String?)
                fragment.arguments = bundle
                uurl=fileurl.toString()
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        url_Str = arguments?.getSerializable("file_url").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragemnt_micro_learning_webview, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        url_Str= uurl

        view.apply {
            webview = findViewById(R.id.webview)
            rl_webview_main = findViewById(R.id.rl_webview_main)
            progress_wheel = findViewById(R.id.progress_wheel)
            ll_loader = findViewById(R.id.ll_loader)
        }
        progress_wheel.stopSpinning()


        webview.visibility = View.VISIBLE

        webview.settings.run {
            javaScriptEnabled = true
            setSupportZoom(true)
            domStorageEnabled = true
            pluginState = WebSettings.PluginState.ON
            //loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            webview
        }.let {
            it.webChromeClient = WebChromeClient()
            it.setLayerType(View.LAYER_TYPE_HARDWARE, null)

            val extension = NewFileUtils.getExtension(File(url_Str))
            if (extension.equals("doc", ignoreCase = true) || extension.equals("docx", ignoreCase = true) || extension.equals("pdf", ignoreCase = true))
                it.loadUrl("http://docs.google.com/gview?embedded=true&url=${ url_Str}")
            else if (extension.equals("pptx", ignoreCase = true) || extension.equals("ppt", ignoreCase = true))
                it.loadUrl("http://docs.google.com/viewer?url=${ url_Str}")

            it.webViewClient = object : WebViewClient() {
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
                    } else
                        view?.loadUrl(url!!)
                    Log.e("Webview", "======================page finished===================")
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    view?.loadUrl("about:blank")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.e("WebView", error?.description.toString())
                    }
                    (mContext as DashboardActivity).showSnackMessage("Sorry, we are unable to load file.")
                }
            }

        }
        rl_webview_main.setOnClickListener(null)

    }
}