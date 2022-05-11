package pl.rubajticos.firebaseexample.ui.sign_up

import android.util.Log
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentSignUpBinding
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.util.EventObserver

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(
    R.layout.fragment_dashboard,
    FragmentSignUpBinding::inflate
) {
    private val viewModel: SignUpViewModel by viewModels()


    override fun setupView() {
        binding.signUp.setOnClickListener {
            viewModel.signUpWithEmailAndPassword(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }


    }

    override fun observeEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner, EventObserver {
            Log.d("MRMR", "$it")
            when (it) {
                is SignUpEvent.Loading -> showToast(getString(R.string.loading))
                is SignUpEvent.SignUpError -> showToast(getString(it.resId))
                SignUpEvent.SignUpSuccess -> showToast(getString(R.string.sign_up_success))
            }
        })
    }
}