package com.example.kesi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.adapter.MessageAdapter
import com.example.kesi.api.GroupApi
import com.example.kesi.data.Message
import com.example.kesi.databinding.ActivityChatBinding
import com.example.kesi.model.GroupDto
import com.example.kesi.setting.RetrofitSetting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding

    //private lateinit var messageList: ArrayList<Message>
    //private lateinit var auth: FirebaseAuth // 인증 객체
    //private lateinit var database: DatabaseReference // DB 객체

    private val retrofit = RetrofitSetting.getRetrofit()
    private val groupApi = retrofit.create(GroupApi::class.java)

    private var gid: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gid = intent.getLongExtra("gid",0);
        Toast.makeText(this,gid.toString(),Toast.LENGTH_SHORT).show()
        groupApi.getGroup(gid!!).enqueue(object : Callback<GroupDto> {
            override fun onResponse(p0: Call<GroupDto>, response: Response<GroupDto>) {
                Toast.makeText(this@ChatActivity,response.code(),Toast.LENGTH_SHORT).show()
                // 액션바에 그룹 이름 보여주기
                supportActionBar?.title = response.body()?.groupName;
            }

            override fun onFailure(p0: Call<GroupDto>, p1: Throwable) {
                Toast.makeText(this@ChatActivity,p1.message,Toast.LENGTH_SHORT).show()
            }

        })

        /*// 메시지 리스트 초기화
        messageList = ArrayList()

        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList)

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = messageAdapter

        // 인증 초기화
        auth = FirebaseAuth.getInstance()
        // DB 초기화
        database = Firebase.database.reference*/

        /*// 그룹 ID 가져오기
        database.child("groups").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (groupSnapshot in snapshot.children) {
                    val groupName = groupSnapshot.child("groupName").getValue(String::class.java)
                    val membersSnapshot = groupSnapshot.child("member")

                    // 현재 사용자가 그룹 멤버에 포함되어 있는지 확인하는 변수
                    var isMember = false

                    // 각 멤버의 값을 확인하여 현재 사용자의 UID가 있는지 확인
                    for (memberSnapshot in membersSnapshot.children) {
                        val memberUid = memberSnapshot.getValue(String::class.java)
                        if (memberUid == auth.currentUser?.uid) {
                            isMember = true
                            break
                        }
                    }

                    // 그룹 이름이 일치하고, 현재 사용자가 그룹 멤버에 포함되어 있을 때
                    if (groupName == targetGroupName && isMember) {
                        groupId = groupSnapshot.key.toString()
                        Log.d("kkk", groupId!!)

                        // 메시지 가져오기 설정
                        setupMessageListener(messageAdapter)
                        break
                    }
                }
            } else {
                Log.d("ksh", "그룹이 존재하지 않습니다.")
            }
        }.addOnFailureListener {
            Log.e("ksh", "그룹을 찾는 데 실패했습니다.")
        }

        // 메시지 전송 버튼 이벤트
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (!message.isBlank()) {
                val messageObject = Message(groupId!!, message, auth.currentUser?.uid!!)

                // 데이터 저장
                database.child("messages").push().setValue(messageObject)

                // 입력값 초기화
                binding.etMessage.setText("")
            }
        }*/
    }

    /*private fun setupMessageListener(messageAdapter: MessageAdapter) {
        database.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in snapshot.children) {
                    val groupId = postSnapshot.child("groupId").getValue(String::class.java)
                    val message = postSnapshot.child("message").getValue(String::class.java)
                    val sendId = postSnapshot.child("sendId").getValue(String::class.java)

                    if (groupId != null && message != null && sendId != null) {
                        val messageObject = Message(groupId, message, sendId)
                        if (messageObject.groupId == groupId) {
                            messageList.add(messageObject)
                        }
                    }
                }
                // 어댑터에 변경 사항 적용
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ksh", "메시지 수신 실패: ${error.message}")
            }
        })
    }*/
}