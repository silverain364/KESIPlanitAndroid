package com.example.kesi.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kesi.R
import com.example.kesi.activity.AddFriendsActivity
import com.example.kesi.databinding.FragmentListBinding
import com.google.android.material.tabs.TabLayout

class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 프래그먼트 설정
        if (savedInstanceState == null) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, GroupListFragment())
                .commit()
        }

        //탭레이아웃에 리스너 연결
        binding.tab.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {//그룹 탭 선택 시 그룹 프래그먼트로 전환
                        replaceFragment(GroupListFragment())
                    }
                    1 -> {//친구 탭 선택 시 친구 프래그먼트로 전환
                        replaceFragment(FriendListFragment())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            //탭을 다시 클릭했을 경우
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {//그룹 탭 선택 시 그룹 프래그먼트로 전환
                        replaceFragment(GroupListFragment())
                    }
                    1 -> {//친구 탭 선택 시 친구 프래그먼트로 전환
                        replaceFragment(FriendListFragment())
                    }
                }
            }

        })

        //플로팅 액션 버튼 열고 닫기
        binding.fabAdd.setOnClickListener{
            when(binding.fabAddFriends.visibility) {
                View.VISIBLE -> {
                    binding.fabAddFriends.visibility = View.INVISIBLE
                    binding.fabAddGroup.visibility = View.INVISIBLE
                }
                View.INVISIBLE -> {
                    binding.fabAddFriends.visibility = View.VISIBLE
                    binding.fabAddGroup.visibility = View.VISIBLE
                }
            }
        }

        //친구 추가 버튼 클릭 시 친구 추가 화면으로 이동
        binding.fabAddFriends.setOnClickListener{
            val intent:Intent = Intent(context, AddFriendsActivity::class.java)
            startActivity(intent)
            /*replaceFragment(AddFriendsFragment())
            binding.fabAddGroup.visibility = View.INVISIBLE//fab버튼 안보이게
            binding.fabAddFriends.visibility = View.INVISIBLE//fab버튼 안보이게*/
        }
    }

    //프래그먼트 교체하는 함수
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}