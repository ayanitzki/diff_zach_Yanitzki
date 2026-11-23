package com.bignerdranch.android.diff_zach_yanitzki

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.diff_zach_yanitzki.databinding.ItemBookBinding

class BookAdapter(
    private var books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.binding.apply {
            tvItemTitle.text = book.title
            tvItemAuthor.text = "Автор: ${book.author}"
            tvItemIsbn.text = "ISBN: ${book.isbn}"
            tvItemFact.text = "Интересный факт: ${book.interestingFact}"
            root.setOnClickListener { onBookClick(book) }
        }
    }

    override fun getItemCount(): Int = books.size

    fun updateData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}