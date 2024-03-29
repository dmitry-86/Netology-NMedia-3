package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.view.loadCircleCrop
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.view.load

class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private val typeAd = 0
    private val typePost = 1

    interface OnInteractionListener {
        fun onImage(post: Post) {}
        fun onLike(post: Post) {}
        fun onEdit(post: Post) {}
        fun onRemove(post: Post) {}
        fun onShare(post: Post) {}
        fun onAdClick(ad: Ad) {}
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> typeAd
            is Post -> typePost
            null -> throw IllegalArgumentException("unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad -> (holder as? AdViewHolder)?.bind(it)
            }
        }
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = post.published.toString()
                content.text = post.content
                avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
                like.isChecked = post.likedByMe
                like.text = "${post.likes}"

                menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

                Glide.with(avatar)
                    .load("http://10.0.2.2:9999/avatars/${post.authorAvatar}")
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .circleCrop()
                    .timeout(10_000)
                    .into(avatar)

                when (post.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        Glide.with(attachment)
                            .load("http://10.0.2.2:9999/media/${post.attachment.url}")
                            .timeout(10_000)
                            .into(attachment)
                    }
                }

                attachment.visibility = if (post.attachment != null) View.VISIBLE else View.GONE

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)

                        menu.setGroupVisible(R.id.owned, post.ownedByMe)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                share.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                attachment.setOnClickListener {
                    onInteractionListener.onImage(post)
                }
            }
        }
    }

    class AdViewHolder(
        private val binding: CardAdBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ad: Ad) {
            binding.apply {
                image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
                image.setOnClickListener {
                    onInteractionListener.onAdClick(ad)
                }
            }
        }
    }

    class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }

}

