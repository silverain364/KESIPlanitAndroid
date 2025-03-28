package com.example.kesi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kesi.api.FriendsApi
import com.example.kesi.api.UserApi
import com.example.kesi.databinding.ActivityAddFriendsBinding
import com.example.kesi.databinding.DialogAddFriendEnailBinding
import com.example.kesi.model.UserInfoDto
import com.example.kesi.setting.RetrofitSetting
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFriendsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddFriendsBinding
    private lateinit var auth: FirebaseAuth
    //private lateinit var database: DatabaseReference

    private val retrofit = RetrofitSetting.getRetrofit();
    private val userApi = retrofit.create(UserApi::class.java)
    private val friendsApi = retrofit.create(FriendsApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //인증 초기화
        auth = FirebaseAuth.getInstance()
        //db 초기화
        //database = Firebase.database.reference

        //db에서 email 읽어와서 보여주기
        userApi.getByUid().enqueue(object : Callback<UserInfoDto> {
            override fun onResponse(p0: Call<UserInfoDto>, response: Response<UserInfoDto>) {
                if (response.code() == 200) {
                    binding.tvEmail.setText(response.body()?.email)
                }
            }

            override fun onFailure(p0: Call<UserInfoDto>, p1: Throwable) {
                Toast.makeText(this@AddFriendsActivity, "이메일을 불러 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })
        /*database.child("user").child(auth.currentUser?.uid!!).child("email").get().addOnSuccessListener {
            binding.tvEmail.setText(it.value.toString())
        }.addOnFailureListener{
            Toast.makeText(this, "이메일을 불러 올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }*/

        //이메일로 친구 추가 버튼을 누르면 다이얼로그를 띄움.
        binding.btnAddEmail.setOnClickListener {
            val builder:AlertDialog.Builder = AlertDialog.Builder(this)
            val dialogBinding = DialogAddFriendEnailBinding.inflate(layoutInflater)
            builder.setView(dialogBinding.root)
            val alertDialog = builder.create()
            //추가하기 버튼을 누르면 존재하는 이메일인지 확인 후 친구 목록에 추가
            dialogBinding.btnAdd.setOnClickListener {
                friendsApi.addFriends(dialogBinding.etEmail.text.toString()).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.code()==200) {
                            // 친구 추가 성공
                            Toast.makeText(this@AddFriendsActivity, "친구 추가 성공", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss() // 다이얼로그 닫기
                        } else {
                            // 친구 추가 실패
                            Toast.makeText(this@AddFriendsActivity, "존재하지 않는 이메일입니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        // 친구 추가 실패
                        Toast.makeText(this@AddFriendsActivity, "친구 추가 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
                // 이메일이 있는지 확인하기 위해 데이터베이스 쿼리
                /*database.child("user").orderByChild("email").equalTo(dialogBinding.etEmail.text.toString()).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) { // 이메일이 존재한다면 UID를 가져옴
                            for (childSnapshot in snapshot.children) {
                                val uid = childSnapshot.key // UID 가져오기
                                // UID를 사용하여 친구 목록에 추가하는 로직
                                addFriend(uid)
                            }
                        } else {//이메일이 존재하지 않는다면
                            Toast.makeText(this, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show()
                        }
                        alertDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "이메일을 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }*/
            }
            alertDialog.show()
        }
    }
    // 뒤로가기 버튼을 눌렀을 때 호출되는 메서드
    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 액티비티 종료, 이전 액티비티로 돌아감
        return true
    }
    private fun addFriend(friendUid: String?) {
        if (auth.currentUser?.uid != null && friendUid != null) {
            // 친구 목록에 UID 추가
            /*database.child("user").child(auth.currentUser?.uid!!).child("friends").child(friendUid).setValue(true)
                .addOnSuccessListener {
                    Toast.makeText(this, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "친구 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }*/
        }
    }
}