package com.bignerdranch.android.diff_zach_yanitzki

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bignerdranch.android.diff_zach_yanitzki.databinding.FragmentBookEditBinding

class BookEditFragment : Fragment() {

    private var _binding: FragmentBookEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentBook: Book

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookArg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_BOOK, Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_BOOK) as? Book
        }

        if (bookArg == null) {
            parentFragmentManager.popBackStack()
            return
        }
        currentBook = bookArg

        if (savedInstanceState == null) {
            binding.etEditIsbn.setText(currentBook.isbn)
            binding.etEditTitle.setText(currentBook.title)
            binding.etEditAuthor.setText(currentBook.author)
            binding.etEditFact.setText(currentBook.interestingFact)
        }

        binding.btnSaveChanges.setOnClickListener {
            val newIsbn = binding.etEditIsbn.text.toString().trim()
            val newTitle = binding.etEditTitle.text.toString().trim()
            val newAuthor = binding.etEditAuthor.text.toString().trim()
            val newFact = binding.etEditFact.text.toString().trim()

            if (newTitle.isEmpty() || newIsbn.isEmpty()) {
                Toast.makeText(requireContext(), "Название и ISBN обязательны", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentBook.isbn = newIsbn
            currentBook.title = newTitle
            currentBook.author = newAuthor
            currentBook.interestingFact = newFact

            BookStorage(requireContext()).addOrUpdateBook(currentBook)
            Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BOOK = "arg_book"

        fun newInstance(book: Book): BookEditFragment {
            return BookEditFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BOOK, book)
                }
            }
        }
    }
}