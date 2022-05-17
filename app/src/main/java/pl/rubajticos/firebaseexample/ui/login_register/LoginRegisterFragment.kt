package pl.rubajticos.firebaseexample.ui.login_register

import android.content.Intent
import android.content.IntentSender
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentLoginRegisterBinding
import pl.rubajticos.firebaseexample.di.SignInRequest
import pl.rubajticos.firebaseexample.di.SignUpRequest
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.util.EventObserver
import javax.inject.Inject

@AndroidEntryPoint
class LoginRegisterFragment : BaseFragment<FragmentLoginRegisterBinding>(
    R.layout.fragment_login_register,
    FragmentLoginRegisterBinding::inflate
) {
    @Inject
    lateinit var googleSignInClient: SignInClient

    @Inject
    @SignInRequest
    lateinit var googleSignInRequest: BeginSignInRequest

    @Inject
    @SignUpRequest
    lateinit var googleSignUpRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2
    private val viewModel: LoginRegisterViewModel by viewModels()

    override fun setupView() {
        binding.signInEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onEvent(LoginRegisterFormEvent.EmailChanged(p0.toString()))
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.signInPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onEvent(LoginRegisterFormEvent.PasswordChanged(p0.toString()))
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.mode.setOnCheckedChangeListener { _, checked ->
            viewModel.onEvent(LoginRegisterFormEvent.ModeChanged(checked))
        }

        binding.signIn.setOnClickListener {
            viewModel.onEvent(LoginRegisterFormEvent.Submit)
        }

        binding.googleSignInBtn.setOnClickListener {
            viewModel.onGoogleButtonClick()
        }
    }

    private fun beginSignInWithGoogle(request: BeginSignInRequest) {
        googleSignInClient.beginSignIn(request)
            .addOnSuccessListener {
                try {
                    startIntentSenderForResult(
                        it.pendingIntent.intentSender,
                        REQ_ONE_TAP,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("MRMR", "starting intent failure ${e.localizedMessage}")
                }
            }
            .addOnFailureListener {
                Log.d("MRMR", "beginSignIn failure ${it.localizedMessage}")
            }
    }

    override fun observeEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is LoginRegisterEvent.Loading -> showToast(getString(R.string.loading))
                is LoginRegisterEvent.Error -> showToast(it.text.asString(requireContext()))
                is LoginRegisterEvent.Success -> showToast(it.text.asString(requireContext()))
                is LoginRegisterEvent.State -> {
                    binding.signInEmailTextInput.error =
                        it.state.emailError?.asString(requireContext())
                    binding.signInPasswordTextInput.error =
                        it.state.passwordError?.asString(requireContext())
                    binding.mode.isChecked = it.state.registerMode
                    if (it.state.registerMode) {
                        binding.signIn.text = getString(R.string.sign_up)
                        binding.googleSignInBtn.text = getString(R.string.sign_up_google)
                    } else {
                        binding.signIn.text = getString(R.string.sign_in)
                        binding.googleSignInBtn.text = getString(R.string.sign_in_google)
                    }
                }
                LoginRegisterEvent.LoginWithGoogle -> beginSignInWithGoogle(googleSignInRequest)
                LoginRegisterEvent.RegisterWithGoogle -> beginSignInWithGoogle(googleSignUpRequest)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = googleSignInClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    Log.d("MRMR", "token=$idToken")
                    if (idToken != null) {
                        viewModel.onEvent(LoginRegisterFormEvent.SubmitWithGoogle(idToken))
                    } else {
                        Log.d("MRMR", " Google ID Token is null")
                    }
                } catch (e: ApiException) {
                    Log.d("MRMR", "result ${e.localizedMessage}")
                }
            }
        }
    }
}