package com.example.screentimemanager.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var databaseReference : DatabaseReference
    private lateinit var userFirebaseDao : UserFirebaseDao
    private lateinit var logInBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        databaseReference  = FirebaseDatabase.getInstance().reference
        userFirebaseDao = UserFirebaseDao(databaseReference)


        // setting up the widgets
        logInBtn = root.findViewById(R.id.log_in_btn)
        emailEditText = root.findViewById(R.id.login_username)
        passwordEditText = root.findViewById(R.id.login_password)

        // setting the on click listener for logging in button
        logInBtn.setOnClickListener{
            onLoginListener()
        }
        return root
    }

    private fun onLoginListener(){
        val email: String = emailEditText.text.toString()
        val password: String = passwordEditText.text.toString()

        if (email.isNullOrEmpty() or password.isNullOrEmpty()){
            Toast.makeText(requireContext(),"Empty fields",Toast.LENGTH_SHORT).show()
        }else{
            var user = UserFirebase(email,"mohammad","Parsaei",password,null)
            GlobalScope.launch{
                userFirebaseDao.addUser(user)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}