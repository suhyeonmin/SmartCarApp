package com.example.communityapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.communityapp.databinding.FragmentNickNameBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread

class NickNameFragment : Fragment() {

    lateinit var nickNameFragmentBinding : FragmentNickNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        nickNameFragmentBinding = FragmentNickNameBinding.inflate(inflater)
        nickNameFragmentBinding.nicknameToolbar.title = "닉네임 입력"

        nickNameFragmentBinding.nicknameJoinBtn.setOnClickListener{

            val nickNameNickName = nickNameFragmentBinding.nicknameNickname.text.toString()

            if(nickNameNickName == null || nickNameNickName.length == 0){
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("닉네임 입력 오류")
                dialogBuilder.setMessage("닉네임을 입력해주세요")
                dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                    nickNameFragmentBinding.nicknameNickname.requestFocus()
                }
                dialogBuilder.show()
                return@setOnClickListener
            }

            val act = activity as MainActivity
            act.userNickName = nickNameNickName

//            Log.d("test","${act.userId}")
//            Log.d("test","${act.userPw}")
//            Log.d("test","${act.userNickName}")

            thread {
                val client = OkHttpClient()

                val site = "http://${ServerInfo.SERVER_IP}:8080/CommunityServer/join_user.jsp"

                //서버로 보낼 데이터를 셋팅
                val builder1 = FormBody.Builder()
                builder1.add("user_id", act.userId)
                builder1.add("user_pw", act.userPw)
                builder1.add("user_nick_name", act.userNickName)
                val formbody = builder1.build()

                val request = Request.Builder().url(site).post(formbody).build()

                val response = client.newCall(request).execute()

                if(response.isSuccessful == true){
                    activity?.runOnUiThread{
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle("가입 완료")
                        dialogBuilder.setMessage("가입이 완료되었습니다")
                        dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                            val mainIntent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(mainIntent)
                            activity?.finish()
                        }
                        dialogBuilder.show()
                    }
                }else{
                    activity?.runOnUiThread{
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle("가입 오류")
                        dialogBuilder.setMessage("가입 오류가 발생하였습니다")
                        dialogBuilder.setPositiveButton("확인",null)
                        dialogBuilder.show()
                    }
                }
            }

//            val mainIntent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(mainIntent)
//            activity?.finish()
        }

        return nickNameFragmentBinding.root
    }
}