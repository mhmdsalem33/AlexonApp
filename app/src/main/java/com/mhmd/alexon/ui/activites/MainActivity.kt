package com.mhmd.alexon.ui.activites

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.viewpager2.widget.ViewPager2
import com.mhmd.alexon.R

import com.mhmd.alexon.databinding.ActivityMainBinding
import com.mhmd.alexon.helper.ZoomOutPageTransformer
import com.mhmd.alexon.ui.adapters.AppIntroViewPager2Adapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val token = getEncryptedSharedPreference().getString("token", "").toString()
        if (token.isNotEmpty()) {
            val navController = findNavController(R.id.fragmentContainerView)
            navController.navigate(R.id.searchFragment)
        } else {
            val navController = findNavController(R.id.fragmentContainerView)
            navController.navigate(R.id.introFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getEncryptedSharedPreference(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "my_encrypted_shared_preferences",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}