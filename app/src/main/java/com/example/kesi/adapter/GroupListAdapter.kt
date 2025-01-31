package com.example.kesi.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.activity.ChatActivity
import com.example.kesi.data.Group
import com.example.kesi.databinding.ItemFriendListBinding
import com.example.kesi.databinding.ItemGroupListBinding
import com.example.kesi.holder.FriendListViewHolder
import com.example.kesi.holder.GroupListViewHolder
import com.example.kesi.model.GroupSimpleDto
import com.example.kesi.setting.RetrofitSetting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GroupListAdapter(val group:ArrayList<GroupSimpleDto>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    //뷰 홀더를 준비하려고 자동으로 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemGroupListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        //인증 초기화
        auth = FirebaseAuth.getInstance()
        //db 초기화
        database = Firebase.database.reference
        return GroupListViewHolder(binding)
    }

    //항목의 개수를 판단하려고 자동으로 호출되는 함수
    override fun getItemCount(): Int = group.size

    //뷰 홀더의 뷰에 데이터를 출력하려고 자동으로 호출되는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as GroupListViewHolder).binding
        binding.tvGroupName.text = group[position].groupName
        binding.tvNumber.text = (group.size).toString()
        //TODO 마지막 대화내용 보여주기
        database.child("messages").child(group[position].gid.toString()).orderByKey() // 메시지를 키(시간순)로 정렬
            .limitToLast(1) // 마지막 1개 항목 가져오기
            .get()
            .addOnSuccessListener { snapshot ->
                val messageSnapshot = snapshot.children.firstOrNull() // 첫 번째(유일한) 메시지 가져오기
                val messageText = messageSnapshot?.child("message")?.getValue(String::class.java)
                if (messageText != null) {
                    binding.tvContent.text = messageText
                } else {
                    binding.tvContent.text = "대화 내용 없음"
                }
            }
            .addOnFailureListener {
                binding.tvContent.text = "오류 발생"
            }
        binding.tvContent.text = "마지막 대화 내용"
        //채팅방을 클릭하면 ChatActivity 화면으로 이동
        binding.itemRoot.setOnClickListener {
            val intent = Intent(binding.root.context,ChatActivity::class.java)
            intent.putExtra("gid",group[position].gid)
            binding.root.context.startActivity(intent)
        }
    }
}