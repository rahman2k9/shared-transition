package raman.audio.shared_transition

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.xyz.*

class DetailedFragment : Fragment() {

    private var imageUrl: Uri? = null
    private var transitionName: String? = null
    var activity: Activity? = null

    companion object {
        private var instance: DetailedFragment? = null

        @Synchronized
        fun getInstance(activity: Activity, galleryItem: GalleryItem): DetailedFragment {
            instance = DetailedFragment()
            val args = Bundle()
            args.putString("imageUrl", galleryItem.imageUrl)
            args.putString("transition", GalleryItem.transitionName(galleryItem.id))
            instance?.arguments = args
            instance?.activity = activity
            return instance!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = Uri.parse(arguments?.getString("imageUrl"))
        transitionName = arguments?.getString("transition")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.xyz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(imageView, transitionName)
        }


        Glide.with(context!!)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            imageView.viewTreeObserver.removeOnPreDrawListener(this)
                            ActivityCompat.startPostponedEnterTransition(activity!!)
                            return true
                        }
                    })
                    return false
                }

            })
            .into(imageView)
    }

    fun getCurrentView(): View {
        return imageView
    }
}