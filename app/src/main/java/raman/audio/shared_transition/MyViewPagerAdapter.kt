package raman.audio.shared_transition

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup


class MyViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var items: MutableList<Fragment> = mutableListOf()
    private val views = SparseArray<View?>(items.size)

    fun setItems(items: MutableList<Fragment>) {
        this.items = items
    }

    override fun getItem(position: Int): Fragment {
        val a = items[position]
        return a
    }

    override fun getCount(): Int = items.size

    override fun getPageTitle(position: Int): CharSequence? {
        return "Title : " + position
    }

    fun getView(position: Int): Fragment = items[position]

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

}