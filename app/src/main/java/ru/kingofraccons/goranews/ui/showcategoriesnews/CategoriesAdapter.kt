package ru.kingofraccons.goranews.ui.showcategoriesnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.kingofraccons.goranews.R
import ru.kingofraccons.goranews.databinding.ItemCategoryBinding
import ru.kingofraccons.goranews.model.Article
import ru.kingofraccons.goranews.model.ListArticles
// adapter for show category
class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(),
    Filterable {
    var categories = mapOf<String, ListArticles?>()
    var allCategories = mapOf<String, ListArticles?>()

    // set all categories
    fun setData(newCategories: Map<String, ListArticles?>) {
        allCategories = newCategories
        updateData(newCategories)
    }

    // update categories
    fun updateData(newCategories: Map<String, ListArticles?>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = categories.size
            override fun getNewListSize() = newCategories.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                categories.toList()[oldItemPosition].first ==
                        newCategories.toList()[newItemPosition].first

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                categories.toList()[oldItemPosition].second ==
                        newCategories.toList()[newItemPosition].second
        }
        DiffUtil.calculateDiff(diff, true).dispatchUpdatesTo(this)
        categories = newCategories
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(categories.toList()[position])
    }

    override fun getItemCount() = categories.size

    // viewHolder for bind category item
    inner class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemCategoryBinding.bind(view)

        fun bind(pair: Pair<String, ListArticles?>) {
            binding.textNameCategory.text = pair.first
            val adapter = ArticlesAdapter()
            adapter.updateData(pair.second?.articles ?: listOf())
            binding.recyclerNews.adapter = adapter
        }
    }

    // filter object
    override fun getFilter() = object : Filter() {
        // filter articles for title or desc
        override fun performFiltering(p0: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = allCategories.toList().associate {
                    it.first to it.second?.copy(
                        articles = filterList(
                            it.second?.articles,
                            p0.toString()
                        )
                    )
                }
            }
        }

        // publish results on adapter
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            updateData((p1?.values ?: mapOf<String, ListArticles?>()) as Map<String, ListArticles?>)
        }

        // filter on title or desc list articles
        fun filterList(articles: List<Article>?, search: String): List<Article> {
            val newArticles = mutableListOf<Article>()
            articles?.forEach {
                if (it.title.contains(search, true) ||
                    (it.description ?: "").contains(search, true)
                )
                    newArticles.add(it)
            }
            return newArticles
        }
    }
}