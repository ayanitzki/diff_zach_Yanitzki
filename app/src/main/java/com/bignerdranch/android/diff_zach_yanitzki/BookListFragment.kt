package com.bignerdranch.android.diff_zach_yanitzki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.diff_zach_yanitzki.databinding.FragmentBookListBinding
import kotlinx.coroutines.launch

class BookListFragment : Fragment() {

    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: BookStorage
    private lateinit var adapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = BookStorage(requireContext())

        adapter = BookAdapter(emptyList()) { selectedBook ->
            val editFragment = BookEditFragment.newInstance(selectedBook)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = adapter

        refreshUI()

        binding.btnSearch.setOnClickListener {
            val isbn = binding.etIsbn.text.toString()
            if (isbn.isBlank()) {
                Toast.makeText(requireContext(), "Введите номер ISBN", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchBook(isbn)
        }
    }

    private fun searchBook(isbn: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSearch.isEnabled = false

        OpenLibraryApi.fetchBookByIsbn(
            context = requireContext(),
            rawIsbn = isbn,
            onSuccess = { book ->
                if (_binding == null) return@fetchBookByIsbn
                binding.progressBar.visibility = View.GONE
                binding.btnSearch.isEnabled = true

                if (book != null) {
                    storage.saveLastBook(book)
                    storage.addOrUpdateBook(book)
                    refreshUI()
                    binding.etIsbn.text.clear()
                    Toast.makeText(requireContext(), "Книга найдена и сохранена!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Книга с таким ISBN не найдена", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { errorMsg ->
                if (_binding == null) return@fetchBookByIsbn
                binding.progressBar.visibility = View.GONE
                binding.btnSearch.isEnabled = true
                Toast.makeText(requireContext(), "Ошибка сети: $errorMsg", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun refreshUI() {
        val last = storage.getLastBook()
        binding.tvLastBook.text = if (last != null) {
            "Последний запрос:\n«${last.title}» — ${last.author} (ISBN: ${last.isbn})"
        } else {
            "Последняя запрошенная книга: нет данных"
        }
        adapter.updateData(storage.getBookList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}