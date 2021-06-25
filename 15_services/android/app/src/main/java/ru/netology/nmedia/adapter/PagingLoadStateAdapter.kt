package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.LoadstatecardBinding

class PagingLoadStateAdapter(private val click : (()-> Unit)) : LoadStateAdapter<PagingLoadStateAdapter.PagingLoadStateAdapterViewHolder>() {

    override fun onBindViewHolder(holder: PagingLoadStateAdapterViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PagingLoadStateAdapterViewHolder {
        val binding = LoadstatecardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagingLoadStateAdapterViewHolder(binding, click)
    }

    class PagingLoadStateAdapterViewHolder(private val binding: LoadstatecardBinding, private val click : (()-> Unit))
        : RecyclerView.ViewHolder(binding.root){

    fun bind(state : LoadState){
        with (binding){
            retry.isVisible =  state is LoadState.Error
            errorLabel.isVisible =  state is LoadState.Error
            progressBar.isVisible =  state is LoadState.Loading

            retry.setOnClickListener {
                click()
            }
        }
    }

}
}