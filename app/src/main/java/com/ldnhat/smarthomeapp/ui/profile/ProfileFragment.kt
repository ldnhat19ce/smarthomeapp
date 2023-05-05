package com.ldnhat.smarthomeapp.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.data.UserPreferences
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.response.AccountResponse
import com.ldnhat.smarthomeapp.databinding.FragmentProfileBinding
import com.ldnhat.smarthomeapp.ui.auth.AuthActivity
import com.ldnhat.smarthomeapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel by viewModels<ProfileViewModel>()

    @Inject
    lateinit var userPreferences: UserPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        viewModel.getAccount()
        viewModel.account.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Resource.Success -> {
                        updateUI(binding, it)
                    }
                    else -> {}
                }
            }
        }

        binding.tvSignOut.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.clear()
            }
            requireActivity().startNewActivity(AuthActivity::class.java)
//            findNavController().navigate(ProfileFragmentDirections.actionProfileToHome())
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(
        binding: FragmentProfileBinding,
        data: Resource.Success<AccountResponse>
    ) {
        binding.tvProfileName.setText(data.value.login)
    }

}