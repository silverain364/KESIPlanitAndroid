package com.example.kesi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.databinding.FragmentFriendListBinding
import com.example.kesi.databinding.FragmentListBinding
import com.example.kesi.databinding.ItemFriendListBinding

class FriendListFragment : Fragment() {
    lateinit var binding:FragmentFriendListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileList = ArrayList<Profile>()
        //TODO 친구목록 삽입
        profileList.add(Profile(name="김재원"))
        profileList.add(Profile(name="김태수"))
        profileList.add(Profile(name="김시현"))
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = FriendListAdapter(profileList)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(),LinearLayoutManager.VERTICAL))
    }
}