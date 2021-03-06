package com.example.chatapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.utils.startHomeActivity
import com.example.chatapp.utils.startLoginActivity
import com.example.chatapp.ui.base.BaseViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, BaseViewModelFactory())
            .get(MainViewModel::class.java)

        if (viewModel.user == null) {
            this.startLoginActivity()
        } else {
            this.startHomeActivity()
        }

    }
}