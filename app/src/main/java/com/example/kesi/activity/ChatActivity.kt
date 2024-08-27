package com.example.kesi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.adapter.MessageAdapter
import com.example.kesi.data.Message
import com.example.kesi.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding

    private lateinit var messageList: ArrayList<Message>
    private lateinit var auth: FirebaseAuth // 인증 객체
    private lateinit var database: DatabaseReference // DB 객체

    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val targetGroupName = intent.getStringExtra("groupName")

        // 메시지 리스트 초기화
        messageList = ArrayList()

        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList)

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = messageAdapter

        // 인증 초기화
        auth = FirebaseAuth.getInstance()
        // DB 초기화
        database = Firebase.database.reference

        // 액션바에 그룹 이름 보여주기
        supportActionBar?.title = targetGroupName

        // 그룹 ID 가져오기
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
        }
    }

    private fun setupMessageListener(messageAdapter: MessageAdapter) {
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
    }
}