package com.example.communityapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.communityapp.databinding.FragmentJoinBinding

class JoinFragment : Fragment() {

    lateinit var joinFragmentBinding : FragmentJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        joinFragmentBinding = FragmentJoinBinding.inflate(inflater)
        joinFragmentBinding.joinToolbar.title = "회원가입"

        joinFragmentBinding.joinNextBtn.setOnClickListener{

            val joinId = joinFragmentBinding.joinId.text.toString()
            val joinPw = joinFragmentBinding.joinPw.text.toString()

            if(joinId == null || joinId.length == 0){
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("아이디 입력 오류")
                dialogBuilder.setMessage("아이디를 입력해주세요")
                dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                    joinFragmentBinding.joinId.requestFocus()
                }
                dialogBuilder.show()
                return@setOnClickListener
            }

            if(joinPw == null || joinPw.length == 0){
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle("비밀번호 입력 오류")
                dialogBuilder.setMessage("비밀번호를 입력해주세요")
                dialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                    joinFragmentBinding.joinPw.requestFocus()
                }
                dialogBuilder.show()
                return@setOnClickListener
            }

            val act = activity as MainActivity

            act.userId = joinId
            act.userPw = joinPw

            act.fragmentController("nick_name",true,true)
        }

        return joinFragmentBinding.root
    }
}