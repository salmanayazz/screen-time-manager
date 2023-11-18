package com.example.screentimemanager.ui.friend

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R

const val ADD_FRIEND_DIALOG_TAG = "Add Friend Fragment Dialog"
class AddFriendFragmentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val ret = inflater.inflate(R.layout.fragment_add_friend_dialog, null)
        val friendViewModel = ViewModelProvider(requireActivity()).get(FriendViewModel::class.java)

        //return dialog
        return activity?.let{
            val builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.setView(ret)
                .setTitle(getString(R.string.add_friend))
                //Users are not able to click outside of the dialog to cancel it
                .setCancelable(false)
                //Button to add friend
                .setPositiveButton(getString(R.string.add)){ _, _ ->
                    //Add friend to the list
                    //Hard coded the name for now
                    friendViewModel.addFriend(Friend("Smith", "Chan", null))
                }
                .setNegativeButton(getString(R.string.cancel)){ _, _ ->
                    //Cancel the dialog
                    dialog?.cancel()
                }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, ADD_FRIEND_DIALOG_TAG)
    }
}