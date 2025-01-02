package com.example.kesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.kesi.databinding.FragmentHomeBinding

class HomeFragment<RecyclerView> : Fragment() {

    //fragment
    private var _binding: CalendarBinding ?= null

    private lateinit var recyclerView: RecyclerView

    //view
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarBinding.inflate(inflater,container,false)

        val view = binding?.root

        initView(_binding!!)

        create_data()
        return view
    }

    private fun initView(binding: CalendarBinding) {
        recyclerView = binding.calRecycler
        var position: Int = Int.MAX_VALUE / 2

        binding?.calRecycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.calRecycler?.adapter = MonthAdpater()

        //item의 위치 지정
        binding?.calRecycler?.scrollToPosition(position)

        //항목씩 스크롤 지정
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding?.calRecycler)

    }

    private fun create_data() {
        binding?.calRecycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}