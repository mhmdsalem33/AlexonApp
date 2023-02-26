package com.mhmd.alexon.ui.fragments.SearchFragment


import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.material.button.MaterialButton
import com.meeting.letsmeeting.base.util.Resource
import com.mhmd.alexon.R
import com.mhmd.alexon.base.Constants
import com.mhmd.alexon.databinding.FragmentSearchBinding
import com.mhmd.alexon.helper.StaggeredGridItemDecoration
import com.mhmd.alexon.ui.adapters.MenuDrawerAdapter
import com.mhmd.alexon.ui.adapters.SearchAdapter
import com.mhmd.alexon.ui.fragments.ProductDetailsFragment.ProductDetailsFragment
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private val searchMvvm: SearchViewModel by viewModels()
    private var slidingRootNav: SlidingRootNav? = null
    private val constants by lazy { Constants() }
    private lateinit var menuDrawerAdapter: MenuDrawerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchAdapter = SearchAdapter()
        menuDrawerAdapter = MenuDrawerAdapter()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusBar()
        onBackPressed()
        initSearchRecyclerView()
        search()
        getSearchProducts()
        setupFilterProductsMenu()
        onItemSearchClick()
        setupSlidingRootNav(savedInstanceState)
        accessToSlidingRoot()
        onDrawerMenuClick()
        setProfileImageInView()
        onClickToOpenDrawer()

    }

    private fun onClickToOpenDrawer() {
        binding.drawerMenu.setOnClickListener {
            slidingRootNav?.openMenu()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setProfileImageInView() {
        val image = getEncryptedSharedPreference().getString("image", "")
        Glide.with(requireActivity()).load(image).into(binding.profileImg)
    }

    private fun onDrawerMenuClick() {
        menuDrawerAdapter.onMenuItemClick = {
            when (it.id) {
                1 -> findNavController().navigate(R.id.searchFragment)
                2 -> findNavController().navigate(R.id.myProfileFragment)
                3 -> findNavController().navigate(R.id.deliveryAddessFragment)
                4 -> findNavController().navigate(R.id.paymentMethodsFragment)
                5 -> findNavController().navigate(R.id.contactUsFragment)
                6 -> findNavController().navigate(R.id.settingsFragment)
                7 -> findNavController().navigate(R.id.helpFragment)
                else -> {
                    Log.d("testApp", "Something went wrong with menu id when navigate")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun accessToSlidingRoot() {

        val firsName = getEncryptedSharedPreference().getString("firstName", "").toString()
        val email = getEncryptedSharedPreference().getString("email", "").toString()
        val image = getEncryptedSharedPreference().getString("image", "").toString()

        // access to  slidingRootNav Views
        val drawerLayoutRoot = slidingRootNav?.layout
        val userName = drawerLayoutRoot?.findViewById<TextView>(R.id.user_name)
        val userEmail = drawerLayoutRoot?.findViewById<TextView>(R.id.user_email)
        val profileImage = drawerLayoutRoot?.findViewById<CircleImageView>(R.id.user_image)
        val logout = drawerLayoutRoot?.findViewById<MaterialButton>(R.id.drawer_log_out)
        userName?.text = firsName
        userEmail?.text = email

        if (profileImage != null) {
            Glide.with(requireActivity()).load(image).into(profileImage)
        }

        //  Logout
        logout?.setOnClickListener {
            getEncryptedSharedPreference().edit().clear().apply()
            slidingRootNav?.closeMenu()
            findNavController().navigate(R.id.introFragment)
        }

        // Access  to drawer menu recyclerView
        val menuRecyclerView = drawerLayoutRoot?.findViewById<RecyclerView>(R.id.menu_RecyclerView)
        menuRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = menuDrawerAdapter
        }

        menuDrawerAdapter.differ.submitList(constants.drawerMenuItems)
    }

    private fun setupSlidingRootNav(savedInstanceState: Bundle?) {
        slidingRootNav = SlidingRootNavBuilder(requireActivity())
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(true)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.layout_drawer)
            .inject()
    }


    private fun onItemSearchClick() {
        searchAdapter.onItemClick = { product ->
            val fragment = ProductDetailsFragment()
            val bundle = Bundle()
            bundle.putParcelable("product", product)
            fragment.arguments = bundle
            findNavController().navigate(
                R.id.action_searchFragment_to_productDetailsFragment,
                bundle
            )
        }
    }


    private fun statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.let { activity?.window?.setStatusBarColor(it.getColor(R.color.login_statusBar_color)) }
        }
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initSearchRecyclerView() {
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing)
        binding.searchRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = searchAdapter
            addItemDecoration(StaggeredGridItemDecoration(spacing))
        }
    }

    private fun search() {
        val edtSearch = binding.edtSearch.text.toString()
        if (edtSearch.isEmpty()) {
            searchMvvm.searchProduct("j")
        }
        var searchJob: Job? = null
        binding.edtSearch.addTextChangedListener { searchQuery ->
            if (searchQuery.toString().isNotEmpty()) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(1000)
                    searchMvvm.searchProduct(searchQuery.toString())
                    getSearchProducts()
                }
            }
        }
    }

    private fun getSearchProducts() {
        lifecycleScope.launchWhenStarted {
            searchMvvm.getSearchProductStateFlow.collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        Log.d("testApp", "Search Product is loading")
                        binding.progressBar.visibility = View.VISIBLE
                        binding.searchRecyclerView.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.searchRecyclerView.visibility = View.VISIBLE
                        response.data?.let {
                            searchAdapter.differ.submitList(it)
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let {
                            Log.d("testApp", it)
                        }
                    }
                    is Resource.EmptyData -> {
                        Log.d("testApp", "No products found")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupFilterProductsMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.filter)
        popupMenu.inflate(R.menu.menu)
        binding.filter.setOnClickListener {
            popupMenu.show()
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.phone -> {
                    searchMvvm.searchProduct("phone")
                    true
                }
                R.id.book -> {
                    searchMvvm.searchProduct("book")
                    true
                }
                R.id.laptop -> {
                    searchMvvm.searchProduct("laptop")
                    true
                }
                else -> false
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


}