package ru.netology.nmedia.activity



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.EmptyFeed
import ru.netology.nmedia.model.ErrorFeed
import ru.netology.nmedia.model.LoadingFeed
import ru.netology.nmedia.model.PostsFeed
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })



        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, { state ->
 
            binding.progress.isVisible = false
            binding.errorGroup.isVisible = false
            binding.emptyText.isVisible = false


            when (state) {
                is PostsFeed -> adapter.submitList(state.posts)
                is LoadingFeed -> binding.progress.isVisible = true
                is ErrorFeed -> binding.errorGroup.isVisible = true
                is EmptyFeed -> binding.emptyText.isVisible = true
            }
        })

        viewModel.internetErrorMessage.observe(viewLifecycleOwner, { onError ->
            if (onError == true) {
                displayInternetError()
                binding.errorGroup.isVisible = true
            }
 
        })




        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }


 
    fun displayInternetError() {
        Toast.makeText(
            requireContext(),
            "Connection error. Try again", Toast.LENGTH_SHORT
        ) .show()
    }

}
