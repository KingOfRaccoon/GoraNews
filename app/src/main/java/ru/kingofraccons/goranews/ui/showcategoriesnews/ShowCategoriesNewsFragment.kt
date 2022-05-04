package ru.kingofraccons.goranews.ui.showcategoriesnews

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.kingofraccons.goranews.R
import ru.kingofraccons.goranews.databinding.FragmentShowCategoriesNewsBinding

// fragment for categories news and news
class ShowCategoriesNewsFragment : Fragment(R.layout.fragment_show_categories_news) {
    private val viewModel: ShowCategoriesNewsViewModel by viewModel()
    private val adapter = CategoriesAdapter()

    // set title and can add menu to appbar
    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        viewModel.connectivityLiveData.observe(viewLifecycleOwner) {
            requireActivity().title = if (!it.isConnected) "Соединение..." else "Новости"
        }
    }

    // observe data from viewModel and set adapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentShowCategoriesNewsBinding.bind(view)
        binding.recyclerCategoriesNews.adapter = adapter

        viewModel.articlesLiveData.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                adapter.setData(it)
            }
        }
    }

    // create menu for search
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val search = menu.findItem(R.id.action_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("data", newText.toString())
                if (newText != null)
                    adapter.filter.filter(newText)
                return false
            }
        })
    }
}