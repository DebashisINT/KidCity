package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import org.jetbrains.anko.runOnUiThread

class CodeScannerFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var scanner_view: CodeScannerView
    private lateinit var codeScanner: CodeScanner

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_code_scanner, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        scanner_view = view.findViewById(R.id.scanner_view)

        codeScanner = CodeScanner(mContext, scanner_view)
        codeScanner.decodeCallback = DecodeCallback {
            mContext.runOnUiThread {
                (mContext as DashboardActivity).apply {
                    isCodeScaneed = true
                    qrCodeText = it.text
                    onBackPressed()
                }
            }
        }

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}