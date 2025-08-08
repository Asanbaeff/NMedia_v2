package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.core.net.toUri

class PostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSinglePostBinding.inflate(
            inflater, container, false
        )
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)


        val postId = arguments?.getLong("postId")

        viewModel.data.observe(viewLifecycleOwner) { feed ->
            val post = feed.posts.find { it.id == postId } ?: return@observe

            binding.post.like.setOnClickListener {
                viewModel.likeById(post.id)
            }

            binding.post.share.setOnClickListener {
                viewModel.shareById(post.id)
            }

            binding.post.content.text = post.content
            binding.post.author.text = post.author
            binding.post.published.text = post.published
            binding.post.like.isChecked = post.likedByMe
            binding.post.like.text = "${post.likes}"
            binding.post.share.text = "${post.shareById}"


            if (post.videoUrl.isNotBlank()) {
                binding.post.video.visibility = View.VISIBLE
            } else {
                binding.post.video.visibility = View.GONE
            }

            binding.post.video.setOnClickListener {
                val url = post.videoUrl
                if (url.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    startActivity(intent)
                }
            }

            binding.post.menu.setOnClickListener {
                PopupMenu(requireContext(), it).apply {
                    menuInflater.inflate(R.menu.options_post, this.menu)

                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                viewModel.removeById(post.id)
                                findNavController().popBackStack()
                                true
                            }

                            R.id.edit -> {
                                viewModel.edit(post)
                                findNavController().navigate(
                                    R.id.action_post_to_editPostFragment32,
                                    bundleOf("textArg" to post.content)
                                )
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
        return binding.root
    }
}