package pl.rubajticos.firebaseexample.ui.dashboard

import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentDashboardBinding
import pl.rubajticos.firebaseexample.ui.base.BaseFragment

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    R.layout.fragment_dashboard,
    FragmentDashboardBinding::inflate
) {

    override fun setupView() {
        binding.signInBtn.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToSignInFragment())
        }
    }

    override fun observeEvents() {
    }
}