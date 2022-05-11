package com.example.communityapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.communityapp.databinding.ActivityViewPagerBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerActivity : AppCompatActivity() {

    lateinit var viewPagerActivityBinding: ActivityViewPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewPagerActivityBinding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(viewPagerActivityBinding.root)

        // 1. 페이지 데이터를 로드
        val list = listOf(ShoppingFragment(), CarInfoFragment(), UserInfoFragment())
        // 2. 어댑터를 생성
        val pagerAdapter = FragmentPagerAdapter(list, this)
        // 3. 어댑터와 뷰페이저 연결
        viewPagerActivityBinding.viewPager.adapter = pagerAdapter

        // 4. 탭 메뉴의 개수만큼 제목을 목록으로 생성
        val titles = listOf("용품", "과속정보", "사용자")
        // 5. 탭레이아웃과 뷰페이저 연결
        TabLayoutMediator(viewPagerActivityBinding.tabLayout, viewPagerActivityBinding.viewPager){ tab: TabLayout.Tab, i: Int ->
            tab.text = titles[i]
        }.attach()
    }
}

class FragmentPagerAdapter(val fragmentList: List<Fragment>, fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return fragmentList.size
    }
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}