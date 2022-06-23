package com.example.unitylife.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.unitylife.App
import com.example.unitylife.data.models.EventModel
import com.example.unitylife.data.models.UserModel
import com.example.unitylife.databinding.FragmentEventDetailBinding
import com.example.unitylife.list_adapter.binders.data.MemberListBinder
import com.example.unitylife.utils.TimeUtils.getDateTimeFromTimestamp
import com.llc.aceplace_ru.di.ViewModelsFactory
import com.llc.aceplace_ru.list_adapter.FeedAdapter
import com.llc.aceplace_ru.list_adapter.FeedItemBinder
import com.llc.aceplace_ru.list_adapter.FeedItemClass
import utils.SharedPreferencesStorage
import javax.inject.Inject


class EventDetailFragment : Fragment() {
    private lateinit var binding: FragmentEventDetailBinding

    private lateinit var adapter: FeedAdapter

    private val viewBinders = mutableMapOf<FeedItemClass, FeedItemBinder>()

    @Inject
    lateinit var factory: ViewModelsFactory
    lateinit var storage: SharedPreferencesStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage =
            (requireActivity().application as App).getAppComponent().getSharedPreferencesStorage()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventModel = EventModel(1, 42, "Марафон для бегунов", "Спортивное мероприятие", "https://sun9-32.userapi.com/impf/AtLcOt4w-WQ5j-_Mak2rN4x0v0CXA2OSs66fKg/BhQLK37Mdrs.jpg?size=1000x667&quality=96&sign=90471f3aaccaf02d193d5cb8bb9845b6&type=album",
            "Москва, парк Измайловский", "В воскресенье проводится первый марафон для жителей общежития МГТУ им. Баумана", "2022-06-20T12:00:00", "2022-06-20T15:00:00",
            55.7F, 37.6F)

        binding.apply {
            title.text = eventModel.title
            categoryTitle.text = eventModel.categoryTitle
            address.text = eventModel.address
            eventDescription.text = eventModel.description
            eventDetailTimeStartText.text = "Начало: " + getDateTimeFromTimestamp(eventModel.timestart)
            eventDetailTimeEndText.text = "Конец: " + getDateTimeFromTimestamp(eventModel.timeend)
            Glide.with(requireView()).load(eventModel.eventAvatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(320)
                .into(eventImage)
        }
        createBinders()
        binding.rvMemberList.adapter = adapter
    }

    @Suppress("UNCHECKED_CAST")
    private fun createBinders() {
        val data = MemberListBinder(
            { data -> onMemberClick(data) },
            Glide.with(requireView())
        )
        viewBinders[data.modelClass] = data as FeedItemBinder

        adapter = FeedAdapter(viewBinders)

        val userModel = UserModel(1, "Юлия", "Костюнина", "yulia2012394@gmail.com", 21, "Female", "Россия", "Москва", "161026", "USER", 123456)
        adapter.submitList(listOf(userModel) as List<Any>?)
    }

    private fun onMemberClick(data: UserModel) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
}