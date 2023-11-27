package com.example.screentimemanager.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.screentimemanager.R

class LoginFragment : Fragment() {
    private lateinit var registerTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        registerTextView = view.findViewById<TextView>(R.id.register_button_text)
        registerTextView.setOnClickListener{
            var intent:Intent= Intent(requireContext(),Register::class.java)
            startActivity(intent)
        }
        return view
    }
}