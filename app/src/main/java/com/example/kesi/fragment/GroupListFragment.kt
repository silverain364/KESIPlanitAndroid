package com.example.kesi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kesi.adapter.GroupListAdapter
import com.example.kesi.api.GroupApi
import com.example.kesi.databinding.FragmentGroupListBinding
import com.example.kesi.model.GroupSimpleDto
import com.example.kesi.setting.RetrofitSetting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupListFragment : Fragment() {
    lateinit var binding: FragmentGroupListBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference

    private val retrofit = RetrofitSetting.getRetrofit();
    private val groupApi = retrofit.create(GroupApi::class.java)

    val groupList = ArrayList<GroupSimpleDto>()
    val adapter = GroupListAdapter(groupList)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //인증 초기화
        auth = FirebaseAuth.getInstance()
        //db 초기화
        database = Firebase.database.reference

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))

        // 그룹 리스트 가져오기
        getGroupList()
    }

    private fun getGroupList() {
        groupApi.getAllGroups().enqueue(object : Callback<List<GroupSimpleDto>> {
            override fun onResponse(p0: Call<List<GroupSimpleDto>>, response: Response<List<GroupSimpleDto>>) {
                groupList.clear()
                response.body()?.let { groupList.addAll(it) }
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(p0: Call<List<GroupSimpleDto>>, p1: Throwable) {
                Toast.makeText(context,"통신 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}