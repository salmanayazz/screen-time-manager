package com.example.screentimemanager.ui.friend

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.repository.FriendRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

const val ADD_FRIEND_DIALOG_TAG = "Add Friend Fragment Dialog"
class AddFriendFragmentDialog : DialogFragment() {
    private lateinit var userInput: EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val ret = inflater.inflate(R.layout.fragment_add_friend_dialog, null)
        val firebaseDatabaseRef = FirebaseDatabase.getInstance().reference
        val friendDao = FriendFirebaseDao(firebaseDatabaseRef)
        val friendRepo = FriendRepository(friendDao)
        userInput = ret.findViewById(R.id.et_friendUserId)
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
                    CoroutineScope(IO).launch{
                        friendRepo.sendFriendRequest(userInput.text.toString())
                    }
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