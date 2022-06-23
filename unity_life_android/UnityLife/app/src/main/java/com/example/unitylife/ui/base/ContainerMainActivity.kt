package com.example.unitylife.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.unitylife.R
import com.example.unitylife.databinding.ContainerMainBinding
import utils.SharedPreferencesStorage
import javax.inject.Inject


class ContainerMainActivity : AppCompatActivity() {
    private var binding: ContainerMainBinding? = null

    @Inject
    lateinit var storage: SharedPreferencesStorage

    private var navHostFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContainerMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.containerMain_root)
    }

    /*private fun authorizationAction() {
        val newIntent = Intent(this, ContainerLoginActivity::class.java)
        startActivity(newIntent)
    }*/
}