package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

class FullscreenImageFragment : Fragment() {

    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String) = FullscreenImageFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_IMAGE_URL, imageUrl)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false)

        val imageView: ImageView = view.findViewById(R.id.fullscreenImageView)
        val url = arguments?.getString(ARG_IMAGE_URL)

        // Загрузка через Glide/Picasso
        Glide.with(this)
            .load(url)
            .into(imageView)

        return view
    }
}
