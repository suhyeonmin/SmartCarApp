package com.example.communityapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.communityapp.databinding.ActivityMainBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var mainActivityBinding : ActivityMainBinding
    lateinit var currentFragment : Fragment

    var userId = ""
    var userPw = ""
    var userNickName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SystemClock.sleep(1000)
        setTheme(R.style.Theme_CommunityApp)

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        val pref = getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val login_user_idx = pref.getInt("login_user_idx", -1)
        val login_auto_login = pref.getInt("login_auto_login", -1)

        if(login_auto_login == 1){
            thread{
                val client = OkHttpClient()
                val builder1 = FormBody.Builder()
                builder1.add("login_user_idx", "$login_user_idx")
                val formBody = builder1.build()

                val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/check_auto_login.jsp"

                val request = Request.Builder().url(site).post(formBody).build()
                val response = client.newCall(request).execute()
                Log.d("test","1")

                if(response.isSuccessful == true){
                    val result_text = response.body?.string()!!.trim()
                    val chk = Integer.parseInt(result_text)
                    if(chk == 1) {
                        //
                        val viewPagerIntent = Intent(this, ViewPagerActivity::class.java)
                        startActivity(viewPagerIntent)
                        finish()
                        //
                    } else {
                        fragmentController("login",false,false)
                    }
                }
            }
        } else{
            fragmentController("login",false,false)
        }
    }

    fun fragmentController(name:String, add:Boolean, animate:Boolean){

        when(name){
            "login" -> {
                currentFragment = LoginFragment()
            }
            "join" -> {
                currentFragment = JoinFragment()
            }
            "nick_name" -> {
                currentFragment = NickNameFragment()
            }
        }

        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.main_container,currentFragment)

        if(add == true){
            trans.addToBackStack(name)
        }

        if(animate == true){
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        trans.commit()
    }
}