package com.example.submissionintermediate.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionintermediate.R
import com.example.submissionintermediate.auth.ViewModelFactory
import com.example.submissionintermediate.data.response.Story
import com.example.submissionintermediate.databinding.ActivityMainBinding
import com.example.submissionintermediate.maps.MapsActivity
import com.example.submissionintermediate.ui.adapter.LoadingStateAdapter
import com.example.submissionintermediate.ui.login.LoginActivity
import com.example.submissionintermediate.ui.newstory.NewStoryActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: com.example.submissionintermediate.ui.adapter.StoryAdapter

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        binding.fabAddstory.setOnClickListener { moveToAddStory() }

        binding.mapsButton.setOnClickListener { moveToMaps() }

        binding.logoutButton.setOnClickListener { clearSession() }

        viewModel.getSession().observe(this) { user ->
            Log.wtf("user session", "User Token ${user.token}")
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }
        setupData()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = com.example.submissionintermediate.ui.adapter.StoryAdapter()
        binding.rvStory.let {
            it.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            it.setHasFixedSize(true)
        }
    }


    private fun setupData() {
        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotBlank()) {
                processGetAllStories(user.token)
            }
        }
    }

    private fun clearSession() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sign_out)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.oke) { _, _ ->
                viewModel.deleteLogin()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun processGetAllStories(token: String) {
        viewModel.getStories(token).observe(this){
            setListStory(it)
        }
    }

    private fun setListStory(pagingData: PagingData<Story>) {
        lifecycleScope.launch {
            adapter.submitData(pagingData)
        }
    }

    private fun moveToAddStory() {
        startActivity(Intent(this, NewStoryActivity::class.java))
    }

    private fun moveToMaps(){
        startActivity(Intent(this, MapsActivity::class.java))
    }
}