package com.example.kesi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.R
import com.example.kesi.adapter.FriendListAdapter
import com.example.kesi.adapter.GroupListAdapter
import com.example.kesi.data.Group
import com.example.kesi.data.User
import com.example.kesi.databinding.FragmentFriendListBinding
import com.example.kesi.databinding.FragmentGroupListBinding

class GroupListFragment : Fragment() {
    lateinit var binding: FragmentGroupListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO.그룹 추가 하면 될 듯?
        val userList1 = ArrayList<User>()
        userList1.add(User(name = "김시현"))
        userList1.add(User(name = "김태수"))
        userList1.add(User(name = "김재원"))

        val userList2 = ArrayList<User>()
        userList2.add(User(name = "김시현"))
        userList2.add(User(name = "김재원"))

        val userList3 = ArrayList<User>()
        userList3.add(User(name = "김재원"))
        userList3.add(User(name = "김태수"))

        val groupList = ArrayList<Group>()
        groupList.add(Group("ㄱㄱㄱ",userList1))
        groupList.add(Group("ㄴㄴㄴ",userList2))
        groupList.add(Group("ㄷㄷㄷ",userList3))
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = GroupListAdapter(groupList)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))
    }
}