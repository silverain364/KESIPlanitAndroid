package com.example.kesi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kesi.databinding.FragmentListBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding

    //뷰 페이저 어댑터
    class FragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        val fragments: List<Fragment>
        init {
            fragments = listOf(GroupListFragment(), FriendListFragment())
        }

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //뷰 페이저에 어댑터 적용
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
        }.attach()
    }
}