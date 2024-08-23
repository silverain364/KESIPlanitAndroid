package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.holder.FriendListViewHolder
import com.example.kesi.data.User
import com.example.kesi.databinding.ItemFriendListBinding

class FriendListAdapter(val user: ArrayList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //뷰 홀더를 준비하려고 자동으로 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val binding = ItemFriendListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FriendListViewHolder(binding)
    }

    //항목의 개수를 판단하려고 자동으로 호출되는 함수
    override fun getItemCount(): Int = user.size

    //뷰 홀더의 뷰에 데이터를 출력하려고 자동으로 호출되는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as FriendListViewHolder).binding
        binding.imageView.setImageResource(user[position].image)
        binding.tvName.text = user[position].nickname
        binding.itemRoot.setOnClickListener{
            //TODO 각각의 항목을 클릭했을 때 이벤트 처리
        }
    }
}