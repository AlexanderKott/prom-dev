package ru.netology.nmedia.view

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.constraintlayout.widget.Placeholder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.ViewTarget
import ru.netology.nmedia.R

fun ImageView.load(url: String, placeholder: Boolean = false , vararg transforms: BitmapTransformation = emptyArray())
:ViewTarget<ImageView, Drawable>
{
    val vt = Glide.with(this)
        .load(url)
        .timeout(10_000)
        .transform(*transforms)

    if (placeholder) vt.placeholder(R.drawable.ic_baseline_cloud_download_24)

    return vt.into(this)
}



fun ImageView.loadCircleCrop(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    load(url, false ,CircleCrop(), *transforms)
