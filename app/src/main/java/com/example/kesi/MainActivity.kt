package com.example.kesi

import android.annotation.SuppressLint
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    //뷰 페이저 어댑터
    class FragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        val fragments: List<Fragment>
        init {
            fragments = listOf(HomeFragment(), ListFragment(), NotificationFragment(), SettingsFragment())
        }

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
    @SuppressLint("UseCompatLoadingForDrawables")
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
        val adapter = FragmentPagerAdapter(this)
        binding.viewPager.adapter = adapter
        //탭과 뷰 페이저 연동
        TabLayoutMediator(binding.tab, binding.viewPager) {
            tab, position ->
            when (position) {
                0 -> {
                    tab.text = "홈"
                    tab.icon = getDrawable(R.drawable.ic_home_white)
                    tab.icon!!.setColorFilter(resources.getColor(R.color.nav_bottom_home), PorterDuff.Mode.SRC_IN)
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

        binding.tab.addOnTabSelectedListener(
           object: TabLayout.OnTabSelectedListener{
               //선택된 탭은 각각의 색으로 변경
               override fun onTabSelected(tab: TabLayout.Tab?) {
                   val color = when(tab!!.position){
                       0 -> {
                           resources.getColor(R.color.nav_bottom_home)
                       }
                       1 -> {
                           resources.getColor(R.color.nav_bottom_list)
                       }
                       2 -> {
                           resources.getColor(R.color.nav_bottom_alarm)
                       }
                       3 -> {
                           resources.getColor(R.color.nav_bottom_setting)
                       }
                       else -> {
                           resources.getColor(R.color.white)
                       }
                   }
                   tab!!.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN);
               }

               //선택된 탭 외의 다른 탭 아이콘은 흰 색으로 변경
               override fun onTabUnselected(tab: TabLayout.Tab?) {
                   tab!!.icon!!.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN);
               }

               override fun onTabReselected(tab: TabLayout.Tab?) {

               }

           }
        )
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