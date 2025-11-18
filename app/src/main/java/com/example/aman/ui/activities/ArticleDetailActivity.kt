package com.example.aman.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aman.R
import com.example.aman.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    companion object {
        const val EXTRA_ARTICLE_TITLE = "article_title"
        const val EXTRA_ARTICLE_IMAGE = "article_image"
        const val EXTRA_ARTICLE_CONTENT = "article_content"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadArticleData()
        setupButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun loadArticleData() {
        val title = intent.getStringExtra(EXTRA_ARTICLE_TITLE)
        val imageRes = intent.getIntExtra(EXTRA_ARTICLE_IMAGE, 0)
        val content = intent.getStringExtra(EXTRA_ARTICLE_CONTENT)

        binding.tvArticleTitle.text = title
        if (imageRes != 0) {
            binding.ivArticleImage.setImageResource(imageRes)
        }
        binding.tvArticleContent.text = content
    }

    private fun setupButtons() {
        binding.fabShare.setOnClickListener {
            shareArticle()
        }
    }

    private fun shareArticle() {
        val title = intent.getStringExtra(EXTRA_ARTICLE_TITLE)
        val content = intent.getStringExtra(EXTRA_ARTICLE_CONTENT)

        val shareText = "$title\n\n$content"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_article)))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}