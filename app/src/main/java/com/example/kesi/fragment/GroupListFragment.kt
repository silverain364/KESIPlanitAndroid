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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GroupListFragment : Fragment() {
    lateinit var binding: FragmentGroupListBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //인증 초기화
        auth = FirebaseAuth.getInstance()
        //db 초기화
        database = Firebase.database.reference

        val groupList = ArrayList<Group>()
        val adapter = GroupListAdapter(groupList)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))

        // 그룹 데이터를 가져옴
        database.child("groups").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (groupSnapshot in snapshot.children) {
                    // 각 그룹의 멤버 UID 리스트를 가져옴
                    val members = ArrayList<String>()
                    for (memberSnapshot in groupSnapshot.child("member").children) {
                        val memberUid = memberSnapshot.getValue(String::class.java)
                        if (memberUid != null) {
                            members.add(memberUid)
                        }
                    }

                    // 현재 유저의 UID가 그룹의 멤버 목록에 포함되어 있는지 확인
                    if (auth.currentUser?.uid in members) {
                        val groupName = groupSnapshot.child("groupName").getValue(String::class.java) ?: "이름 없음"

                        // 그룹을 리스트에 추가
                        groupList.add(Group(groupName, members))
                    }
                }

                // 어댑터에 데이터 변경 사항 알림
                adapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            // 데이터를 가져오는 중에 오류 발생 시 처리
            it.printStackTrace()
        }
    }
}