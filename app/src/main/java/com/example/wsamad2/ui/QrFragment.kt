package com.example.wsamad2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wsamad2.R
import com.example.wsamad2.databinding.FragmentQrBinding


class QrFragment : Fragment(R.layout.fragment_qr) {
    private lateinit var binding: FragmentQrBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentQrBinding.bind(view)


    }


}