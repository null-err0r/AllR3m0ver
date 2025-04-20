package all.remover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import android.provider.MediaStore
import android.content.ContentResolver
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

class MediaFragment : Fragment() {
    enum class MediaType { IMAGE, VIDEO, AUDIO }

    private lateinit var recyclerView: RecyclerView
    private lateinit var deleteAllButton: Button
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var mediaType: MediaType

    companion object {
        private const val ARG_MEDIA_TYPE = "media_type"
        fun newInstance(type: MediaType) = MediaFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_MEDIA_TYPE, type.name)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaType = MediaType.valueOf(arguments?.getString(ARG_MEDIA_TYPE) ?: "IMAGE")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_media, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        deleteAllButton = view.findViewById(R.id.delete_all_button)

        setupRecyclerView()
        loadMedia()

        deleteAllButton.setOnClickListener { showDeleteConfirmation() }

        return view
    }

    private fun setupRecyclerView() {
        mediaAdapter = MediaAdapter(mediaType)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mediaAdapter
        }
    }

    private fun loadMedia() {
        val contentResolver = requireContext().contentResolver
        val uri = when (mediaType) {
            MediaType.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            MediaType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            MediaType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED
        )

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        )

        cursor?.use {
            val mediaList = mutableListOf<MediaItem>()
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val name = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val contentUri = Uri.withAppendedPath(uri, id.toString())
                mediaList.add(MediaItem(id, name, contentUri))
            }
            mediaAdapter.submitList(mediaList)
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setMessage("آیا مطمئن هستید که می‌خواهید همه را حذف کنید؟")
            .setPositiveButton("بله") { _, _ -> deleteAllMedia() }
            .setNegativeButton("خیر", null)
            .show()
    }

    private fun deleteAllMedia() {
        val contentResolver = requireContext().contentResolver
        mediaAdapter.currentList.forEach { item ->
            contentResolver.delete(item.uri, null, null)
        }
        mediaAdapter.submitList(emptyList())
    }
}

data class MediaItem(val id: Long, val name: String, val uri: Uri)