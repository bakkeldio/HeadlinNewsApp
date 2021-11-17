package com.example.newsapp.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.base.setDivider
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.model.CategoryEnum
import com.example.newsapp.databinding.FragmentNewsListBinding
import com.example.newsapp.presentation.NewsViewModel
import com.example.newsapp.presentation.adapter.NewsAdapter
import com.example.newsapp.presentation.model.ResourceState
import com.example.newsapp.presentation.popup.PopupDialog
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NewsListFragment : Fragment(), NewsAdapter.Listener {

    private val viewModel: NewsViewModel by viewModels()

    private var _binding: FragmentNewsListBinding? = null


    private val binding get() = _binding!!

    private var popupDialog: PopupDialog? = null

    private var lastSearchQuery: String? = null

    private var lastChosenCategory = CategoryEnum.GENERAL


    private val newsAdapter by lazy {
        NewsAdapter(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.searchNews()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearch()
        binding.toolbarView.setupWithNavController(findNavController())
        binding.recyclerview.apply {
            adapter = newsAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        viewModel.searchNews(lastSearchQuery, lastChosenCategory, isPaging = true)
                    }
                }
            })
            setDivider(R.drawable.horizontal_divider)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect { state ->
                binding.loader.isVisible = state is ResourceState.Loading
                binding.emptyDataMessage.isVisible = state is ResourceState.Empty
                when (state) {
                    is ResourceState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG)
                            .show()
                    }
                    is ResourceState.Success -> {
                        submitDataToAdapter(state.data.second.articles, state.data.first)
                    }
                    is ResourceState.Empty -> {
                        newsAdapter.submitList(emptyList())
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun submitDataToAdapter(data:  List<Article>, isPaging: Boolean) {
        val existingList = mutableListOf<Article>()
        if (isPaging){
            existingList.addAll(newsAdapter.currentList)
        }
        existingList.addAll(data)
        newsAdapter.submitList(existingList)
    }

    private fun initSearch() {
        binding.toolbarView.apply {
            (menu.findItem(R.id.action_search).actionView as? SearchView)?.let {
                it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    private var searchJob: Job? = null

                    override fun onQueryTextSubmit(query: String): Boolean {
                        hideSoftInputKeyBoard(it)
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        if (!it.hasFocus()) return true
                        searchJob?.cancel()
                        searchJob = CoroutineScope(Dispatchers.Main).launch {
                            lastSearchQuery = if (newText.isEmpty()) null else newText
                            delay(500)
                            viewModel.searchNews(newText, isUpdate = true)
                        }
                        return true
                    }
                })
                it.setOnCloseListener {
                    viewModel.searchNews(isUpdate = true)
                    true
                }
            }
            setOnMenuItemClickListener {
                if (it?.itemId == R.id.action_filter) {
                    showPopupDialog(findViewById(R.id.action_filter))
                }
                true
            }
        }
    }

    private fun showPopupDialog(view: View) {
        val data = listOf(
            CategoryEnum.GENERAL.value,
            CategoryEnum.BUSINESS.value,
            CategoryEnum.ENTERTAINMENT.value,
            CategoryEnum.HEALTH.value,
            CategoryEnum.SCIENCE.value,
            CategoryEnum.SPORTS.value,
            CategoryEnum.TECHNOLOGY.value
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)

        popupDialog = PopupDialog.Builder(requireContext())
            .anchorView(anchorView = view)
            .adapter(adapter = adapter)
            .backgroundDrawable(background = R.drawable.bg_list_popup_view)
            .onItemClickListener { _, _, position, _ ->
                lastChosenCategory = CategoryEnum.getEnumByValue(data[position])
                when (data[position]) {
                    CategoryEnum.GENERAL.value -> {
                        viewModel.searchNews(lastSearchQuery, CategoryEnum.GENERAL, true)
                    }
                    CategoryEnum.TECHNOLOGY.value -> {
                        viewModel.searchNews(lastSearchQuery, CategoryEnum.TECHNOLOGY, true)
                    }
                    CategoryEnum.SPORTS.value -> {
                        viewModel.searchNews(lastSearchQuery, CategoryEnum.SPORTS, true)
                    }
                    CategoryEnum.SCIENCE.value -> {
                        viewModel.searchNews(lastSearchQuery, CategoryEnum.SCIENCE, true)
                    }
                    CategoryEnum.HEALTH.value -> {
                        viewModel.searchNews(lastSearchQuery, CategoryEnum.HEALTH, true)
                    }
                    CategoryEnum.ENTERTAINMENT.value -> {
                        viewModel.searchNews(lastSearchQuery, CategoryEnum.ENTERTAINMENT, true)
                    }
                    CategoryEnum.BUSINESS.value -> {
                        viewModel.searchNews(lastSearchQuery, CategoryEnum.BUSINESS, true)
                    }
                }
                popupDialog?.dismiss()
            }.verticalOffset(
                verticalOffset = requireContext().resources.getDimensionPixelOffset(
                    R.dimen.dp10
                )
            )
            .rightToRightOfAnchor(status = true)
            .build()
        popupDialog?.show()

    }

    private fun hideSoftInputKeyBoard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNewsClick(item: Article) {
        findNavController().navigate(
            R.id.newsDetailFragment,
            bundleOf(
                "imageUrl" to item.urlToImage,
                "title" to item.title,
                "description" to item.description
            )
        )
    }

}