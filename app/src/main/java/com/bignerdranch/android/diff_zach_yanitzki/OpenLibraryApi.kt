package com.bignerdranch.android.diff_zach_yanitzki

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.UUID

object OpenLibraryApi {
    private var requestQueue: RequestQueue? = null

    private fun getQueue(context: Context): RequestQueue {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.applicationContext)
        }
        return requestQueue!!
    }

    fun fetchBookByIsbn(
        context: Context,
        rawIsbn: String,
        onSuccess: (Book?) -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanIsbn = rawIsbn.trim().replace("-", "")
        if (cleanIsbn.isEmpty()) {
            onSuccess(null)
            return
        }

        val bibKey = "ISBN:$cleanIsbn"
        val url = "https://openlibrary.org"

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val rootJson = JSONObject(response)
                    if (!rootJson.has(bibKey)) {
                        onSuccess(null)
                        return@StringRequest
                    }

                    val bookJson = rootJson.getJSONObject(bibKey)
                    val title = bookJson.optString("title", "Без названия")

                    val authorsArray = bookJson.optJSONArray("authors")
                    val author = if (authorsArray != null && authorsArray.length() > 0) {
                        authorsArray.getJSONObject(0).optString("name", "Неизвестный автор")
                    } else {
                        "Неизвестный автор"
                    }

                    val excerpts = bookJson.optJSONArray("excerpts")
                    val subjects = bookJson.optJSONArray("subjects")
                    val pages = bookJson.optInt("number_of_pages", 0)
                    val publishDate = bookJson.optString("publish_date", "")

                    val fact = when {
                        excerpts != null && excerpts.length() > 0 ->
                            "Цитата: «${excerpts.getJSONObject(0).optString("text")}»"
                        subjects != null && subjects.length() > 0 ->
                            "Жанр/тема: ${subjects.getJSONObject(0).optString("name")}. Издано: $publishDate"
                        pages > 0 ->
                            "В этом издании ровно $pages стр. (год выпуска: $publishDate)"
                        else ->
                            "Книга зарегистрирована в базе OpenLibrary под номером $cleanIsbn."
                    }

                    val book = Book(
                        id = UUID.randomUUID().toString(),
                        isbn = cleanIsbn,
                        title = title,
                        author = author,
                        interestingFact = fact
                    )
                    onSuccess(book)
                } catch (e: Exception) {
                    onError("Ошибка разбора данных: ${e.message}")
                }
            },
            { error ->
                onError(error.message ?: "Ошибка подключения к серверу")
            }
        )

        getQueue(context).add(stringRequest)
    }
}