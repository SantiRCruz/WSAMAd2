package com.example.wsamad2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wsamad2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        animate()

    }

    private fun animate() {
        binding.imageView.animate().alpha(0f).translationY(-3000f).scaleX(0f).scaleY(0f).setDuration(0).withEndAction {
            binding.imageView2.animate().alpha(0f).translationY(-100f).scaleX(0f).scaleY(0f).setDuration(0).withEndAction {
                binding.imageView.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).setDuration(800).withEndAction {
                    binding.imageView2.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).setDuration(400).withEndAction {
                     val i = Intent(this@MainActivity,LoginActivity::class.java)
                     startActivity(i)
                        finish()
                    }
                }
            }
        }
    }
}