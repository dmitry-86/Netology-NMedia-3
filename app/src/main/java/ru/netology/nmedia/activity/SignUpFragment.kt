package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignUpViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    @Inject
    lateinit var auth: AppAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        val viewModel: SignUpViewModel by viewModels()

        val name = binding.nameField.text
        val login = binding.logInField.text
        val password = binding.passwordField.text
        val passwordRepeat = binding.passwordRepeatField.text

        binding.signInButton.setOnClickListener {
            if (login!!.isBlank() || name!!.isBlank() || password!!.isBlank()) {
                Toast.makeText(context, "Fields could not be blank", Toast.LENGTH_LONG).show()
            }else if(password.toString() != passwordRepeat.toString()){
                Toast.makeText(context, "passwords do not match", Toast.LENGTH_LONG).show()
            }else{
                viewModel.registerUser(login.toString(), password.toString(), name.toString())
            }
        }

        viewModel.data.observe(viewLifecycleOwner, {
            auth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        return binding.root
    }
}