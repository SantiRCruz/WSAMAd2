package com.example.wsamad2.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.navigation.fragment.findNavController
import com.example.wsamad2.R
import com.example.wsamad2.core.Constants
import com.example.wsamad2.data.get
import com.example.wsamad2.data.models.History
import com.example.wsamad2.databinding.FragmentHomeBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val historyList = mutableListOf<History>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        obtainHistory()
        obtainCases()
        actualDate()
        clicks()

    }

    private fun clicks() {
        binding.imgQr.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_qrFragment) }
    }

    private fun obtainHistory() {
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.USER, Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", "")
        val name = sharedPreferences.getString("name", "")
        binding.txtName.text = name
        binding.txtNameData.text = name
        Constants.okHttp.newCall(get("symptoms_history?user_id=$id")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")) {
                    val data = json.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        historyList.add(
                            History(
                                SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(
                                    item.getString(
                                        "date"
                                    )
                                ), item.getInt("probability_infection")
                            )
                        )
                    }
                    val finalData = historyList[data.length() - 1]
                    requireActivity().runOnUiThread {
                        binding.llWithData  .visibility = View.VISIBLE
                        binding.llData1.visibility = View.VISIBLE
                        binding.txtDayMonth.text = SimpleDateFormat("dd/mm").format(finalData.date)
                        binding.txtYearHour.text = SimpleDateFormat("/yyyy KK:mm:ssa").format(finalData.date)
                        if (finalData.probability_infection > 50) {
                            binding.llBgData.backgroundTintList = getColorStateList(requireContext(),R.color.red2)
                            binding.txtTitleData.text = "CALL TO DOCTOR"
                            binding.txtMessageData.text = "You may be infected with a virus"
                        } else {
                            binding.txtMessageData.text = "* Wear mask. Keep 2m distance. Wash hands."
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        binding.llNoData.visibility = View.VISIBLE
                        binding.llNoData1.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun obtainCases() {
        Constants.okHttp.newCall(get("cases")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val data = json.getInt("data")
                requireActivity().runOnUiThread {
                    if (data > 0) {
                        binding.txtNumCases.text = "$data cases"
                        binding.llNumCases.backgroundTintList =
                            getColorStateList(requireContext(), R.color.red2)
                    } else {
                        binding.txtNumCases.text = "No cases"
                    }
                }
            }
        })
    }

    private fun actualDate() {
        binding.txtActualDate.text = SimpleDateFormat("MMM dd, yyyy").format(Date())
    }

}