package com.example.unitylife.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.unitylife.App
import com.example.unitylife.data.models.EventModel
import com.example.unitylife.databinding.FragmentEventsBinding
import com.example.unitylife.ext.injectViewModel
import com.example.unitylife.list_adapter.binders.data.EventListBinder
import com.example.unitylife.view_models.EventState
import com.example.unitylife.view_models.EventsViewModel
import com.llc.aceplace_ru.di.ViewModelsFactory
import com.llc.aceplace_ru.list_adapter.FeedAdapter
import com.llc.aceplace_ru.list_adapter.FeedItemBinder
import com.llc.aceplace_ru.list_adapter.FeedItemClass
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import utils.SharedPreferencesStorage
import javax.inject.Inject

class AllEventsFragment : Fragment() {
    private lateinit var binding: FragmentEventsBinding
    private lateinit var viewModel: EventsViewModel
    private lateinit var adapter: FeedAdapter

    @Inject
    lateinit var factory: ViewModelsFactory
    lateinit var storage: SharedPreferencesStorage

    private val viewBinders = mutableMapOf<FeedItemClass, FeedItemBinder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).getAppComponent().plusAllEvents().inject(this)
        storage =
            (requireActivity().application as App).getAppComponent().getSharedPreferencesStorage()
        viewModel = injectViewModel(factory)
        //viewModel.getAllEvents(storage.getUserId())
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getState().collect { onEventsStateChanged(it) }
        }
    }

    private fun onEventsStateChanged(newState: EventState) {
        when (newState) {
            is EventState.GetAllEventsSuccess -> {
                adapter.submitList(newState.listOfEvents)
            }
            is EventState.CreateEventSuccess -> {
                //TODO
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createBinders() {
        val data = EventListBinder(
            { data -> onEventClick(data) },
            Glide.with(requireView())
        )
        viewBinders[data.modelClass] = data as FeedItemBinder

        adapter = FeedAdapter(viewBinders)

        var list = mutableListOf<EventModel>()
        list.add(EventModel(1, 42, "Марафон для бегунов", "Спортивное мероприятие", "https://sun9-32.userapi.com/impf/AtLcOt4w-WQ5j-_Mak2rN4x0v0CXA2OSs66fKg/BhQLK37Mdrs.jpg?size=1000x667&quality=96&sign=90471f3aaccaf02d193d5cb8bb9845b6&type=album",
            "Москва, парк Измайловский", "В воскресенье проводится первый марафон для жителей общежития МГТУ им. Баумана", "2022-06-20T12:00:00", "2022-06-20T15:00:00",
            55.7F, 37.6F))
        list.add(EventModel(2, 42, "День открытых дверей", "Образовательное мероприятие", "https://alfakom.uz/images/img/partners/mgtu_baum.jpg",
            "2-я Бауманская ул., д.5, стр.1, Москва", "Приходите на день открытых дверей в МГТУ имени Баумана", "2022-04-25T10:00:00","2022-04-25T18:00:00", 55.7F, 37.6F ))
        adapter.submitList(list as List<Any>?)
    }

    private fun onEventClick(data: EventModel) {
        val actionToDetail =
            MainFragmentDirections.actionMainFragmentToEventDetailFragment(data.eventId)
        findNavController().navigate(actionToDetail)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createBinders()
        binding.eventsRv.adapter = adapter
    }
}