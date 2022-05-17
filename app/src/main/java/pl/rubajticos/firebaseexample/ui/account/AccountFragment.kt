package pl.rubajticos.firebaseexample.ui.account

import androidx.fragment.app.viewModels
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
        viewModel.getAccountData()
    }

    override fun observeEvents() {
        viewModel.uiEvents.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is AccountEvents.ShowAccountInfo -> {
                    accountAdapter.update(it.infos.map { item ->
                        AccountInfoUiItem(
                            item.label.asString(requireContext()), item.value
                        )
                    })
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