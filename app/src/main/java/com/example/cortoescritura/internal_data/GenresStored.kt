package com.example.cortoescritura.internal_data

import com.example.cortoescritura.R
import com.example.cortoescritura.models.Genre


object GenresStored {
    private val GENRES: ArrayList<Genre> = object : ArrayList<Genre>() {
        init {
            add(Genre("Cuento de Hadas", "", R.drawable.fairy))
            add(Genre("Drama", "", R.drawable.drama))
            add(Genre("Erotica", "", R.drawable.erotic));
            add(Genre("Fabula", "", R.drawable.fable))
            add(Genre("Fantasia", "", R.drawable.fantasy))
            add(Genre("Ficcion", "", R.drawable.fiction))
            add(Genre("Horror/CreepyPasta", "", R.drawable.horror))
            add(Genre("Humor", "", R.drawable.humor))
            add(Genre("Infantil", "", R.drawable.childish))
            add(Genre("Leyenda", "", R.drawable.legend))
            add(Genre("Leyenda Urbana", "", R.drawable.urban_legend))
            add(Genre("Misterio", "", R.drawable.mistery))
            add(Genre("Poesia", "", R.drawable.poetry))
            add(Genre("Real/Personal", "", R.drawable.real))
            add(Genre("Romance", "", R.drawable.romance))
        }
    }

    fun returnGenres(): ArrayList<Genre> {
        return GENRES
    }

    fun returnNameGenres(): ArrayList<String> {
        val genres = ArrayList<String>()
        genres.add("Selección genero:")
        for (i in GENRES) {
            genres.add(i.name)
        }
        return genres
    }
}