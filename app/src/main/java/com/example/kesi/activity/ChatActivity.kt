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
import com.example.kesi.model.MessageDto
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

    private var messageList: ArrayList<Message> = ArrayList<Message>()
    private lateinit var auth: FirebaseAuth // 인증 객체
    private lateinit var database: DatabaseReference // DB 객체

    private val retrofit = RetrofitSetting.getRetrofit()
    private val groupApi = retrofit.create(GroupApi::class.java)

    private var gid: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인증 초기화
        auth = FirebaseAuth.getInstance()
        // DB 초기화
        database = Firebase.database.reference

        // 인텐트로 gid 받기
        gid = intent.getLongExtra("gid",0);
        // 서버에서 gid에 해당하는 그룹을 가져와서 액션바 그룹 이름 변경
        groupApi.getGroup(gid!!).enqueue(object : Callback<GroupDto> {
            override fun onResponse(p0: Call<GroupDto>, response: Response<GroupDto>) {
                // 액션바에 그룹 이름 보여주기
                binding.tvTitle.setText(response.body()?.groupName)
            }

            override fun onFailure(p0: Call<GroupDto>, p1: Throwable) {
                Toast.makeText(this@ChatActivity,p1.message,Toast.LENGTH_SHORT).show()
            }
        })

        val messageAdapter = MessageAdapter(this@ChatActivity, messageList)

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = messageAdapter

        // 메시지 리스너 설정
        setupMessageListener(messageAdapter)

        // 메시지 전송 버튼 이벤트
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            if (!messageText.isBlank()) {
                val message = Message(messageText, auth.currentUser?.email.toString())

                // 데이터 저장
                database.child("messages").child(gid.toString()).child(System.currentTimeMillis().toString()).setValue(message)

                // 입력값 초기화
                binding.etMessage.setText("")
            }
        }
    }

    private fun setupMessageListener(messageAdapter: MessageAdapter) {
        Log.d("test","setupMessageListener 진입")
        // 특정 그룹 ID(gid) 하위의 메시지만 수신
        database.child("messages").child(gid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 메시지 리스트 초기화
                    messageList.clear()
                    Log.d("test","리스너 실행")
                    for (postSnapshot in snapshot.children) {
                        // 메시지 객체 가져오기
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
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