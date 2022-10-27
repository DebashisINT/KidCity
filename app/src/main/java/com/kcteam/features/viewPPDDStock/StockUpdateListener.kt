package com.kcteam.features.viewPPDDStock

/**
 * Created by Kinsuk on 16-11-2017.
 */
interface StockUpdateListener {
     fun onUpdateClick(openingMonth: String, openingYear: String, amount: String, closingMonth: String, closingYear: String, description: String, mo: String, co: String, po: String)
}