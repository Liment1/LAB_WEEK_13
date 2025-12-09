package com.example.lab_weekk_13

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lab_weekk_13.api.MovieService
import com.example.lab_weekk_13.database.MovieDao
import com.example.lab_weekk_13.database.MovieDatabase
import com.example.lab_weekk_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val movieService: MovieService, private val movieDatabase: MovieDatabase) {
    private val apiKey = BuildConfig.MY_API_KEY
    // LiveData that contains a list of movies
    private val movieLiveData = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = movieLiveData
    // LiveData that contains an error message
    private val errorLiveData = MutableLiveData<String>()
    val error: LiveData<String>
        get() = errorLiveData
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // Check if there are movies saved in the database
            val movieDao: MovieDao = movieDatabase.movieDao()
            val savedMovies = movieDao.getMovies()
            // If there are no movies saved in the database,
            // fetch the list of popular movies from the API
            if (savedMovies.isEmpty()) {
                val movies = movieService.getPopularMovies(apiKey).results
                // save the list of popular movies to the database
                movieDao.addMovies(movies)
                // emit the list of popular movies from the API
                emit(movies)
            } else {
                // If there are movies saved in the database,
                // emit the list of saved movies from the database
                emit(savedMovies)
            }
        }.flowOn(Dispatchers.IO)
    }
}