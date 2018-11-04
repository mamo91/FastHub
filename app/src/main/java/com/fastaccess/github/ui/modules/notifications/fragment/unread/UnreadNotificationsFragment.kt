package com.fastaccess.github.ui.modules.notifications.fragment.unread

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.adapter.UnreadNotificationsAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.notifications.fragment.unread.viewmodel.UnreadNotificationsViewModel
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.observeNotNull
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 21.10.18.
 */
class UnreadNotificationsFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(UnreadNotificationsViewModel::class.java) }
    private val adapter by lazy { UnreadNotificationsAdapter() }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadNotifications(true)
        swipeRefresh.setOnRefreshListener {
            recyclerView.resetScrollState()
            viewModel.loadNotifications(true)
        }
        recyclerView.addOnLoadMore { viewModel.loadNotifications() }
        listenToChanges()
    }

    private fun listenToChanges() {

        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.notifications().observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance() = UnreadNotificationsFragment()
    }
}