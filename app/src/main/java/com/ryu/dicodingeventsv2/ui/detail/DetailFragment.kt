package com.ryu.dicodingeventsv2.ui.detail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ryu.dicodingeventsv2.R
import com.ryu.dicodingeventsv2.data.ApiConfig
import com.ryu.dicodingeventsv2.data.EventRepository
import com.ryu.dicodingeventsv2.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetailViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("DetailFragment", "onViewCreated called")

        val apiService = ApiConfig.getApiService()
        val repository = EventRepository(apiService)
        val factory = DetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        val eventId = arguments?.getString("eventId")
        Log.d("DetailFragment", "Received eventId: $eventId")
        if (eventId != null) {
            viewModel.fetchEventDetail(eventId)
        }

        observeViewModel()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.eventDetail.observe(viewLifecycleOwner) { eventResponse ->
            Log.d("DetailFragment", "Event detail received: $eventResponse")
            eventResponse?.let { event ->
                Log.d("DetailFragment", "Setting up view with event data")
                if (event != null) {
                    binding.apply {
                        tvEventTitle.text = event.name
                        tvEventCategory.text = event.category
                        tvEventOwner.text = "Organized by:  + ${event.ownerName}"
                        tvEventLocation.text = "Lokasi: ${event.cityName}"

                        val beginTime = event.beginTime?.let { parseDateTime(it) }
                        val endTime = event.endTime?.let { parseDateTime(it) }

                        tvEventDate.text =
                            "Tanggal: ${beginTime?.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}"
                        tvEventTime.text =
                            "Waktu: ${beginTime?.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                                endTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            }"
                        tvEventQuota.text = "Quota: ${event.registrants}/${event.quota}"
                        val sisaQuota = (event.quota ?: 0) - (event.registrants ?: 0)
                        tvSisaQuota.text = "Sisa Quota: ${sisaQuota}"
                        tvEventDescription.text = HtmlCompat.fromHtml(
                            event.description ?: "",
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )

                        Glide.with(this@DetailFragment)
                            .load(event.mediaCover)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(ivEventImage)

                        Log.d("DetailFragment", "All views updated")
                    }
                }
            }
        }
    
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateTime(dateTimeString: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}