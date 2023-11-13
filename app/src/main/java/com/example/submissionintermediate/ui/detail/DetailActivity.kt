package com.example.submissionintermediate.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.submissionintermediate.R
import com.example.submissionintermediate.data.response.Story
import com.example.submissionintermediate.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_STORY, Story::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY)

        }
        if (story != null) {
            setData(story)
        }
    }

    private fun setData(story: Story) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

}