package raman.audio.shared_transition

import android.graphics.Bitmap
import android.os.Build
import android.support.v4.view.ViewCompat
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.image_item.view.*
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.Nullable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.*
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import com.bumptech.glide.request.target.SimpleTarget


class ImageItem(val galleryItem: GalleryItem) : Item<ViewHolder>() {


    override fun getLayout(): Int = R.layout.image_item

    override fun bind(viewHolder: ViewHolder, position: Int) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(viewHolder.itemView.imageView, GalleryItem.transitionName(galleryItem.id))
        }

        val target = object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(bitmap: Bitmap, @Nullable transition: Transition<in Bitmap>?) {
                 viewHolder.itemView.imageView.setImageBitmap(bitmap)
                viewHolder.itemView.imageView.tag = GalleryItem.transitionName(galleryItem.id)
            }
        }


        Glide.with(viewHolder.itemView.context)
            .asBitmap()
            .load(galleryItem.imageUrl)
            .transform(RoundedCornersTransformation(20, 0))
            .into(target)


    }
}