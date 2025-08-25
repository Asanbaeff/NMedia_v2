package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.view.loadCircleCrop
import java.text.SimpleDateFormat
import java.util.*

interface OnInteractionListener {
    fun onLike(item: PostWithAuthor) {}
    fun onEdit(item: PostWithAuthor) {}
    fun onRemove(item: PostWithAuthor) {}
    fun onShare(item: PostWithAuthor) {}
}


data class PostWithAuthor(
    val post: Post,
    val author: Author
)

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<PostWithAuthor, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PostWithAuthor) {
        val post = item.post
        val author = item.author

        binding.apply {
            this.author.text = author.name
            published.text = formatDate(post.published)
            content.text = post.content
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${author.avatar}")
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(item)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(item)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(item)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(item)
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<PostWithAuthor>() {
    override fun areItemsTheSame(oldItem: PostWithAuthor, newItem: PostWithAuthor): Boolean {
        return oldItem.post.id == newItem.post.id
    }

    override fun areContentsTheSame(oldItem: PostWithAuthor, newItem: PostWithAuthor): Boolean {
        return oldItem == newItem
    }
}
