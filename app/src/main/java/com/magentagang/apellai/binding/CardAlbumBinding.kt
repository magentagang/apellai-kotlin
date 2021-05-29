package com.magentagang.apellai.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.magentagang.apellai.model.Album

@BindingAdapter("albumName")
fun TextView.setAlbumName(item: Album?) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("albumArtist")
fun TextView.setAlbumArtist(item: Album?) {
    item?.let {
        text = item.artist
    }
}