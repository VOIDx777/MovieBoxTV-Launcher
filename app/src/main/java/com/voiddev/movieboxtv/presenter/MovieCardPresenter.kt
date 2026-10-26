package com.voiddev.movieboxtv.presenter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import coil.load
import coil.size.Scale
import com.voiddev.movieboxtv.model.MovieItem

class MovieCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val card = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(300, 170) // 16:9 Banner ratio
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val movie = item as? MovieItem ?: return
        val card = viewHolder.view as ImageCardView

        card.titleText = movie.title
        card.contentText = "⭐ ${movie.score ?: "N/A"}  •  ${movie.releaseTime ?: ""}"

        val imgUrl = movie.cover ?: movie.poster
        card.mainImageView.load(imgUrl) {
            crossfade(200)
            scale(Scale.FIT)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val card = viewHolder.view as ImageCardView
        card.mainImage = null
    }
}
