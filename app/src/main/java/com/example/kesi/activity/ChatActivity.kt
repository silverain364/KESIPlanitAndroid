package com.example.kesi.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.R
import com.example.kesi.adapter.AllSchedulesRecyclerViewAdapter
import com.example.kesi.adapter.MessageAdapter
import com.example.kesi.api.GroupApi
import com.example.kesi.data.Message
import com.example.kesi.databinding.ActivityChatBinding
import com.example.kesi.model.AllSchedulesDto
import com.example.kesi.model.BottomSheetScheduleDto
import com.example.kesi.model.GroupDto
import com.example.kesi.model.MessageDto
import com.example.kesi.setting.RetrofitSetting
import com.example.kesi.util.view.GroupSpaceCalendar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding

    private var messageList: ArrayList<Message> = ArrayList<Message>()
    private lateinit var auth: FirebaseAuth // 인증 객체
    private lateinit var database: DatabaseReference // DB 객체

    private val retrofit = RetrofitSetting.getRetrofit()
    private val groupApi = retrofit.create(GroupApi::class.java)

    private var gid: Long? = null

    private lateinit var allSchedulesRecyclerViewAdapter: AllSchedulesRecyclerViewAdapter // 전체 일정 리사이클러뷰 어댑터
    private lateinit var groupSpaceCalendar: GroupSpaceCalendar

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCalendar()

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

        var testSchedule: ArrayList<AllSchedulesDto> = arrayListOf(
            AllSchedulesDto(R.drawable.ic_star2, "오후 미팅", "11","15:00 - 17:00"),
            AllSchedulesDto(R.drawable.ic_star2, "아침 회의", "11","09:00 - 10:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "11","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "12","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "12","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "12","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "12","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "아침 회의", "12","09:00 - 10:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "12","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "13","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "14","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "14","12:00 - 13:00"),
            AllSchedulesDto(R.drawable.ic_star2, "팀 점심", "14","12:00 - 13:00")
        )

        // 전체 일정 리사이클러뷰
        binding.rvAllSchdules.layoutManager = LinearLayoutManager(this@ChatActivity)
        allSchedulesRecyclerViewAdapter = AllSchedulesRecyclerViewAdapter(testSchedule)
        binding.rvAllSchdules.adapter = allSchedulesRecyclerViewAdapter

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //bottomSheet.postDelayed({
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
//                            binding.bottomBar.visibility = View.VISIBLE
                        }

                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
//                            binding.bottomBar.visibility = View.VISIBLE
                        }

                        BottomSheetBehavior.STATE_COLLAPSED -> {
//                            binding.bottomBar.visibility = View.INVISIBLE
                        }
                    }
                    binding.rvChat.scrollToPosition(messageList.lastIndex)
                //}, 10) // 50ms 지연 후 실행
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                (binding.rvChat.layoutParams as ConstraintLayout.LayoutParams).apply {
//                    bottomMargin = (bottomSheet.height * (1 - slideOffset)).toInt()
//                }



                //실제 동작 범위
                val realHeight = (bottomSheet.height - bottomSheetBehavior.expandedOffset - bottomSheetBehavior.peekHeight)

                val validRange = realHeight / bottomSheet.height.toFloat()//최대 반경

                //bottomBar가 다 보이기 위한 퍼센트를 구함
                val bottomBarHeight = binding.bottomBar.height
                val topBarHeight = binding.topBar.height

                val bottomBarRange = (bottomBarHeight + topBarHeight) / realHeight.toFloat()


                Log.d("ChatActivity", "onSlide : height : ${bottomSheet.height} realHeight : ${realHeight} validRange : $validRange")

                if(bottomBarRange <= validRange * slideOffset)
                    binding.bottomGuide.setGuidelinePercent(validRange * slideOffset)
                else
                    binding.bottomGuide.setGuidelinePercent(1f)
            }

        })

        val messageAdapter = MessageAdapter(this@ChatActivity, messageList)

        // RecyclerView
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = messageAdapter

        // 메시지 리스너 설정
        setupMessageListener(messageAdapter)

        // TODO 이미지 변경으로 바꿔야함.
        binding.etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // EditText가 포커스를 받았을 때 버튼 텍스트 변경
                binding.btnSend.text = "전송"
            } else {
                // EditText의 포커스가 사라지면 버튼 텍스트 원래대로 변경
                binding.btnSend.text = "캘린더"
            }
        }

        // 메시지 전송 버튼 이벤트
        binding.btnSend.setOnClickListener {
            // TODO 이미지 비교로 바꿔야함.
            if (binding.btnSend.text.equals("캘린더")) { // 버튼이 캘린더인 경우
                /*TransitionManager.beginDelayedTransition(binding.constraintLayout)
                if (binding.calendarLayout.height != 0) {
                    constraintSet.setGuidelinePercent(binding.guideline.id, 0.0F)
                } else {
                    constraintSet.setGuidelinePercent(binding.guideline.id, 0.3F)
                }
                constraintSet.applyTo(binding.constraintLayout)*/
                // 가장 아래로 스크롤
                binding.rvChat.scrollToPosition(messageList.size-1)
            } else {
                val messageText = binding.etMessage.text.toString()
                if (!messageText.isBlank()) {
                    val message = Message(messageText, auth.currentUser?.email.toString())
                    // 데이터 저장
                    database.child("messages").child(gid.toString()).child(System.currentTimeMillis().toString()).setValue(message)
                    // 입력값 초기화
                    binding.etMessage.setText("")
                    // 포커스 해제
                    binding.etMessage.clearFocus()
                }
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
                    // 가장 아래로 스크롤
                    binding.rvChat.scrollToPosition(messageList.size-1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ksh", "메시지 수신 실패: ${error.message}")
                }
            })
    }

    private fun initCalendar(){
        groupSpaceCalendar = GroupSpaceCalendar(
            binding.monthTv,
            binding.yearTv,
            binding.calendarRv
        )

        binding.calendarPreviousBtn.setOnClickListener {
            groupSpaceCalendar.previousMonth()
        }

        binding.calendarNextBtn.setOnClickListener {
            groupSpaceCalendar.nextMonth()
        }
    }
}