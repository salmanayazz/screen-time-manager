package com.example.screentimemanager.ui.friend

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.screentimemanager.R

const val ADD_FRIEND_DIALOG_TAG = "Add Friend Fragment Dialog"
class AddFriendFragmentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val ret = inflater.inflate(R.layout.fragment_add_friend_dialog, null)

        //return dialog
        return activity?.let{
            val builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.setView(ret)
                .setTitle(getString(R.string.add_friend))
                //Users are not able to click outside of the dialog to cancel it
                .setCancelable(false)
                //Button to add friend
                .setPositiveButton(getString(R.string.add)){ _, _ ->
                    //Here will do the logic of adding friend into the database
                }
                .setNegativeButton(getString(R.string.cancel)){ _, _ ->
                    dialog?.cancel() //Cancel the dialog
                }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, ADD_FRIEND_DIALOG_TAG)
    }
}