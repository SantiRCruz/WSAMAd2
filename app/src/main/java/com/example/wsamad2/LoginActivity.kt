package com.example.wsamad2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.wsamad2.core.Constants
import com.example.wsamad2.core.networkInfo
import com.example.wsamad2.data.post
import com.example.wsamad2.data.signIn
import com.example.wsamad2.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        writing()
        clicks()

    }

    private fun writing() {
        emailWriting()
        passwordWriting()
    }

    private fun passwordWriting() {
        binding.edtPassword.addTextChangedListener {
            val regex = Pattern.compile("^(1234)")
            if (!regex.matcher(it!!).matches()) {
                binding.edtPassword.error = "This filed have a wrong format"
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.gray)
            } else {
                binding.edtPassword.error = null
                binding.btnSignIn.isEnabled = true
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.white)
            }
        }
    }

    private fun emailWriting() {
        binding.edtEmail.addTextChangedListener {
            val regex = Pattern.compile("^([a-zA-Z]{1,}@wsa[.]com)")
            if (!regex.matcher(it!!).matches()) {
                binding.edtEmail.error = "This filed have a wrong format"
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.gray)
            } else {
                binding.edtEmail.error = null
                binding.btnSignIn.isEnabled = true
                binding.btnSignIn.backgroundTintList = getColorStateList(R.color.white)
            }
        }
    }

    private fun clicks() {
        binding.btnSignIn.setOnClickListener { validate() }
    }

    private fun validate() {
        val results = arrayOf(validateEmail(), validatePassword())
        if (false in results)
            return

        if (!networkInfo(applicationContext)) {
            alertMessage("No internet Connection")
            return
        }
        sendInfoActions(false)
        sendSignIn()
    }

    private fun sendInfoActions(b:Boolean) {
        if (b){
            binding.progress.visibility = View.GONE
            binding.btnSignIn.visibility = View.VISIBLE
        }else{
            binding.progress.visibility = View.VISIBLE
            binding.btnSignIn.visibility = View.GONE
        }
    }

    private fun sendSignIn() {
        Constants.okHttp.newCall(
            post(
                "signin/",
                signIn(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
            )
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                runOnUiThread {
                    sendInfoActions(true)
                    alertMessage("Server Error!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")) {
                    val data = json.getJSONObject("data")
                    val sharedPreferences = getSharedPreferences(Constants.USER,Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()){
                        putString("id",data.getString("id"))
                        putString("name",data.getString("name"))
                        putString("token",data.getString("token"))
                        apply()
                    }
                    val i  = Intent(this@LoginActivity,HomeActivity::class.java)
                    startActivity(i)

                } else {
                    runOnUiThread {
                        sendInfoActions(true)
                        alertMessage("Wrong Credentials")
                    }
                }
            }
        })
    }

    private fun validatePassword(): Boolean {
        return if (binding.edtPassword.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else {
            true
        }
    }

    private fun validateEmail(): Boolean {
        val regex = Pattern.compile("^([a-zA-Z]{1,}@wsa[.]com)")
        return if (binding.edtEmail.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else if (!regex.matcher(binding.edtEmail.text.toString()).matches()) {
            alertMessage("The Email have a wrong format")
            false
        } else {
            true
        }
    }

    private fun alertMessage(s: String) {
        binding.txtAlert.text = s
        binding.btnSignIn.animate().translationY(1000f).alpha(0f).setDuration(200).withEndAction {
            binding.llAlert.animate().alpha(1f).setDuration(200).withEndAction {
                binding.llAlert.animate().alpha(1f).setDuration(800).withEndAction {
                    binding.llAlert.animate().alpha(0f).setDuration(200)
                    binding.btnSignIn.animate().translationY(0f).alpha(1f).setDuration(200)

                }
            }
        }

    }
}