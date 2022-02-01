package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject
    lateinit var auth: AppAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)

        val viewModel: SignInViewModel by viewModels()

        binding.signInButton.setOnClickListener {
            viewModel.authUser(
                binding.logInField.text.toString(),
                binding.passwordField.text.toString()
            )
        }

        viewModel.data.observe(viewLifecycleOwner, {
            auth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.errorLogin) {
                binding.passwordField.error = getString(R.string.error_auth)
            }
        }

        return binding.root
    }
}