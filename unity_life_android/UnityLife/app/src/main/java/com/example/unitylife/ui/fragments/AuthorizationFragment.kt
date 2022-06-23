package com.example.unitylife.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.unitylife.App
import com.example.unitylife.R
import com.example.unitylife.data.models.SendToServerUserModel
import com.example.unitylife.databinding.FragmentAuthorizationBinding
import com.example.unitylife.ext.injectViewModel
import com.example.unitylife.ext.showSnackbar
import com.example.unitylife.view_models.LoginState
import com.example.unitylife.view_models.LoginViewModel
import com.llc.aceplace_ru.di.ViewModelsFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import utils.SharedPreferencesStorage
import javax.inject.Inject

class AuthorizationFragment : Fragment()  {
    private lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var factory: ViewModelsFactory
    lateinit var storage: SharedPreferencesStorage

    private var binding: FragmentAuthorizationBinding? = null

    var isRegisterScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = (requireActivity().application as App).getAppComponent().getSharedPreferencesStorage()
        (requireActivity().application as App).getAppComponent().plusLogin().inject(this)
        viewModel = injectViewModel(factory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthorizationBinding.inflate(
            inflater,
            container,
            false
        )
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        lifecycleScope.launch {
            viewModel.getState().collect { onLoginStateChanged(it) }
        }
    }

    private fun onLoginStateChanged(newState: LoginState) {
        when (newState) {
            is LoginState.LoginSuccess -> {
                val token = newState.token
                storage.putAuthToken(token)
            }
            is LoginState.RegisterSuccess -> {
                val userModel = newState.userModel
                storage.putUserModel(userModel)
            }
            is LoginState.LogoutSuccess -> {
                storage.removeAuthToken()

            }
            is LoginState.Error -> onError(newState.textId)
            else -> { }
        }
    }

    private fun informUser(textId: Int) {
        binding?.let {
            showSnackbar(
                it.root,
                getString(textId),
                duration = 1500
            )
        }
    }

    private fun onError(textId: Int) {
        binding?.let {
            showSnackbar(
                it.root,
                getString(textId),
                duration = 1500
            )
        }
    }

    private fun setupClickListeners() {
        binding?.apply {
            signInBtnActionContinue.setOnClickListener {
                if (isRegisterScreen) {
                    registerUser()
                } else {
                    loginUser()
                }
            }
            signInBtnSignIn.setOnClickListener {
                isRegisterScreen = !isRegisterScreen
                if (isRegisterScreen) {
                    setRegisterView()
                } else {
                    setLoginView()
                }
            }
            closeBtn.setOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setRegisterView() {
        binding?.apply {
            textHasAccount.text = getString(R.string.sign_in_already_has_account)
            signInBtnSignIn.text = getString(R.string.action_sign_in)
            signInTilFirstName.visibility = View.VISIBLE
            signInTilSecondName.visibility = View.VISIBLE
            signUpTilAge.visibility = View.VISIBLE
            signUpTilCity.visibility = View.VISIBLE
            signUpTilCountry.visibility = View.VISIBLE
        }
    }

    private fun setLoginView() {
        binding?.apply {
            textHasAccount.text = getString(R.string.sign_in_hasnt_account)
            signInBtnSignIn.text = getString(R.string.action_sign_up)
            signInTilFirstName.visibility = View.GONE
            signInTilSecondName.visibility = View.GONE
            signUpTilAge.visibility = View.GONE
            signUpTilCity.visibility = View.GONE
            signUpTilCountry.visibility = View.GONE
        }
    }

    private fun loginUser() {
        viewModel.login(storage.getUserId())
    }

    private fun registerUser() {
        binding?.apply {
            val userModel = SendToServerUserModel(
                firstName = signInEtFirstName.text.toString(),
                lastName = signInEtSecondName.text.toString(),
                email = signInEtEmail.text.toString(),
                age = signUpEtAge.text.toString().toInt(),
                gender = signUpEtGender.text.toString(),
                country = signUpEtCountry.text.toString(),
                city = signUpEtCity.text.toString(),
                password = signInEtPassword.text.toString(),
                role = "USER"
            )
            viewModel.register(userModel)
        }
    }
}