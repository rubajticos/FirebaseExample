package pl.rubajticos.firebaseexample.ui.account

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentAccountBinding
import pl.rubajticos.firebaseexample.ui.base.BaseFragment
import pl.rubajticos.firebaseexample.util.EventObserver

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(
    R.layout.fragment_account,
    FragmentAccountBinding::inflate
) {
    private val viewModel: AccountViewModel by viewModels()
    private lateinit var accountAdapter: AccountAdapter


    override fun setupView() {
        prepareRecyclerView()
        binding.signOutBtn.setOnClickListener {
            viewModel.signOut()
        }


        viewModel.getAccountData()
    }

    override fun observeEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is AccountEvents.ShowAccountInfo -> {
                    accountAdapter.update(it.info.map { item ->
                        AccountInfoUiItem(
                            item.label.asString(requireContext()), item.value
                        )
                    })
                }
                is AccountEvents.SignOutError -> showToast(it.text.asString(requireContext()))
                AccountEvents.SignOutSuccess -> {
                    showToast(getString(R.string.sign_out_success))
                    findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToDashboardFragment())
                }
            }
        })
    }

    private fun prepareRecyclerView() {
        val viewManager = LinearLayoutManager(requireContext())
        accountAdapter = AccountAdapter(emptyList())
        binding.userDetailsRV.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = accountAdapter
        }
    }
}