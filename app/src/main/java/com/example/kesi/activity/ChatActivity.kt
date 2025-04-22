package com.example.kesi.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.toColor
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.R
import com.example.kesi.adapter.AllSchedulesRecyclerViewAdapter
import com.example.kesi.adapter.GroupCalendarScheduleSummaryAdapter
import com.example.kesi.adapter.MessageAdapter
import com.example.kesi.api.GroupApi
import com.example.kesi.data.GroupCalendarScheduleSummaryItem
import com.example.kesi.data.Message
import com.example.kesi.databinding.ActivityChatBinding
import com.example.kesi.domain.*
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
import java.time.LocalDate
import java.time.LocalTime


class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding

    private var messageList: ArrayList<Message> = ArrayList()
    private lateinit var auth: FirebaseAuth // 인증 객체
    private lateinit var database: DatabaseReference // DB 객체

    private val retrofit = RetrofitSetting.getRetrofit()
    private val groupApi = retrofit.create(GroupApi::class.java)

    private var gid: Long? = null

    private lateinit var allSchedulesRecyclerViewAdapter: AllSchedulesRecyclerViewAdapter // 전체 일정 리사이클러뷰 어댑터
    private lateinit var groupCalendarScheduleSummaryAdapter: GroupCalendarScheduleSummaryAdapter
    private lateinit var groupSpaceCalendar: GroupSpaceCalendar

    private lateinit var mDetector: GestureDetectorCompat
    private var prevFocus: View? = null
    private var isSendBtn = false

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // 인증 초기화
        database = Firebase.database.reference // DB 초기화
        gid = intent.getLongExtra("gid",0);// 인텐트로 gid 받기
        mDetector = GestureDetectorCompat(this, SingleTapListener())


        // 서버에서 gid에 해당하는 그룹을 가져와서 액션바 그룹 이름 변경
        loadGroupInformation()


        // 전체 일정 리사이클러뷰
        allSchedulesRecyclerViewAdapter = AllSchedulesRecyclerViewAdapter(getTeatAllSchedulesDto())
        groupCalendarScheduleSummaryAdapter = GroupCalendarScheduleSummaryAdapter(getGroupCalendarScheduleSummaryItem());

        binding.rvAllSchdules.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = groupCalendarScheduleSummaryAdapter
        }

        //bottomSheat 설정
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

        //message adapter 설정
        val messageAdapter = MessageAdapter(this@ChatActivity, messageList)

        binding.rvChat.apply { // RecyclerView
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }
        setupMessageListener(messageAdapter) // 메시지 리스너 설정



        binding.etMessage.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s != null && (s.isNotEmpty() || s.isNotBlank())) {
                    binding.btnSend.background = getDrawable(R.drawable.ic_send)
                    isSendBtn = true
                } else {
                    binding.btnSend.background = getDrawable(R.drawable.ic_btn_calendar)
                    isSendBtn = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        // 메시지 전송 버튼 이벤트
        binding.btnSend.setOnClickListener {
            Log.d("ChatActivity", "btnSend : $isSendBtn")
            if (!isSendBtn) { // 버튼이 캘린더인 경우
                val intent = Intent(this, AddGroupScheduleActivity::class.java)
                startActivity(intent)
                /*bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                // 가장 아래로 스크롤
                binding.rvChat.scrollToPosition(messageList.size-1)*/
                return@setOnClickListener
            }

            val messageText = binding.etMessage.text.toString()
            if (messageText.isNotBlank()) {
                val message = Message(messageText, auth.currentUser?.email.toString())
                // 데이터 저장
                database.child("messages").child(gid.toString()).child(System.currentTimeMillis().toString()).setValue(message)
                // 입력값 초기화
                binding.etMessage.setText("")
                binding.etMessage.requestFocus() // 포커스 유지
                /*// 포커스 해제
                binding.etMessage.clearFocus()*/
            }

        }

        binding.btnPlus.setOnClickListener{
            if (binding.extendedBottomBar.visibility == View.GONE) {
                binding.extendedBottomBar.visibility = View.VISIBLE
            } else {
                binding.extendedBottomBar.visibility = View.GONE
            }

            binding.bottomGuide.viewTreeObserver.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.bottomGuide.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    binding.bottomSheet.post {
                        bottomSheetBehavior.peekHeight += 1
                        bottomSheetBehavior.peekHeight -= 1
                    }
                }
            })
        }

    }

    private fun loadGroupInformation() {
        groupApi.getGroup(gid!!).enqueue(object : Callback<GroupDto> {
            override fun onResponse(p0: Call<GroupDto>, response: Response<GroupDto>) {
                if(response.body() == null) return
                binding.tvTitle.text = response.body()?.groupName  // 액션바에 그룹 이름 보여주기
                initCalendar(response.body()!!)
            }

            override fun onFailure(p0: Call<GroupDto>, p1: Throwable) {
                Toast.makeText(this@ChatActivity,p1.message,Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun initCalendar(groupDto: GroupDto){
        groupSpaceCalendar = GroupSpaceCalendar(
            binding.monthTv,
            binding.yearTv,
            binding.calendarRv,
            groupDto
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

    private fun getTeatAllSchedulesDto() = arrayListOf(
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


    private fun getGroupCalendarScheduleSummaryItem() = arrayListOf(
        GroupCalendarScheduleSummaryItem.Day(LocalDate.of(2025, 4, 1)),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.GROUP),
        GroupCalendarScheduleSummaryItem.Item(GroupSchedule(
            id = 1L,
            start = LocalDate.of(2025, 4, 24),
            end = LocalDate.of(2025, 4, 24),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            color = Color.RED.toColor(),
            title = "아침 회의",
            description = "전체 팀과 진행 상황 공유",
            link = "https://meet.google.com/abc-defg-hij",
            place = "회의실 A",
            securityLevel = SecurityLevel.MEDIUM,
            sourceCalendarId = 1L
        )),
        GroupCalendarScheduleSummaryItem.Item(GroupSchedule(
            id = 2L,
            start = LocalDate.of(2025, 4, 25),
            end = LocalDate.of(2025, 4, 25),
            startTime = LocalTime.of(13, 30),
            endTime = LocalTime.of(15, 0),
            color = Color.BLUE.toColor(),
            title = "클라이언트 발표",
            description = "프로젝트 중간 발표 및 피드백 수렴",
            link = "https://zoom.us/j/123456789",
            place = "온라인",
            securityLevel = SecurityLevel.HIGH,
            sourceCalendarId = 1L
        )),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.PERSONAL),
        GroupCalendarScheduleSummaryItem.Item(PersonalSchedule(
            id = 3L,
            start = LocalDate.of(2025, 4, 26),
            end = LocalDate.of(2025, 4, 27),
            startTime = LocalTime.of(20, 0),
            endTime = LocalTime.of(23, 0),
            color = Color.GREEN.toColor(),
            title = "야간 코딩 세션",
            description = "해커톤 대비 팀 작업",
            link = "",
            place = "스터디룸 2",
            securityLevel = SecurityLevel.LOW
        )),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.OTHER),
        GroupCalendarScheduleSummaryItem.Item(
            OtherSchedule(
                id = 4L,
                start = LocalDate.of(2025, 4, 28),
                end = LocalDate.of(2025, 4, 28),
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(12, 0),
                color = Color.YELLOW.toColor(),
                title = "부서 간 협업 미팅",
                description = "마케팅팀과 개발팀 간 아이디어 회의",
                link = "https://teams.microsoft.com/meeting",
                place = "회의실 B",
                securityLevel = SecurityLevel.MEDIUM
            )
        ),
        GroupCalendarScheduleSummaryItem.Item(
            OtherSchedule(
                id = 5L,
                start = LocalDate.of(2025, 4, 29),
                end = LocalDate.of(2025, 4, 29),
                startTime = LocalTime.of(8, 30),
                endTime = LocalTime.of(9, 30),
                color = Color.CYAN.toColor(),
                title = "리더십 교육",
                description = "사내 리더십 강화 프로그램",
                link = "",
                place = "강의실 1",
                securityLevel = SecurityLevel.LOW
            )
        ),
        GroupCalendarScheduleSummaryItem.Day(LocalDate.of(2025, 5, 1)),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.GROUP),
        GroupCalendarScheduleSummaryItem.Item(
            GroupSchedule(
                id = 6L,
                start = LocalDate.of(2025, 5, 1),
                end = LocalDate.of(2025, 5, 1),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 30),
                color = Color.LTGRAY.toColor(),
                title = "UX 리뷰",
                description = "앱 UI/UX 개선 사항 논의",
                link = "",
                place = "디자인실",
                securityLevel = SecurityLevel.MEDIUM,
                sourceCalendarId = 1L
            )
        ),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.PERSONAL),
        GroupCalendarScheduleSummaryItem.Item(
            PersonalSchedule(
                id = 7L,
                start = LocalDate.of(2025, 5, 1),
                end = LocalDate.of(2025, 5, 1),
                startTime = LocalTime.of(12, 0),
                endTime = LocalTime.of(13, 0),
                color = Color.GRAY.toColor(),
                title = "점심 미팅",
                description = "멘토와의 캐주얼 미팅",
                link = "",
                place = "카페",
                securityLevel = SecurityLevel.LOW
            )
        ),

        GroupCalendarScheduleSummaryItem.Day(LocalDate.of(2025, 5, 2)),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.GROUP),
        GroupCalendarScheduleSummaryItem.Item(
            GroupSchedule(
                id = 8L,
                start = LocalDate.of(2025, 5, 2),
                end = LocalDate.of(2025, 5, 2),
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(17, 0),
                color = Color.MAGENTA.toColor(),
                title = "보안 교육",
                description = "개인정보 보호 및 보안 정책 교육",
                link = "",
                place = "온라인 강의",
                securityLevel = SecurityLevel.HIGH,
                sourceCalendarId = 1L
            )
        ),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.OTHER),
        GroupCalendarScheduleSummaryItem.Item(
            OtherSchedule(
                id = 9L,
                start = LocalDate.of(2025, 5, 2),
                end = LocalDate.of(2025, 5, 2),
                startTime = LocalTime.of(16, 0),
                endTime = LocalTime.of(18, 0),
                color = Color.DKGRAY.toColor(),
                title = "팀 빌딩",
                description = "팀워크 강화를 위한 활동",
                link = "",
                place = "야외 운동장",
                securityLevel = SecurityLevel.LOW
            )
        ),

        GroupCalendarScheduleSummaryItem.Day(LocalDate.of(2025, 5, 3)),
        GroupCalendarScheduleSummaryItem.Kind(ScheduleType.GROUP),
        GroupCalendarScheduleSummaryItem.Item(
            GroupSchedule(
                id = 10L,
                start = LocalDate.of(2025, 5, 3),
                end = LocalDate.of(2025, 5, 4),
                startTime = LocalTime.of(18, 0),
                endTime = LocalTime.of(22, 0),
                color = Color.BLACK.toColor(),
                title = "해커톤",
                description = "사내 해커톤 행사 (1박 2일)",
                link = "https://hackathon.company.com",
                place = "본사 1층",
                securityLevel = SecurityLevel.HIGH,
                sourceCalendarId = 1L
            )
        )
    )

}