package com.ryu.dicodingeventsv2.ui.events

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ryu.dicodingeventsv2.R
import com.ryu.dicodingeventsv2.databinding.FragmentEventsBinding
import com.ryu.dicodingeventsv2.ui.EventAdapter
import com.ryu.dicodingeventsv2.ui.EventViewModel

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        eventViewModel.fetchEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter{ event ->
            Navigation.findNavController(requireView())
                .navigate(R.id.detailFragment, Bundle().apply {
                    putString("eventId", event.id.toString())
                })
            Log.d("EventFragment", "Navigating to DetailFragment with eventId: ${event.id}")
        }
        binding.rvEvents.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        eventViewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}