package com.example.kesi.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.data.User
import com.example.kesi.databinding.ItemAddGroupMemberBinding
import com.example.kesi.holder.AddGroupMemberHolder
import com.example.kesi.model.FriendsDto
import com.example.kesi.model.GroupMemberDto

class AddGroupMemberAdapter(val friendsList: ArrayList<FriendsDto>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = SparseBooleanArray()

    //뷰 홀더를 준비하려고 자동으로 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGroupMemberHolder {
        val binding = ItemAddGroupMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddGroupMemberHolder(binding)
    }

    //항목의 개수를 판단하려고 자동으로 호출되는 함수
    override fun getItemCount(): Int = friendsList.size

    //뷰 홀더의 뷰에 데이터를 출력하려고 자동으로 호출되는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AddGroupMemberHolder).binding
        binding.ivProfilePicture.setImageResource(R.drawable.ic_user)
        binding.tvName.text = friendsList[position].nickname

        // 체크박스 상태를 SparseBooleanArray에서 가져와 설정
        binding.cb.isChecked = items.get(position, false)

        // 체크박스 상태 변경 리스너 설정
        binding.cb.setOnCheckedChangeListener { _, isChecked ->
            items.put(position, isChecked)
        }
    }

    // 체크된 항목들을 가져오는 메서드
    fun getCheckedMembers(): ArrayList<GroupMemberDto> {
        val checkedMembers = ArrayList<GroupMemberDto>()
        // 체크된 항목들만 리스트에 담기
        for (i in 0 until items.size()) {
            if (items.valueAt(i)) {
                checkedMembers.add(GroupMemberDto(friendsList[i].email,friendsList[i].nickname,friendsList[i].gender,friendsList[i].imgPath))
            }
        }
        // 체크된 항목들 리스트 리턴
        return checkedMembers
    }
}