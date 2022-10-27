package com.kcteam.features.addshop.presentation

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.utils.TessOCR
import com.kcteam.app.utils.Toaster
import kotlinx.android.synthetic.main.inflate_card_details_item.view.*


class ShowCardAdapter(private val mContext: Context, private val cardDetails: ArrayList<String>) : RecyclerView.Adapter<ShowCardAdapter.ViewHolder>() {

    private val inflater: LayoutInflater by lazy {
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private val mTess: TessOCR by lazy {
        TessOCR(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.inflate_card_details_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews()
    }

    override fun getItemCount(): Int {
        return cardDetails.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindViews() {
            itemView.apply {
                tv_card_details.text = cardDetails[adapterPosition]
                iv_copy.setOnClickListener {

                    val numeric = ArrayList<String>()

                    val removeHyphen = cardDetails[adapterPosition].replace("-", "")
                    val removePlus = removeHyphen.replace("+", "")
                    val finalString = removePlus.replace(" ", "")
                    if (mTess.isNumeric(finalString))
                        numeric.add(cardDetails[adapterPosition])

                    try {
                        if (numeric.size > 0) {
                            val removeSpace = mTess.parseResults(numeric)[0].replace(" ", "")
                            val removePlus_ = removeSpace.replace("+", "")
                            val finalString_ = removePlus_.replace("-", "")
                            cardDetails[adapterPosition] = finalString_
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }


                    val clipboard: ClipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("Copy Text", cardDetails[adapterPosition])
                    clipboard.setPrimaryClip(clip)

                    Toaster.msgShort(mContext, "Text Copied")
                }
            }
        }
    }
}