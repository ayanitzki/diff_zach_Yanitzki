package com.bignerdranch.android.diff_zach_yanitzki

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class BookStorage(context: Context) {
    private val prefs = context.getSharedPreferences("books_prefs", Context.MODE_PRIVATE)

    fun saveLastBook(book: Book) {
        val json = JSONObject().apply {
            put("id", book.id)
            put("isbn", book.isbn)
            put("title", book.title)
            put("author", book.author)
            put("interestingFact", book.interestingFact)
        }
        prefs.edit().putString("last_book", json.toString()).apply()
    }

    fun getLastBook(): Book? {
        val str = prefs.getString("last_book", null) ?: return null
        return parseBook(JSONObject(str))
    }

    fun getBookList(): MutableList<Book> {
        val str = prefs.getString("book_list", null) ?: return mutableListOf()
        val array = JSONArray(str)
        val list = mutableListOf<Book>()
        for (i in 0 until array.length()) {
            list.add(parseBook(array.getJSONObject(i)))
        }
        return list
    }

    fun saveBookList(list: List<Book>) {
        val array = JSONArray()
        list.forEach { book ->
            val obj = JSONObject().apply {
                put("id", book.id)
                put("isbn", book.isbn)
                put("title", book.title)
                put("author", book.author)
                put("interestingFact", book.interestingFact)
            }
            array.put(obj)
        }
        prefs.edit().putString("book_list", array.toString()).apply()
    }

    fun addOrUpdateBook(book: Book) {
        val list = getBookList()
        val index = list.indexOfFirst { it.id == book.id }
        if (index != -1) {
            list[index] = book
        } else {
            list.add(0, book)
        }
        saveBookList(list)

        if (getLastBook()?.id == book.id) {
            saveLastBook(book)
        }
    }

    private fun parseBook(obj: JSONObject): Book {
        return Book(
            id = obj.optString("id"),
            isbn = obj.optString("isbn"),
            title = obj.optString("title"),
            author = obj.optString("author"),
            interestingFact = obj.optString("interestingFact")
        )
    }
}