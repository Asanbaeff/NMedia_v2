package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.databinding.FragmentFullScreenImageBinding

class FullScreenImageFragment : Fragment() {

    private var url: String? = null
    private var _binding: FragmentFullScreenImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString("url")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Центрировать изображение и заполнить экран
        binding.imageView.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
        binding.imageView.adjustViewBounds = true

        url?.let {
            Glide.with(this)
                .load(it)
                .into(binding.imageView)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}