package com.example.kesi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.adapter.AddGroupMemberAdapter
import com.example.kesi.api.FriendsApi
import com.example.kesi.api.GroupApi
import com.example.kesi.data.Group
import com.example.kesi.data.User
import com.example.kesi.databinding.ActivityAddGroupBinding
import com.example.kesi.model.FriendsDto
import com.example.kesi.model.GroupMakeInfoRequestDto
import com.example.kesi.setting.RetrofitSetting
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class AddGroupActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddGroupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val retrofit = RetrofitSetting.getRetrofit()
    private val friendsApi = retrofit.create(FriendsApi::class.java)
    private val groupApi = retrofit.create(GroupApi::class.java)
    val friendsList = ArrayList<FriendsDto>()
    val adapter = AddGroupMemberAdapter(friendsList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //인증 초기화
        auth = FirebaseAuth.getInstance()
        //db 초기화
        database = Firebase.database.reference

        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

        // 서버에서 친구 목록 불러와서 띄워주기
        friendsApi.getFriends().enqueue(object : Callback<List<FriendsDto>> {
            override fun onResponse(p0: Call<List<FriendsDto>>, response: Response<List<FriendsDto>>) {
                response.body()?.let {
                    friends ->
                    friendsList.clear()
                    friendsList.addAll(friends)
                    friendsList.sortBy { it.nickname }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(p0: Call<List<FriendsDto>>, p1: Throwable) {
                Toast.makeText(this@AddGroupActivity,"통신 실패", Toast.LENGTH_SHORT).show()
            }

        })

        binding.btnCreateGroup.setOnClickListener {
            // 체크된 항목(친구)들을 가져옴
            val groupMembers = adapter.getCheckedMembers()
            //val members = ArrayList<String>()
            //val groupId = UUID.randomUUID().toString() // 고유한 그룹 ID 생성
            val groupName = binding.etGroupName.text.toString().trim()
            val inviteUserEmails = ArrayList<String>()
            for (i in 0 until groupMembers.size) {
                inviteUserEmails.add(groupMembers[i].email)
            }
            val groupMakeInfoRequestDto = GroupMakeInfoRequestDto(inviteUserEmails, groupName)
            // 그룹 생성(서버)
            groupApi.creatGroup(groupMakeInfoRequestDto).enqueue(object : Callback<Long> {
                override fun onResponse(p0: Call<Long>, response: Response<Long>) {

                }

                override fun onFailure(p0: Call<Long>, p1: Throwable) {
                    Toast.makeText(this@AddGroupActivity,"통신 실패",Toast.LENGTH_SHORT).show()
                }

            })

            /*// 본인의 UID를 members 리스트에 추가
            val currentUserUid = auth.currentUser?.uid
            if (currentUserUid != null) {
                members.add(currentUserUid)
            }*/

            //val tasks = mutableListOf<Task<DataSnapshot>>()

            /*for (email in emails) {
                val task = database.child("user").orderByChild("email").equalTo(email).get()
                tasks.add(task)

                task.addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val uid = userSnapshot.key  // UID를 가져옴
                            if (uid != null) {
                                members.add(uid)
                            }
                        }
                    } else {
                        Log.d("ksh", "해당 이메일을 가진 사용자가 없습니다.")
                    }
                }.addOnFailureListener {
                    Log.e("ksh", "이메일을 가져오는데 실패했습니다.", it)
                }
            }*/

            /*Tasks.whenAllComplete(tasks).addOnCompleteListener {
                // 모든 이메일에 대한 UID 처리 완료 후 그룹 생성
                if (members.isNotEmpty()) {
                    val group = Group(groupName, members)
                    database.child("groups").child(groupId).setValue(group)
                        .addOnSuccessListener {
                            Log.d("ksh", "그룹 생성 성공: $groupName")
                        }
                        .addOnFailureListener {
                            Log.e("ksh", "그룹 생성 실패", it)
                        }
                } else {
                    Log.d("ksh", "그룹을 생성할 멤버가 없습니다.")
                }
            }*/
        }
    }

    // 뒤로가기 버튼을 눌렀을 때 호출되는 메서드
    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 액티비티 종료, 이전 액티비티로 돌아감
        return true
    }
}