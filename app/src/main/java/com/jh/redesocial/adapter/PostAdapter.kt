package com.jh.redesocial.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jh.redesocial.databinding.PostItemBinding
import com.jh.redesocial.model.Post

class PostAdapter(private val listaPosts: Array<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // O ViewHolder segura as referências dos componentes do post_item.xml
    class PostViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // Infla o layout post_item usando View Binding
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = listaPosts[position]

        // Define a descrição no TextView [cite: 19]
        holder.binding.txtDescricao.text = post.descricao

        // Define a imagem no ImageView [cite: 25]
        if (post.foto != null) {
            holder.binding.imgPost.setImageBitmap(post.foto)
        }
    }

    override fun getItemCount(): Int = listaPosts.size
}