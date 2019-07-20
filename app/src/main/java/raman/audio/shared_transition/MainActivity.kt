package raman.audio.shared_transition

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.SharedElementCallback
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private var reenterState: Bundle? = null

    companion object {
        const val EXTRA_STARTING_ALBUM_POSITION = "extra_starting_item_position"
        const val EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.setExitSharedElementCallback(this, exitElementCallback)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
    }

    private val exitElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (reenterState != null) {
                val startingPosition = reenterState!!.getInt(EXTRA_STARTING_ALBUM_POSITION)
                val currentPosition = reenterState!!.getInt(EXTRA_CURRENT_ALBUM_POSITION)
                if (startingPosition != currentPosition) {
                    val newTransitionName = GalleryItem.transitionName(DataSource.items[currentPosition].id)
                    val newSharedElement = recyclerView.findViewWithTag<View>(newTransitionName)
                    if (newSharedElement != null) {
                        names.clear()
                        names.add(newTransitionName)
                        sharedElements.clear()
                        sharedElements[newTransitionName] = newSharedElement
                    }
                }
                reenterState = null
            }
        }
    }


    private fun setupRecyclerView() {
        val adapter = GroupAdapter<ViewHolder>()
        val section = Section()

        val list = DataSource.items

        list.forEach {
            section.add(ImageItem(it))
        }

        adapter.add(section)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(this)
    }

    override fun onItemClick(item: Item<ViewHolder>, view: View) {
        item as ImageItem
        val intent = Intent(this, DetailedActivity::class.java)
        val b = Bundle()
        val transitionName = GalleryItem.transitionName(item.galleryItem.id)
        b.putLong("id", item.galleryItem.id)
        b.putString("imageUrl", item.galleryItem.imageUrl)
        b.putString("transition", transitionName)

        intent.putExtras(b)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            view,
            GalleryItem.transitionName(item.galleryItem.id)
        )
        startActivity(intent, options.toBundle())
        Toast.makeText(this, "${item.galleryItem.imageUrl}:$transitionName", Toast.LENGTH_LONG).show()
    }


    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        reenterState = Bundle(data.extras)
        reenterState?.let {
            val startingPosition = it.getInt(EXTRA_STARTING_ALBUM_POSITION)
            val currentPosition = it.getInt(EXTRA_CURRENT_ALBUM_POSITION)
            if (startingPosition != currentPosition) recyclerView.scrollToPosition(currentPosition)
            ActivityCompat.postponeEnterTransition(this)

            recyclerView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                    ActivityCompat.startPostponedEnterTransition(this@MainActivity)
                    return true
                }
            })
        }
    }
}
