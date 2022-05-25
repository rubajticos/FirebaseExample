package pl.rubajticos.firebaseexample.ui.news

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.databinding.FragmentNewsBinding
import pl.rubajticos.firebaseexample.ui.base.BaseFragment

@AndroidEntryPoint
class NewsFragment :
    BaseFragment<FragmentNewsBinding>(R.layout.fragment_news, FragmentNewsBinding::inflate) {

    val viewModel: NewsViewModel by viewModels()

    override fun setupView() {
    }

    override fun observeEvents() {
    }


}