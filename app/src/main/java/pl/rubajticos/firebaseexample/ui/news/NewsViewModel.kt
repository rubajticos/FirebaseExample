package pl.rubajticos.firebaseexample.ui.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.rubajticos.firebaseexample.datasource.RealtimeNewsDataSource
import pl.rubajticos.firebaseexample.util.Event
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val realtimeNewsDataSource: RealtimeNewsDataSource
) : ViewModel() {

    private val _state = MutableLiveData<Event<NewsState>>()
    val state: LiveData<Event<NewsState>> = _state

    fun onEvent(event: NewsEvent) {
        when (event) {
            is NewsEvent.AddNews -> Log.d("MRMR", "Create News")
        }
    }

}

