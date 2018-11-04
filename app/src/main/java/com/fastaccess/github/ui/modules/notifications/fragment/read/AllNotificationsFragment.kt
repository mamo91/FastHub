package com.fastaccess.github.ui.modules.notifications.fragment.read

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.adapter.AllNotificationsAdapter
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.observeNotNull
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 04.11.18.
 */
class AllNotificationsFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(AllNotificationsViewModel::class.java) }
    private val adapter by lazy { AllNotificationsAdapter() }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadNotifications()
        swipeRefresh.setOnRefreshListener {
            viewModel.loadNotifications()
        }
        recyclerView.addOnLoadMore { viewModel.loadNotifications() }
        listenToChanges()
    }

    private fun listenToChanges() {

        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance() = AllNotificationsFragment()
    }
}