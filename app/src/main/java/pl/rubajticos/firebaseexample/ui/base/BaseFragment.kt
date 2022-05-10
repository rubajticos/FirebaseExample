package pl.rubajticos.firebaseexample.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    layoutResId: Int,
    private val inflate: Inflate<VB>
) : Fragment(layoutResId) {
    private var _binding: VB? = null
    val binding get() = _binding!!

    abstract fun setupView()
    abstract fun observeEvents()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}