package com.example.kesi.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.provider.MediaStore
import android.telecom.Call
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kesi.api.UserApi
import com.example.kesi.basic.ImageSetting
import com.example.kesi.data.User
import com.example.kesi.databinding.ActivityProfileSettingsBinding
import com.example.kesi.model.JoinRequestDto
import com.example.kesi.setting.RetrofitSetting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.LocalDate

class ProfileSettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference

    private val retrofit = RetrofitSetting.getRetrofit();
    private val userApi = retrofit.create(UserApi::class.java)
    private var profileImage: File? = null;


    @RequiresApi(Build.VERSION_CODES.O)
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode != RESULT_OK) return@registerForActivityResult
        if(it.data == null) return@registerForActivityResult

        val uri = it.data!!.data ?: return@registerForActivityResult

        val cursor = contentResolver.query(uri, null, null, null)
        cursor!!.moveToNext()

        val index = cursor.getColumnIndex("_data")
        if(index == -1) return@registerForActivityResult

        val path = cursor.getString(index)

        //Todo. 나중에 필요하면 이미지 크기를 줄어야 할수도 있음
        // 1/8 사이즈로 축소
        profileImage = ImageSetting.resizeImage(File(path), this.filesDir.absolutePath, 4)

        Glide.with(this)
            .load(uri)
            .into(binding.ivProfilePicture)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        auth = Firebase.auth

        //db 초기화
        database = Firebase.database.reference

        //인텐트로부터 email과 pw 받기
        val email = intent.getStringExtra("email")

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)

        //액션바의 기본 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //액션바의 기본 타이틀 숨김
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //회원가입 버튼 클릭 시 데이터베이스에 저장 후 로그인 화면으로 이동
        binding.btnSignUp.setOnClickListener {
            /*val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)*/

            val nickname = binding.etNickname.text.toString().trim()
            val gender = when(binding.rgGender.checkedRadioButtonId) {
                binding.rbMen.id -> "남자"
                binding.rbWomen.id -> "여자"
                else -> "선택되지 않음"
            }
            val birth = binding.etDOB.text.toString().trim()

            //Todo. 추후 다양한 예외 처리 필요
            val y = birth.substring(0, 4).toInt();
            val m = birth.substring(4, 6).toInt();
            val d = birth.substring(6, 8).toInt();

            val joinRequestDto =  JoinRequestDto(nickname, LocalDate.of(y, m, d).toString(), gender);

            uploadProfile()

            userApi.join(joinRequestDto).enqueue(object: Callback<String> {
                override fun onResponse(p0: retrofit2.Call<String>, response: Response<String>) {
                    if(response.code() == 200){
                        val intent: Intent = Intent(this@ProfileSettingsActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onFailure(p0: retrofit2.Call<String>, p1: Throwable) {
                    Toast.makeText(this@ProfileSettingsActivity,p1.message,Toast.LENGTH_SHORT).show()
                    Log.d("http", "exception : ${p1.message}")
                }

            })
        }

        binding.ivProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            galleryLauncher.launch(intent)
        }
    }


    //메뉴를 사용자가 선택했을 때의 이벤트 처리를 하는 함수(여기서는 뒤로가기 버튼 때문에 써줘야 함)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 뒤로가기 버튼 클릭 시 처리
                onBackPressedDispatcher.onBackPressed() // 뒤로가기 동작 수행
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun uploadProfile(){
        if(profileImage == null) return

        //image --> MultiPartFile로 변경
        val imageBody = RequestBody.create(MediaType.parse("image/*"), profileImage!!)
        val requestImage = MultipartBody.Part.createFormData(
            "image", profileImage!!.name, imageBody
        )

        userApi.uploadProfile(requestImage).enqueue(object : Callback<String>{
            override fun onResponse(p0: retrofit2.Call<String>, p1: Response<String>) {
            }
            override fun onFailure(p0: retrofit2.Call<String>, p1: Throwable) {
            }
        })
    }


    /*//회원가입 기능
    private fun signUp(email: String) {
        val nickname = binding.etNickname.text.toString().trim()
        val gender = when(binding.rgGender.checkedRadioButtonId) {
            binding.rbMen.id -> "남자"
            binding.rbWomen.id -> "여자"
            else -> "선택되지 않음"
        }
        val birth = binding.etDOB.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 성공 시 실행
                    Toast.makeText(this,"회원가입 성공",Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    addUserToDatabase(auth.currentUser?.uid!!, email, nickname, gender, birth)
                } else {
                    // 실패 시 실행
                    Toast.makeText(this,"회원가입 실패",Toast.LENGTH_SHORT).show()
                }
            }
    }*/

    private fun addUserToDatabase(uId: String, email: String, nickname: String, gender:String, birth:String) {
        database.child("user").child(uId).setValue(User(email = email, nickname = nickname, gender = gender, birth = birth))
    }
}