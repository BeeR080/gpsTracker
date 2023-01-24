package com.beer080.gpstracker.main.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.DialogSaveBinding
import com.beer080.gpstracker.main.data.TrackItem

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

    fun showSaveDialog(context: Context,track:TrackItem?, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val binding = DialogSaveBinding.inflate(
            LayoutInflater.from(context),
            null,
            false)
        builder.setView(binding.root)
        val dialog = builder.create()
        binding.apply {
            dialogtvTime.text = "Time: " + track?.time + " m"
            dialogtvVelocity.text = "Velocity: " + track?.velocity + " km/h"
            dialogtvDistance.text ="Distance: " + track?.distance + " km"
            dialogbtSave.setOnClickListener {
                listener.onClick()
                dialog.dismiss()
            }
            dialogbtCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    interface Listener{
        fun onClick()
    }
}