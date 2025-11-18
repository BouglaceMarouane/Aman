package com.example.aman.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aman.R
import com.example.aman.databinding.ItemOnboardingBinding

class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private val pages = listOf(
        OnboardingPage(
            R.raw.onboarding1,
            R.string.onboarding_title_1,
            R.string.onboarding_desc_1
        ),
        OnboardingPage(
            R.raw.onboarding2,
            R.string.onboarding_title_2,
            R.string.onboarding_desc_2
        ),
        OnboardingPage(
            R.raw.onboarding3,
            R.string.onboarding_title_3,
            R.string.onboarding_desc_3
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount() = pages.size

    class OnboardingViewHolder(
        private val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: OnboardingPage) {
            // Set Lottie animation
            binding.lottieAnimation.setAnimation(page.animationRes)
            binding.lottieAnimation.playAnimation()

            // Set text
            binding.tvTitle.setText(page.titleRes)
            binding.tvDescription.setText(page.descriptionRes)
        }
    }

    data class OnboardingPage(
        val animationRes: Int,  // Changed from imageRes to animationRes
        val titleRes: Int,
        val descriptionRes: Int
    )
}