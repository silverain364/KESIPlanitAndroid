package com.example.kesi.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.R
import com.example.kesi.adapter.AllSchedulesRecyclerViewAdapter
import com.example.kesi.adapter.MessageAdapter
import com.example.kesi.api.GroupApi
import com.example.kesi.data.Message
import com.example.kesi.databinding.ActivityChatBinding
import com.example.kesi.model.AllSchedulesDto
import com.example.kesi.model.GroupDto
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

    private lateinit var mDetector: GestureDetectorCompat
    private var prevFocus: View? = null

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables", "ClickableViewAccessibility")
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

        mDetector = GestureDetectorCompat(this, SingleTapListener())

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
                //val topBarHeight = binding.topBar.height

                val bottomBarRange = (bottomBarHeight/* + topBarHeight*/) / realHeight.toFloat()


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

        binding.etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.btnSend.background = getDrawable(R.drawable.ic_send)
            } else {
                binding.btnSend.background = getDrawable(R.drawable.ic_btn_calendar)
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

    // 터치 영역에 따라 키보드를 숨기기 위해 구현
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // Activity에서 터치 이벤트가 발생할 때 현재 포커스를 가진 뷰를 저장
        if (ev.action == MotionEvent.ACTION_UP)
            prevFocus = currentFocus
        val result = super.dispatchTouchEvent(ev)
        // dispatchTouchEvent 호출 후 singleTapUp 제스처 탐지
        mDetector.onTouchEvent(ev)
        return result
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // ACTION_UP 이벤트에서 포커스를 가진 뷰가 EditText일 때 터치 영역을 확인하여 키보드를 토글
            if (e.action == MotionEvent.ACTION_UP && prevFocus is EditText) {
                val prevFocus = prevFocus ?: return false
                // 포커를 가진 EditText의 터치 영역 계산
                val hitRect = Rect()
                prevFocus.getGlobalVisibleRect(hitRect)

                // 터치 이벤트가 EditText의 터치 영역에 속하지 않을 때 키보드를 숨길지 결정
                if (!hitRect.contains(e.x.toInt(), e.y.toInt())) {
                    if (currentFocus is EditText && currentFocus != prevFocus) {
                        // 터치한 영역의 뷰가 다른 EditText일 때는 키보드를 가리지 않는다.
                        return false
                    } else {
                        // 터치한 영역이 EditText의 터치 영역 밖이면서 다른 EditText가 아닐 때 키보드 hide
                        getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(prevFocus.windowToken, 0)
                        prevFocus.clearFocus()
                    }
                }
            }
            return super.onSingleTapUp(e)
        }
    }
}