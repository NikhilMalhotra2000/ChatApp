package com.example.chatapp.ui.base
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.data.LoginDataSource
import com.example.chatapp.data.LoginRepository
import com.example.chatapp.data.firebase.FirebaseSource
import com.example.chatapp.ui.MainViewModel
import com.example.chatapp.ui.login.AuthViewModel

class BaseViewModelFactory : ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(loginRepository = LoginRepository(dataSource = LoginDataSource(firebaseSource = FirebaseSource()))) as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource(firebaseSource = FirebaseSource())
                )
            ) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}