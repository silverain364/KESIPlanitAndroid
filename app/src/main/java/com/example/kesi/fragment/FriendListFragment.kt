package com.example.kesi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.adapter.FriendListAdapter
import com.example.kesi.api.FriendsApi
import com.example.kesi.databinding.FragmentFriendListBinding
import com.example.kesi.model.FriendsDto
import com.example.kesi.setting.RetrofitSetting
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendListFragment : Fragment() {
    lateinit var binding:FragmentFriendListBinding
    private lateinit var auth:FirebaseAuth
    //private lateinit var database:DatabaseReference
    private val retrofit = RetrofitSetting.getRetrofit();
    private val friendsApi = retrofit.create(FriendsApi::class.java)
    val userList = ArrayList<FriendsDto>()
    val adapter = FriendListAdapter(userList)
    override fun onResume() {
        super.onResume()
        getFriendsList()
    }

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

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(),LinearLayoutManager.VERTICAL))

        getFriendsList()
        /*// 현재 사용자의 친구 UID들을 가져옴.
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
        }*/
    }

    fun getFriendsList() {
        // 서버에서 친구 목록 가져오기
        friendsApi.getFriends().enqueue(object : Callback<List<FriendsDto>> {
            override fun onResponse(p0: Call<List<FriendsDto>>, response: Response<List<FriendsDto>>) {
                if (response.isSuccessful) {
                    // 응답 받은 데이터를 userList에 추가
                    response.body()?.let { friends ->
                        userList.clear() // 기존 데이터 초기화
                        userList.addAll(friends) // 새 데이터 추가
                        // 이름을 기준으로 오름차순 정렬
                        userList.sortBy { it.nickname }
                        adapter.notifyDataSetChanged() // RecyclerView 갱신
                    }
                } else {
                    Log.d("HTTP", "응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(p0: Call<List<FriendsDto>>, p1: Throwable) {
                Log.d("HTTP", "통신 실패")
            }
        })
    }
}