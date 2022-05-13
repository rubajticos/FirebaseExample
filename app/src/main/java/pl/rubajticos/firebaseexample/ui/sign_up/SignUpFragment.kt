package pl.rubajticos.firebaseexample.ui.sign_up

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentSignUpBinding
import pl.rubajticos.firebaseexample.di.SignUpRequest
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.util.EventObserver
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(
    R.layout.fragment_dashboard,
    FragmentSignUpBinding::inflate
) {
    @Inject
    lateinit var googleSignInClient: SignInClient

    @Inject
    @SignUpRequest
    lateinit var googleSignUpRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2
    private val viewModel: SignUpViewModel by viewModels()


    override fun setupView() {
        binding.signUp.setOnClickListener {
            viewModel.signUpWithEmailAndPassword(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }

        binding.googleSignUpBtn.setOnClickListener {
            startGoogleSignUp()
        }
    }

    private fun startGoogleSignUp() {
        googleSignInClient.beginSignIn(googleSignUpRequest)
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
                Log.d("MRMR", "beginSignUp failure ${it.localizedMessage}")
                startGoogleSignUp()
            }
    }

    override fun observeEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner, EventObserver {
            Log.d("MRMR", "$it")
            when (it) {
                is SignUpEvent.Loading -> showToast(getString(R.string.loading))
                is SignUpEvent.SignUpError -> showToast(it.text.asString(requireContext()))
                is SignUpEvent.SignUpSuccess -> showToast(it.text.asString(requireContext()))
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