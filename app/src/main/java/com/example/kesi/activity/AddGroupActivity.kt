package com.example.kesi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.adapter.AddGroupMemberAdapter
import com.example.kesi.data.Group
import com.example.kesi.data.User
import com.example.kesi.databinding.ActivityAddGroupBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class AddGroupActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddGroupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
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

        val userList = ArrayList<User>()
        val adapter = AddGroupMemberAdapter(userList)

        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

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
                                Toast.makeText(applicationContext,"친구의 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "친구가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "친구 목록을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCreateGroup.setOnClickListener {
            // 체크된 항목들의 이메일을 가져옴
            val emails = adapter.getEmails()
            val members = ArrayList<String>()
            val groupId = UUID.randomUUID().toString() // 고유한 그룹 ID 생성
            val groupName = binding.etGroupName.text.toString().trim()

            // 본인의 UID를 members 리스트에 추가
            val currentUserUid = auth.currentUser?.uid
            if (currentUserUid != null) {
                members.add(currentUserUid)
            }

            val tasks = mutableListOf<Task<DataSnapshot>>()

            for (email in emails) {
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
            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener {
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
            }
        }
    }

    // 뒤로가기 버튼을 눌렀을 때 호출되는 메서드
    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 액티비티 종료, 이전 액티비티로 돌아감
        return true
    }
}