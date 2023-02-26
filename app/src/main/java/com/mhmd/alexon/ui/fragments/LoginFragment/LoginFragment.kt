package com.mhmd.alexon.ui.fragments.LoginFragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.meeting.letsmeeting.base.util.Resource
import com.mhmd.alexon.R
import com.mhmd.alexon.databinding.FragmentLoginBinding
import com.mhmd.alexon.domain.models.UserModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONException

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val loginMvvm: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusBar()
        showAndHidePassword()
        onBtnLoginClick()
        getUserLoginResponse()
        signInWithGoogleBottom()
        faceBookLoginManager()
        onFaceBookBtnClick()
        onArrowBackClick()

    }

    private fun onArrowBackClick() {
        binding.arrowBack.arrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_introFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserLoginResponse() {
        lifecycleScope.launchWhenStarted {
            loginMvvm.loginStateFlow.collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnLogin.revertAnimation()
                        response.data?.let {
                            val sharedPreferences = getEncryptedSharedPreference().edit()
                            sharedPreferences.putString("firstName", it.username).apply()
                            sharedPreferences.putString("token", it.token).apply()
                            sharedPreferences.putString("image", it.image).apply()
                            sharedPreferences.putString("email", it.email).apply()
                            sharedPreferences.putString("id", it.id.toString()).apply()
                            Toast.makeText(
                                requireContext(),
                                "Welcome back we signed you in!",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_loginFragment_to_searchFragment)
                        }
                    }
                    is Resource.Error -> {
                        binding.btnLogin.revertAnimation()
                        response.message?.let {
                            Toast.makeText(
                                requireContext(),
                                response.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                    is Resource.EmptyData -> {
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(
                            requireContext(),
                            "user data is not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun onBtnLoginClick() {
        binding.btnLogin.setOnClickListener {
            val userName = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (userName.isEmpty()) {
                binding.edtEmail.apply {
                    requestFocus()
                    error = "user name is empty use this user kminchelle"
                }
            } else if (password.isEmpty()) {
                binding.edtPassword.apply {
                    requestFocus()
                    error = "password is empty use this password 0lelplR"
                }
            } else {
                val user = UserModel(username = userName, password = password)
                loginMvvm.login(user)
            }
        }

    }

    private fun statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.let { activity?.window?.setStatusBarColor(it.getColor(R.color.login_statusBar_color)) }
        }
    }

    private fun showAndHidePassword() {
        binding.showPassword.setOnClickListener {
            if (binding.edtPassword.transformationMethod != null) {
                binding.edtPassword.transformationMethod = null
                binding.showPassword.visibility = View.GONE
                binding.hidePassword.visibility = View.VISIBLE
            }
        }
        binding.hidePassword.setOnClickListener {
            if (binding.edtPassword.transformationMethod == null) {
                binding.edtPassword.transformationMethod = PasswordTransformationMethod()
                binding.hidePassword.visibility = View.GONE
                binding.showPassword.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getEncryptedSharedPreference(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "my_encrypted_shared_preferences",
            masterKeyAlias,
            requireContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    //Start Login with Google
    @RequiresApi(Build.VERSION_CODES.M)
    private fun signInWithGoogleBottom() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        binding.btnLoginWithGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleResults(task)
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener { it ->
            it.addOnSuccessListener {
                val sharedPreferences = getEncryptedSharedPreference().edit()
                sharedPreferences.putString("firstName", account.displayName).apply()
                sharedPreferences.putString("token", account.idToken.toString()).apply()
                sharedPreferences.putString("image", account.photoUrl.toString()).apply()
                sharedPreferences.putString("email", account.email).apply()
                sharedPreferences.putString("id", account.id).apply()
                Toast.makeText(
                    requireContext(),
                    "Welcome back we signed you in!",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(R.id.action_loginFragment_to_searchFragment)
            }
            it.addOnFailureListener {
                Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    //End Login with Google

    // start sign in with face book
    private fun faceBookLoginManager() {
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.d("testApp", "facebook sign in is canceled ")
            }

            override fun onError(error: FacebookException) {
                Log.d("testApp", error.message.toString())
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSuccess(result: LoginResult) {
                getUserInformationByFacebook(result)
                Log.d("testAPP", "FACEBOOK SIGN IN SUCCESS")
                lifecycleScope.launchWhenStarted {
                    delay(1000)
                    findNavController().navigate(R.id.action_loginFragment_to_searchFragment)
                }
            }
        })
    }

    private fun onFaceBookBtnClick() {
        binding.btnFacebookSignIn.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserInformationByFacebook(result: LoginResult?) {
        val graphRequest = GraphRequest.newMeRequest(result?.accessToken) { _, response ->
            try {
                val jsonObject = response?.jsonObject
                val userId = jsonObject?.getString("id")
                val email = jsonObject?.getString("email")
                val fullName = jsonObject?.getString("name")
                val profileUrl =
                    jsonObject?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")

                val sharedPreferences = getEncryptedSharedPreference().edit()
                sharedPreferences.putString("firstName", fullName.toString()).apply()
                sharedPreferences.putString("token", result?.accessToken?.token.toString()).apply()
                sharedPreferences.putString("image", profileUrl.toString()).apply()
                sharedPreferences.putString("email", email.toString()).apply()
                sharedPreferences.putString("id", userId.toString()).apply()

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,picture.type(large),email")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    // end sign in with face book

}