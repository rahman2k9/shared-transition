package raman.audio.shared_transition

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.SharedElementCallback
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_detailed.*

class DetailedActivity : AppCompatActivity() {

    private var imageUrl: String = ""
    private var transition: String = ""
    private var id: Long = 0
    private var isReturning: Boolean = false
    private var startingPosition: Int = 0
    private var currentPosition: Int = 0
    private var viewPagerAdapter: MyViewPagerAdapter? = null

    companion object {
        const val EXTRA_STARTING_ALBUM_POSITION = "extra_starting_item_position"
        const val EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position"
    }

    private val enterElementCallback: SharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (isReturning) {
                viewPagerAdapter?.getView(currentPosition).let {
                    if (it is DetailedFragment) {
                        val sharedElement = it.getCurrentView()
                        if (startingPosition != currentPosition) {
                            names.clear()
                            names.add(ViewCompat.getTransitionName(sharedElement).toString())
                            sharedElements.clear()
                            sharedElements.put(ViewCompat.getTransitionName(sharedElement).toString(), sharedElement)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        ActivityCompat.postponeEnterTransition(this)
        ActivityCompat.setEnterSharedElementCallback(this, enterElementCallback)


        imageUrl = intent.extras.getString("imageUrl", "")
        transition = intent.extras.getString("transition", "")
        startingPosition = intent.extras.getLong("id", 1).toInt() - 1

        val list: MutableList<Fragment> = mutableListOf()
        DataSource.items.forEach {
            list.add(DetailedFragment.getInstance(this@DetailedActivity, it))
        }

        viewPagerAdapter = MyViewPagerAdapter(supportFragmentManager)
        currentPosition = savedInstanceState?.getInt("current") ?: startingPosition

        viewPagerAdapter?.setItems(list)
        viewPager.adapter = viewPagerAdapter
        viewPager.currentItem = currentPosition


        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
            }
        })
    }

    override fun onDestroy() {
        this.supportFinishAfterTransition()
        super.onDestroy()

    }

    override fun onPause() {
        this.supportFinishAfterTransition()
        super.onPause()

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(EXTRA_CURRENT_ALBUM_POSITION, currentPosition)
    }

    override fun finishAfterTransition() {
        isReturning = true
        val data = Intent()
        data.putExtra(EXTRA_STARTING_ALBUM_POSITION, startingPosition)
        data.putExtra(EXTRA_CURRENT_ALBUM_POSITION, currentPosition)
        setResult(Activity.RESULT_OK, data)
        super.finishAfterTransition()
    }


}
