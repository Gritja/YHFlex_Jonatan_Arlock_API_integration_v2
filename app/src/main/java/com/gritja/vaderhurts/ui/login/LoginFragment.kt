package com.gritja.vaderhurts.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.gritja.vaderhurts.R
import com.gritja.vaderhurts.databinding.FragmentLoginBinding
import com.gritja.vaderhurts.ui.login.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private var isLoggedIn = false // Store login status locally

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        disableUIElements()

        val usernameEditText: EditText = root.findViewById(R.id.username)
        val passwordEditText: EditText = root.findViewById(R.id.password)
        val loginButton: Button = root.findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username == "admin" && password == "123") {
                updateUIAfterLogin()
                findNavController().navigate(R.id.nav_home)
            } else {
                Toast.makeText(requireContext(), "Invalid login", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun updateUIAfterLogin() {
        isLoggedIn = true
        enableUIElements()
    }

    private fun disableUIElements() {
        // Disable navbar items in this fragment
        if (activity != null) {
            val navView = activity?.findViewById<NavigationView>(R.id.nav_view)
            if (navView != null) {
                navView.menu.findItem(R.id.nav_home).isVisible = false
                navView.menu.findItem(R.id.nav_gallery).isVisible = false
                navView.menu.findItem(R.id.nav_slideshow).isVisible = false
            }
        }

        // Disable other UI elements as needed
        //binding.someElement.visibility = View.GONE
    }

    private fun enableUIElements() {
        // Enable navbar items in this fragment
        if (activity != null) {
            val navView = activity?.findViewById<NavigationView>(R.id.nav_view)
            if (navView != null) {
                navView.menu.findItem(R.id.nav_home).isVisible = true
                navView.menu.findItem(R.id.nav_gallery).isVisible = true
                navView.menu.findItem(R.id.nav_slideshow).isVisible = true
            }
        }
    }

        // Re-enable other UI elements as needed
        //binding.someElement.visibility = View.VISIBLE

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}