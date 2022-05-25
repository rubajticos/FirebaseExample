package pl.rubajticos.firebaseexample.ui.dashboard

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentDashboardBinding
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.util.EventObserver

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    R.layout.fragment_dashboard,
    FragmentDashboardBinding::inflate
) {

    private val viewModel: DashboardViewModel by viewModels()

    override fun setupView() {
        viewModel.checkUserLogin()

        binding.realtimeDatabaseButton.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToNewsFragment())
        }

    }

    override fun observeEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                DashboardEvent.NavigateToLogin -> {
                    findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToLoginRegisterFragment())
                }
            }
        })
    }
}