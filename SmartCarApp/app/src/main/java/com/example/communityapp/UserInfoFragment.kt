package com.example.communityapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.communityapp.databinding.FragmentUserInfoBinding

class UserInfoFragment : Fragment() {

    lateinit var userInfoFragmentBinding : FragmentUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        userInfoFragmentBinding = FragmentUserInfoBinding.inflate(inflater)

        userInfoFragmentBinding.button.setOnClickListener{
            val boardMainIntent = Intent(requireContext(), BoardMainActivity::class.java)
            startActivity(boardMainIntent)
            activity?.finish()
        }

        return userInfoFragmentBinding.root
    }
}