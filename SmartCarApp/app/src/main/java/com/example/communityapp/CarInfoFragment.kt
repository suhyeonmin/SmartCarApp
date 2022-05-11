package com.example.communityapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.communityapp.databinding.FragmentCarInfoBinding

class CarInfoFragment : Fragment() {

    lateinit var carInfoFragmentBinding : FragmentCarInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        carInfoFragmentBinding = FragmentCarInfoBinding.inflate(inflater)

        return carInfoFragmentBinding.root
    }
}