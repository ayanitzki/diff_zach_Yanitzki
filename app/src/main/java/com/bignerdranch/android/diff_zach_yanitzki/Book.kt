package com.bignerdranch.android.diff_zach_yanitzki

import java.io.Serializable

data class Book(
    val id: String,
    var isbn: String,
    var title: String,
    var author: String,
    var interestingFact: String
) : Serializable