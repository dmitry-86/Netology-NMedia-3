package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.FeedAdapter
import ru.netology.nmedia.adapter.PagingLoadStateAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    @Inject
    lateinit var repository: PostRepository

    @Inject
    lateinit var auth: AppAuth
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(object : FeedAdapter.OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onImage(post: Post) {
                val bundle = Bundle().apply { putString("url", post.attachment?.url) }
                findNavController().navigate(R.id.action_feedFragment_to_imageFragment, bundle)
            }

            override fun onLike(post: Post) {
                if(authViewModel.authenticated) {
                    viewModel.likePost(post)
               }else {
                    createDialog()
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                if (authViewModel.authenticated) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }
            }
        })

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(object : PagingLoadStateAdapter.OnInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            }),
            footer = PagingLoadStateAdapter(object : PagingLoadStateAdapter.OnInteractionListener {
                override fun onRetry() {
                    adapter.retry()
                }
            }),
        )

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                println("DO SOMETHING")
            }
        }).attachToRecyclerView(binding.list)

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swiperefresh.isRefreshing =
                    state.refresh is LoadState.Loading
//                    state.prepend is LoadState.Loading ||
//                    state.append is LoadState.Loading
            }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        authViewModel.data.observe(viewLifecycleOwner) { adapter.refresh() }


        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        })

        binding.newPosts.setOnClickListener {
            binding.newPosts.visibility = View.GONE
            viewModel.loadNewPosts()
        }

        binding.fab.setOnClickListener {
            if(authViewModel.authenticated) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }else{
                createDialog()
            }
        }

        return binding.root
    }

    private fun createDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Would you like to sign in?")
        builder.setNeutralButton("Yes"){dialogInterface, i ->
            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
        }
        builder.setNegativeButton("No"){dialog, i ->
            findNavController().navigate(R.id.feedFragment)
        }
        builder.show()
    }


}
