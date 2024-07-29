package com.example.kesi

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kesi.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    //뷰 페이저 어댑터
    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        val fragments: List<Fragment>
        init {
            fragments = listOf(HomeFragment(), ListFragment(), NotificationFragment(), SettingsFragment())
        }

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        //ActionBarDrawerToggle 버튼 적용
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_opened, R.string.drawer_closed)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
        //뷰 페이저에 어댑터 적용
        val adapter = MyFragmentPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tab, binding.viewPager) {
            tab, position ->
            when (position) {
                0 -> {
                    tab.text = "홈"
                    tab.icon = getDrawable(R.drawable.ic_home_white)
                }
                1 -> {
                    tab.text = "목록"
                    tab.icon = getDrawable(R.drawable.ic_list_white)
                }
                2 -> {
                    tab.text = "알림"
                    tab.icon = getDrawable(R.drawable.ic_notification_white)
                }
                3 -> {
                    tab.text = "설정"
                    tab.icon = getDrawable(R.drawable.ic_settings_white)
                }
            }
        }.attach()
    }

    //액티비티에 정적인 메뉴를 구성할 때 사용하는 함수
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        //MenuItem 객체를 얻고 그 안에 포함된 ActionView 객체 획득
        val menuItem = menu.findItem(R.id.menuSearch)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                //검색어 변경 이벤트
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                //키보드의 검색 버튼을 클릭한 순간의 이벤트
                Log.d("ksh","search text : $query")
                return true
            }
        })
        return true
    }

    //메뉴를 사용자가 선택했을 때의 이벤트 처리를 하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //이벤트가 토글 버튼에서 발생하면
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}