package com.voiddev.movieboxtv.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
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

        title = "MovieBox TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        adapter = rowsAdapter

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            val movie = item as? MovieItem
            if (movie != null && movie.effectiveId.isNotEmpty()) {
                val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra("SUBJECT_ID", movie.effectiveId)
                }
                startActivity(intent)
            }
        }

        loadHomeData()
    }

    private fun loadHomeData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getHome()
                val categories = response.data?.list ?: emptyList()

                withContext(Dispatchers.Main) {
                    rowsAdapter.clear()
                    val cardPresenter = MovieCardPresenter()

                    categories.forEachIndexed { index, category ->
                        if (category.items.isNotEmpty()) {
                            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                            category.items.forEach { listRowAdapter.add(it) }
                            val header = HeaderItem(index.toLong(), category.title ?: "Section")
                            rowsAdapter.add(ListRow(header, listRowAdapter))
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "API Load Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
