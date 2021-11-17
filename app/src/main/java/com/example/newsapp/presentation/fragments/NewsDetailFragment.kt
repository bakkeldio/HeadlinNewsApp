package com.example.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsDetailBinding


class NewsDetailFragment : Fragment() {

    private var _binding: FragmentNewsDetailBinding? = null


    private val binding: FragmentNewsDetailBinding get() = _binding!!

    private var imageUrl: String? = null
    private var title: String? = null
    private var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = requireArguments().getString("imageUrl")
        title = requireArguments().getString("title")
        description = requireArguments().getString("description")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarView.setupWithNavController(findNavController())
        Glide.with(requireContext())
            .load(imageUrl)
            .error(R.drawable.news_default_image)
            .placeholder(R.drawable.news_default_image)
            .into(binding.imageView)

        binding.title.text = title
        binding.content.text = description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}