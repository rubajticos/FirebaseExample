package pl.rubajticos.firebaseexample.ui.dashboard

import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentSignUpBinding
import pl.rubajticos.firebaseexample.ui.base.BaseFragment

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(
    R.layout.fragment_dashboard,
    FragmentSignUpBinding::inflate
) {

    override fun setupView() {
    }

    override fun observeEvents() {
    }
}