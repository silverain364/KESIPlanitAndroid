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
    //뷰 홀더를 준비하려고 자동으로 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemGroupListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GroupListViewHolder(binding)
    }

    //항목의 개수를 판단하려고 자동으로 호출되는 함수
    override fun getItemCount(): Int = group.size

    //뷰 홀더의 뷰에 데이터를 출력하려고 자동으로 호출되는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as GroupListViewHolder).binding
        binding.tvGroupName.text = group[position].groupName
        binding.tvNumber.text = (group[position].member.size).toString()
        //TODO 마지막 대화내용 보여주기
        binding.tvContent.text = "마지막 대화 내용"
    }
}