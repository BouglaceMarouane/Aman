package com.example.aman.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aman.R
import com.example.aman.databinding.FragmentAdviceBinding
import com.example.aman.ui.activities.ArticleDetailActivity
import com.example.aman.ui.adapters.ArticleAdapter

class AdviceFragment : Fragment() {

    private var _binding: FragmentAdviceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadArticles()
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter { article ->
            openArticleDetail(article)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AdviceFragment.adapter
        }
    }

    private fun loadArticles() {
        val articles = listOf(
            Article(
                id = 1,
                title = getString(R.string.article_1_title),
                summary = getString(R.string.article_1_summary),
                imageRes = R.drawable.article_1,
                content = getString(R.string.article_1_content)
            ),
            Article(
                id = 2,
                title = getString(R.string.article_2_title),
                summary = getString(R.string.article_2_summary),
                imageRes = R.drawable.article_2,
                content = getString(R.string.article_2_content)
            ),
            Article(
                id = 3,
                title = getString(R.string.article_3_title),
                summary = getString(R.string.article_3_summary),
                imageRes = R.drawable.article_3,
                content = getString(R.string.article_3_content)
            )
        )

        adapter.submitList(articles)
    }

    private fun openArticleDetail(article: Article) {
        val intent = Intent(requireContext(), ArticleDetailActivity::class.java).apply {
            putExtra(ArticleDetailActivity.EXTRA_ARTICLE_TITLE, article.title)
            putExtra(ArticleDetailActivity.EXTRA_ARTICLE_IMAGE, article.imageRes)
            putExtra(ArticleDetailActivity.EXTRA_ARTICLE_CONTENT, article.content)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Article(
    val id: Int,
    val title: String,
    val summary: String,
    val imageRes: Int,
    val content: String
)