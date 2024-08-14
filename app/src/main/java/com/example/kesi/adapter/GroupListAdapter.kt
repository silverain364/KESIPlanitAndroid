package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.data.Group
import com.example.kesi.databinding.ItemFriendListBinding
import com.example.kesi.databinding.ItemGroupListBinding
import com.example.kesi.holder.FriendListViewHolder
import com.example.kesi.holder.GroupListViewHolder

class GroupListAdapter(val group:ArrayList<Group>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemGroupListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GroupListViewHolder(binding)
    }

    override fun getItemCount(): Int = group.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as GroupListViewHolder).binding
        binding.tvGroupName.text = group[position].groupName
        binding.tvNumber.text = (group[position].member.size+1).toString()
    }
}