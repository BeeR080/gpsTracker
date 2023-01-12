package com.beer080.gpstracker.main.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.beer080.gpstracker.R

object DialogManager {
    fun showLocEnabledDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location_disabled_title)
        dialog.setMessage(context.getString(R.string.location_disabled_message))

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No"){
            _,_ -> dialog.dismiss()

        }
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,"Yes"){
                _,_ ->
            listener.onClick()
        }

        dialog.show()

    }

    interface Listener{
        fun onClick()
    }
}