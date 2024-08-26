package com.example.kesi.adapter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.data.User
import com.example.kesi.databinding.ItemAddGroupMemberBinding
import com.example.kesi.holder.AddGroupMemberHolder

class AddGroupMemberAdapter(val user: ArrayList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val checkedItems = SparseBooleanArray()

    //뷰 홀더를 준비하려고 자동으로 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGroupMemberHolder {
        val binding = ItemAddGroupMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddGroupMemberHolder(binding)
    }

    //항목의 개수를 판단하려고 자동으로 호출되는 함수
    override fun getItemCount(): Int = user.size

    //뷰 홀더의 뷰에 데이터를 출력하려고 자동으로 호출되는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AddGroupMemberHolder).binding
        binding.ivProfilePicture.setImageResource(user[position].image)
        binding.tvName.text = user[position].nickname
        val email = user[position].email

        // 체크박스 상태를 SparseBooleanArray에서 가져와 설정
        binding.cb.isChecked = checkedItems.get(position, false)

        // 체크박스 상태 변경 리스너 설정
        binding.cb.setOnCheckedChangeListener { _, isChecked ->
            checkedItems.put(position, isChecked)
        }
    }

    // 체크된 항목들의 이메일을 가져오는 메서드
    fun getEmails(): List<String> {
        val checkedEmails = mutableListOf<String>()
        for (i in 0 until checkedItems.size()) {
            val position = checkedItems.keyAt(i)
            if (checkedItems.valueAt(i)) {
                checkedEmails.add(user[position].email)
            }
        }
        return checkedEmails
    }
}