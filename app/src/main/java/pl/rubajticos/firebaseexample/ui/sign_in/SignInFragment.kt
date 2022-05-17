package pl.rubajticos.firebaseexample.ui.sign_in

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
import pl.rubajticos.firebaseexample.databinding.FragmentSignInBinding
import pl.rubajticos.firebaseexample.di.SignInRequest
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.util.EventObserver
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(
    R.layout.fragment_sign_in,
    FragmentSignInBinding::inflate
) {
    @Inject
    lateinit var googleSignInClient: SignInClient

    @Inject
    @SignInRequest
    lateinit var googleSignInRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2
    private val viewModel: SignInViewModel by viewModels()

    override fun setupView() {
        binding.signInEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onEvent(SignInFormEvent.EmailChanged(p0.toString()))
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.signIn.setOnClickListener {
            viewModel.onEvent(SignInFormEvent.Submit)
        }

        binding.googleSignInBtn.setOnClickListener {
            startGoogleSignIn()
        }
    }

    private fun startGoogleSignIn() {
        googleSignInClient.beginSignIn(googleSignInRequest)
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
            Log.d("MRMR", "$it")
            when (it) {
                is SignInEvent.Loading -> showToast(getString(R.string.loading))
                is SignInEvent.SignInError -> showToast(it.text.asString(requireContext()))
                is SignInEvent.SignInSuccess -> showToast(it.text.asString(requireContext()))
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
                        viewModel.signUpWithGoogle(idToken)
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