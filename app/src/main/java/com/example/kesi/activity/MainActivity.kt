package com.example.kesi.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kesi.fragment.HomeFragment
import com.example.kesi.fragment.NotificationFragment
import com.example.kesi.R
import com.example.kesi.api.UserApi
import com.example.kesi.fragment.AccountFragment
import com.example.kesi.databinding.ActivityMainBinding
import com.example.kesi.fragment.ListFragment
import com.example.kesi.model.UserInfoDto
import com.example.kesi.setting.RetrofitSetting
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    private val retrofit = RetrofitSetting.getRetrofit();
    private val userApi = retrofit.create(UserApi::class.java)

    //뷰 페이저 어댑터
    class FragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
        val fragments: List<Fragment>
        init {
            fragments = listOf(HomeFragment(), ListFragment(), NotificationFragment(), AccountFragment())
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
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toggle.syncState()

        userApi.getByUid().enqueue(object : Callback<UserInfoDto> {
            override fun onResponse(p0: Call<UserInfoDto>, response: Response<UserInfoDto>) {
                Toast.makeText(this@MainActivity,response.code().toString(),Toast.LENGTH_SHORT).show()
                if (response.code() == 500) {
                    val intent: Intent = Intent(this@MainActivity, ProfileSettingsActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(p0: Call<UserInfoDto>, p1: Throwable) {
                Toast.makeText(this@MainActivity,"통신실패",Toast.LENGTH_SHORT).show()
            }

        })



        // 뷰 페이저 스와이프 막기
        binding.viewPager.run {
            isUserInputEnabled = false
        }
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
                    tab.text = "계정"
                    tab.icon = getDrawable(R.drawable.ic_account_white)
                }
            }
        }.attach()

        binding.tab.addOnTabSelectedListener(
           object: TabLayout.OnTabSelectedListener{
               override fun onTabSelected(tab: TabLayout.Tab?) {
                   //선택된 탭은 각각의 색으로 변경
                   val color = when(tab!!.position){
                       0 -> {
                           ContextCompat.getColor(applicationContext, R.color.nav_bottom_home)
                       }
                       1 -> {
                           ContextCompat.getColor(applicationContext, R.color.nav_bottom_list)
                       }
                       2 -> {
                           ContextCompat.getColor(applicationContext, R.color.nav_bottom_alarm)
                       }
                       3 -> {
                           ContextCompat.getColor(applicationContext, R.color.nav_bottom_account)
                       }
                       else -> {
                           ContextCompat.getColor(applicationContext, R.color.white)
                       }
                   }
                   tab.icon?.setTint(color)

                   //각각의 탭을 클릭했을 경우 타이틀 변경
                   when(tab?.position) {
                       0 -> binding.tvTitle.text = "KESI"
                       1 -> binding.tvTitle.text = "목록"
                       2 -> binding.tvTitle.text = "알림"
                       3 -> binding.tvTitle.text = "내 계정"
                   }
               }

               override fun onTabUnselected(tab: TabLayout.Tab?) {

               }

               override fun onTabReselected(tab: TabLayout.Tab?) {
                    onTabSelected(tab)
               }
           })
        // 첫 번째 탭을 선택된 상태로 만들어 초기 색상을 적용
        val firstTab = binding.tab.getTabAt(0)
        firstTab?.select()  // 첫 번째 탭을 선택된 상태로 만듦
        firstTab?.icon?.setTint(ContextCompat.getColor(applicationContext, R.color.nav_bottom_home))
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

        //SearchView가 열렸을 때 tvTitle을 숨기기
        searchView.setOnSearchClickListener {
            binding.tvTitle.visibility = View.GONE
        }

        //SearchView가 닫혔을 때 tvTitle을 다시 보이게 하기
        searchView.setOnCloseListener {
            binding.tvTitle.visibility = View.VISIBLE
            false
        }
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