package ru.netology.nmedia.activity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import java.io.File

class PostFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // выбор из галереи
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                imageUri = it
                Glide.with(this)
                    .load(it)
                    .into(imageView)
            }
        }

        // фото с камеры
        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && imageUri != null) {
                imageView.setImageURI(imageUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        imageView = view.findViewById(R.id.imageView)



        imageView.setOnClickListener {
            imageUri?.let {
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        FullscreenImageFragment.newInstance(it.toString())
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }

        view.findViewById<Button>(R.id.btnGallery).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        view.findViewById<Button>(R.id.btnCamera).setOnClickListener {
            val photoFile = File(requireContext().cacheDir, "${System.currentTimeMillis()}.jpg")
            imageUri = FileProvider.getUriForFile(
                requireContext(), "${requireContext().packageName}.fileprovider", photoFile
            )
            takePhotoLauncher.launch(imageUri)
        }

        return view
    }
}
