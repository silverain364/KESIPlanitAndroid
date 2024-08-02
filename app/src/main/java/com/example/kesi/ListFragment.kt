package com.example.kesi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kesi.databinding.FragmentListBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding

    /*//뷰 페이저 어댑터
    class FragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        val fragments: List<Fragment>
        init {
            fragments = listOf(GroupListFragment(), FriendListFragment())
        }

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*//뷰 페이저에 어댑터 적용
        val adapter = ListFragment.FragmentPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        //탭과 뷰 페이저 연동
        TabLayoutMediator(binding.tab, binding.viewPager) {
                tab, position ->
            when (position) {
                0 -> {
                    tab.text = "그룹"
                }
                1 -> {
                    tab.text = "친구"
                }
            }
        }.attach()*/
        // 초기 프래그먼트 설정
        if (savedInstanceState == null) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, GroupListFragment())
                .commit()
        }

        binding.tab.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        replaceFragment(GroupListFragment())
                    }
                    1 -> {
                        replaceFragment(FriendListFragment())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

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
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}