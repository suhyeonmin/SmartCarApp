package com.example.communityapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.communityapp.databinding.ActivityBoardMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.jar.Manifest
import kotlin.concurrent.thread

class BoardMainActivity : AppCompatActivity() {

    lateinit var boardMainActivityBinding : ActivityBoardMainBinding
    lateinit var currentFragment: Fragment

    val boardIndexList = ArrayList<Int>()
    val boardNameList = ArrayList<String>()
    var selectedBoardType = 0
    var readContentIdx = 0
    var nowPage = 1

    val permissionList = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_MEDIA_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        boardMainActivityBinding = ActivityBoardMainBinding.inflate(layoutInflater)
        setContentView(boardMainActivityBinding.root)

        requestPermissions(permissionList, 0)

        boardIndexList.add(0)
        boardNameList.add("전체 게시판")

        thread {
            val client = OkHttpClient()

            val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/get_board_list.jsp"

            val request = Request.Builder().url(site).get().build()
            val response = client.newCall(request).execute()

            if(response.isSuccessful == true){
                val resultText = response.body?.string()!!.trim()

                val root = JSONArray(resultText)

                for(i in 0 until root.length()){
                    val obj = root.getJSONObject(i)

                    val boardIdx = obj.getInt("board_idx")
                    val boardName = obj.getString("board_name")

                    boardIndexList.add(boardIdx)
                    boardNameList.add(boardName)
                }
            }
        }
        //
        fragmentController("board_main",false,true)
        //
    }

    fun fragmentController(name:String, add:Boolean, animate:Boolean){
        when(name){
            "board_main" -> {
                currentFragment = BoardMainFragment()
            }
            "board_read" -> {
                currentFragment = BoardReadFragment()
            }
            "board_write" -> {
                currentFragment = BoardWriteFragment()
            }
            "board_modify" -> {
                currentFragment = BoardModifyFragment()
            }
        }

        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.board_main_container, currentFragment)

        if(add == true){
            trans.addToBackStack(name)
        }

        if(animate == true){
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

        trans.commit()
    }

    fun fragmentRemoveBackStack(name:String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}