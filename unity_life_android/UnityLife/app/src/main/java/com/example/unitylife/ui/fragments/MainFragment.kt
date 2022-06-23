package com.example.unitylife.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.unitylife.App
import com.example.unitylife.R
import com.example.unitylife.databinding.DrawerEventsBinding
import com.example.unitylife.databinding.DrawerEventsContentBinding
import com.example.unitylife.databinding.DrawerEventsMenuBinding
import com.example.unitylife.databinding.FragmentProfileNotLoggedBinding
import com.example.unitylife.ext.injectViewModel
import com.example.unitylife.ext.showSnackbar
import com.example.unitylife.view_models.EventsViewModel
import com.example.unitylife.view_models.LoginState
import com.example.unitylife.view_models.LoginViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.llc.aceplace_ru.di.ViewModelsFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import utils.SharedPreferencesStorage
import javax.inject.Inject


class MainFragment : Fragment() {
    private var binding: DrawerEventsBinding? = null
    private var notLoggedBinding: FragmentProfileNotLoggedBinding? = null
    private var bindingDrawer: DrawerEventsMenuBinding? = null
    private var bindingContent: DrawerEventsContentBinding? = null

    @Inject
    lateinit var factory: ViewModelsFactory
    lateinit var storage: SharedPreferencesStorage
    lateinit var loginViewModel: LoginViewModel

    private var pageAdapter: PagesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = (requireActivity().application as App).getAppComponent().getSharedPreferencesStorage()
        (requireActivity().application as App).getAppComponent().plusLogin().inject(this)
        loginViewModel = injectViewModel(factory)
    }

    override fun onDestroy() {
        pageAdapter = null
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        /*if (storage.getFlagIsUserLoggedIn()) {
            binding = DrawerEventsBinding.inflate(
                inflater,
                container,
                false
            )
            bindingDrawer = DrawerEventsMenuBinding.bind(binding!!.drawerEventsMenu.root)
            bindingContent = DrawerEventsContentBinding.bind(binding!!.drawerEventsContent.root)
            return binding?.root
        } else {
            notLoggedBinding = FragmentProfileNotLoggedBinding.inflate(
                inflater,
                container,
                false
            )
            return notLoggedBinding?.root
        }*/
        binding = DrawerEventsBinding.inflate(
            inflater,
            container,
            false
        )
        bindingDrawer = DrawerEventsMenuBinding.bind(binding!!.drawerEventsMenu.root)
        bindingContent = DrawerEventsContentBinding.bind(binding!!.drawerEventsContent.root)
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        bindingDrawer = null
        bindingContent = null
        notLoggedBinding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        lifecycleScope.launch {
            loginViewModel.getState().collect { onLoginStateChanged(it) }
        }
    }

    private fun onLoginStateChanged(newState: LoginState) {
        when (newState) {
            is LoginState.LoginSuccess -> {
                val token = newState.token
                storage.putAuthToken(token)
            }
            is LoginState.RegisterSuccess -> {
                val userModel = newState.userModel
                storage.putUserModel(userModel)
            }
            is LoginState.LogoutSuccess -> {
                storage.removeAuthToken()
            }
            is LoginState.Error -> onError(newState.textId)
            else -> { }
        }
    }

    private fun onError(textId: Int) {
        binding?.let {
            showSnackbar(
                it.root,
                getString(textId),
                duration = 1500
            )
        }
    }

    private fun init() {
        /*if (storage.getFlagIsUserLoggedIn()) {
            pageAdapter = PagesAdapter()
            bindingContent?.apply {
                eventsViewPager.adapter = pageAdapter
                TabLayoutMediator(
                    eventsTabLayout,
                    eventsViewPager,
                    true,
                    true,
                    onPageChangeListener
                ).attach()
                eventsToolbar.setNavigationOnClickListener {
                    println("toolbar opened")
                    binding?.drawerEventsRoot?.open()
                }
                //viewModel.requestSync()
            }
            bindingDrawer?.apply {
                drawerProfileBtnExit.setOnClickListener { onExitClick() }
                drawerProfileBtnEvents.setOnClickListener { onEventsClick() }
                drawerProfileBtnProfile.setOnClickListener { onProfileClick() }
            }
        } else {
            notLoggedBinding?.apply {
                profileNotLoggedBtnAction.setOnClickListener { authorizationAction() }
            }
        }*/
        pageAdapter = PagesAdapter()
        bindingContent?.apply {
            eventsViewPager.adapter = pageAdapter
            TabLayoutMediator(
                eventsTabLayout,
                eventsViewPager,
                true,
                true,
                onPageChangeListener
            ).attach()
            eventsToolbar.setNavigationOnClickListener {
                println("toolbar opened")
                binding?.drawerEventsRoot?.open()
            }
            //viewModel.requestSync()
        }
        bindingDrawer?.apply {
            drawerProfileBtnExit.setOnClickListener { onExitClick() }
            drawerProfileBtnEvents.setOnClickListener { onEventsClick() }
            drawerProfileBtnProfile.setOnClickListener { onProfileClick() }
        }
    }

    private val onPageChangeListener = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
        when (position) {
            0 -> tab.text = getString(R.string.see_all_events)
            1 -> tab.text = getString(R.string.see_only_current_events)
        }
    }

    private fun onExitClick() {
        logoutUser()
    }

    private fun logoutUser() {
        loginViewModel.logout(storage.getUserId())
        storage.removeUser()
    }

    private fun onEventsClick() {
        if (findNavController().currentDestination?.id != R.id.mainFragment){
            findNavController().navigateUp()
        }
    }

    private fun onProfileClick() {
        if (true) {
            val action =
                MainFragmentDirections.actionMainFragmentToProfileFragment()
            findNavController().navigate(action)
        } else {
            val action =
                MainFragmentDirections.actionMainFragmentToAuthorizationFragment()
            findNavController().navigate(action)
        }
    }

    private fun authorizationAction() {
        //TODO
    }

    inner class PagesAdapter : FragmentStateAdapter(this) {
        private val pageCurrentEventsFragment = CurrentEventsFragment()
        private val pageAllEventsFragment = AllEventsFragment()

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return pageAllEventsFragment
                1 -> return pageCurrentEventsFragment
            }
            return pageAllEventsFragment
        }
    }
}