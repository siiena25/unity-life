package com.example.unitylife.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.unitylife.App
import com.example.unitylife.R
import com.example.unitylife.databinding.*
import com.example.unitylife.ext.injectViewModel
import com.example.unitylife.ui.base.ContainerMainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayoutMediator
import com.llc.aceplace_ru.di.ViewModelsFactory
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

    private var pageAdapter: ProfilePagesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = (requireActivity().application as App).getAppComponent().getSharedPreferencesStorage()
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
        if (storage.getFlagIsUserLoggedIn()) {
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
        }
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
        subscribeOnStateChanged()
    }

    private fun init() {
        if (storage.getFlagIsUserLoggedIn()) {
            pageAdapter = ProfilePagesAdapter()
            bindingContent?.apply {
                eventsViewPager.adapter = pageAdapter
                TabLayoutMediator(
                    eventsTabLayout,
                    eventsViewPager,
                    true,
                    true,
                    onPageChangeListener
                ).attach()
                eventsToolbar.setNavigationOnClickListener { binding?.drawerEventsRoot?.open() }
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
        }
    }

    private fun onEventsClick() {
        if (findNavController().currentDestination?.id != R.id.mainFragment){
            findNavController().navigateUp()
        }
    }

    private fun onProfileClick() {
        val action =
            MainFragmentDirections.actionMainFragmentToProfileFragment()
        findNavController().navigate(action)
    }

    private fun authorizationAction() {
        //TODO
    }

    inner class ProfilePagesAdapter : FragmentStateAdapter(this) {
        private val pageCurrentEventsFragment = CurrentEventsFragment()
        private val pageAllEventsFragment = AllEventsFragment()

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return pageCurrentEventsFragment
                1 -> return pageAllEventsFragment
            }
            return pageCurrentEventsFragment
        }
    }
}