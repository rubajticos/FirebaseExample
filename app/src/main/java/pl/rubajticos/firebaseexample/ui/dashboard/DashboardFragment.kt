package pl.rubajticos.firebaseexample.ui.dashboard

import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.databinding.FragmentDashboardBinding

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    R.layout.fragment_dashboard,
    FragmentDashboardBinding::inflate
) {

    override fun setupView() {
    }

    override fun observeEvents() {
    }
}