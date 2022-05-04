package ru.kingofraccons.goranews.ui.showcategoriesnews

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import ru.kingofraccons.goranews.R
import ru.kingofraccons.goranews.databinding.ItemNewBinding
import ru.kingofraccons.goranews.model.Article

// adapter for show article
class ArticlesAdapter : RecyclerView.Adapter<ArticlesAdapter.ArticlesViewHolder>() {
    private var articles = listOf<Article>()

    // update data with help DiffUtils
    fun updateData(newArticles: List<Article>) {
        val diff = object : DiffUtil.Callback() {
            override fun getOldListSize() = articles.size
            override fun getNewListSize() = newArticles.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                articles[oldItemPosition].title == newArticles[newItemPosition].title

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                articles[oldItemPosition] == newArticles[newItemPosition]
        }
        DiffUtil.calculateDiff(diff, true).dispatchUpdatesTo(this)
        articles = newArticles
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        return ArticlesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_new, parent, false)
        ).apply { // setup ratio 2:1 (height : width)
            val width = (parent.measuredWidth / 2.5).toInt()
            val view: View = this.itemView
            view.layoutParams.width = width
            view.layoutParams.height = width * 2
            setIsRecyclable(false)
        }
    }
    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size

    // viewHolder for binding article
    inner class ArticlesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemNewBinding.bind(view)

        // bind item recyclerview
        fun bind(article: Article) {
            binding.textTitleArticle.text = article.title
            binding.imageArticle.loadImage(article.urlToImage.toString(), binding.imageError)
            binding.root.setOnClickListener {
                val openPage = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(itemView.context, openPage, null)
            }
        }

        // load images in imageview async with error image
        private fun ImageView.loadImage(uri: String, view: View) {
            this.post {
                val myOptions = RequestOptions()
                    .override(this.width, this.height)
                    .centerCrop()

                Glide.with(this.context)
                    .load(uri)
                    .apply(myOptions)
                    .listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            view.visibility = View.VISIBLE
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            view.visibility = View.GONE
                            return false
                        }
                    })
                    .into(this)
            }
        }
    }
}