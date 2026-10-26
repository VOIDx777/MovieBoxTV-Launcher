package com.voiddev.movieboxtv.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.voiddev.movieboxtv.api.RetrofitClient
import com.voiddev.movieboxtv.model.MovieItem
import com.voiddev.movieboxtv.presenter.MovieCardPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : BrowseSupportFragment() {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = "MOVIEBOX TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = resources.getColor(android.R.color.black, null)

        adapter = rowsAdapter
        setupEvents()
        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val categories = RetrofitClient.api.getHome()
                val presenter = MovieCardPresenter()

                withContext(Dispatchers.Main) {
                    var idCounter = 0L
                    categories.forEach { category ->
                        val validItems = category.items.filter { 
                            it.title != "Unknown" && (!it.poster.isNullOrEmpty() || !it.cover.isNullOrEmpty()) 
                        }
                        if (validItems.isNotEmpty()) {
                            val rowAdapter = ArrayObjectAdapter(presenter)
                            validItems.forEach { rowAdapter.add(it) }

                            val header = HeaderItem(idCounter++, category.title)
                            rowsAdapter.add(ListRow(header, rowAdapter))
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "API Load Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupEvents() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            val movie = item as? MovieItem ?: return@OnItemViewClickedListener
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("SUBJECT_ID", movie.subjectId)
                putExtra("TITLE", movie.title)
            }
            startActivity(intent)
        }
    }
}
