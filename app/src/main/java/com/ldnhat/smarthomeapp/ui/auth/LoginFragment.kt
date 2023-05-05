package com.ldnhat.smarthomeapp.ui.auth

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.databinding.FragmentLoginBinding
import com.ldnhat.smarthomeapp.ui.base.BaseFragment
import com.ldnhat.smarthomeapp.ui.enable
import com.ldnhat.smarthomeapp.ui.handleApiError
import com.ldnhat.smarthomeapp.ui.home.HomeActivity
import com.ldnhat.smarthomeapp.ui.startNewActivity
import com.ldnhat.smarthomeapp.ui.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressbar.visible(false)
        binding.buttonLogin.enable(false)

        viewModel.loginResponse.observe(viewLifecycleOwner) {
            binding.progressbar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.saveAccessTokens(it.value.id_token)
                    }
                    requireActivity().startNewActivity(HomeActivity::class.java)
                }
                is Resource.Failure -> handleApiError(it) {
                    login()
                }
                else -> {}
            }
        }

        binding.editTextTextPassword.addTextChangedListener {
            val email = binding.editTextTextEmailAddress.text.toString().trim()
            binding.buttonLogin.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.editTextTextEmailAddress.text.toString().trim()
        val password = binding.editTextTextPassword.text.toString().trim()
        viewModel.login(email, password, false)
    }
}