package all.remover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class MediaAdapter(private val mediaType: MediaFragment.MediaType) :
    ListAdapter<MediaItem, MediaAdapter.ViewHolder>(MediaDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val previewImage: ImageView = itemView.findViewById(R.id.preview_image)
        val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.nameTextView.text = item.name

        when (mediaType) {
            MediaFragment.MediaType.IMAGE -> {
                Glide.with(holder.itemView.context)
                    .load(item.uri)
                    .transform(CenterCrop(), RoundedCorners(16))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_error)
                    .into(holder.previewImage)
            }
            MediaFragment.MediaType.VIDEO -> {
                Glide.with(holder.itemView.context)
                    .load(item.uri)
                    .thumbnail(0.25f)
                    .transform(CenterCrop(), RoundedCorners(16))
                    .placeholder(R.drawable.ic_video_placeholder)
                    .error(R.drawable.ic_error)
                    .into(holder.previewImage)
            }
            MediaFragment.MediaType.AUDIO -> {
                holder.previewImage.setImageResource(R.drawable.ic_audio_placeholder)
            }
        }
    }
}

class MediaDiffCallback : DiffUtil.ItemCallback<MediaItem>() {
    override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean =
        oldItem == newItem
}