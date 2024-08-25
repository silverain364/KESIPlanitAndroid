package com.example.kesi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.data.User
import com.example.kesi.adapter.FriendListAdapter
import com.example.kesi.databinding.FragmentFriendListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FriendListFragment : Fragment() {
    lateinit var binding:FragmentFriendListBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //인증 초기화
        auth = FirebaseAuth.getInstance()
        //db 초기화
        database = Firebase.database.reference

        val userList = ArrayList<User>()
        val adapter = FriendListAdapter(userList)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(),LinearLayoutManager.VERTICAL))

        // 현재 사용자의 친구 UID들을 가져옴.
        if (auth.currentUser?.uid != null) {
            database.child("user").child(auth.currentUser?.uid!!).child("friends").get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (friendSnapshot in snapshot.children) {
                        val friendUid = friendSnapshot.key // 친구 UID

                        // 각 친구 UID로 해당 사용자의 정보를 가져옴.
                        if (friendUid != null) {
                            database.child("user").child(friendUid).get().addOnSuccessListener { userSnapshot ->
                                if (userSnapshot.exists()) {
                                    val image = userSnapshot.child("image").getValue(Int::class.java)?:0
                                    val nickname = userSnapshot.child("nickname").getValue(String::class.java)
                                    val email = userSnapshot.child("email").getValue(String::class.java)
                                    val gender = userSnapshot.child("gender").getValue(String::class.java)
                                    val birth = userSnapshot.child("birth").getValue(String::class.java)

                                    Log.d("ksh", "$nickname $email $gender $birth $image")
                                    if (nickname != null && email != null && gender != null && birth != null) {
                                        userList.add(User(image, nickname, email, gender, birth))
                                        adapter.notifyItemInserted(userList.size-1)
                                    }
                                }
                            }.addOnFailureListener {
                                Toast.makeText(context,"친구의 정보를 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "친구가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "친구 목록을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}