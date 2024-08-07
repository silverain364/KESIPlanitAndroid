package com.example.kesi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kesi.databinding.FragmentAddFriendsBinding

class AddFriendsFragment : Fragment() {
    lateinit var binding: FragmentAddFriendsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFriendsBinding.inflate(layoutInflater)
        return binding.root
    }

}