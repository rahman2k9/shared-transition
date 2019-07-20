# Shared Transition between Gallery RecyclerView and ViewPager
<p align="center">
 <img src="https://github.com/rahman2k9/shared-transition/blob/master/shared.gif" width="200" height="330" />
 </p>
## Steps to accomplish
1. Create any type of gallery view you needed!
2. Create an adapter usign FragmentPagerAdapter for ViewPager as you wish
3. And now Follow givien instructions

    * Your must provide a unique transition name to each View/ImageView in RecyclerView and PagerAdapter
    * these transition names must be same on both sides.
    * Shared Transition does not required any effort with direct activity or fragment. But if you shared transition is not connected directly to other activity/fragment you must use follow these things.
    
    
### Target Activity
   * You must hold second activity to perform any transition while viewpager fragment is ready. Therefore create a SharedElementCallback 
```
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

ActivityCompat.postponeEnterTransition(this)
ActivityCompat.setEnterSharedElementCallback(this, enterElementCallback)
        
```
   * Target Activity ViewPager DetailFragment
   * When viewpager fragment is ready then resume pospondTransition
   
```

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
                            imageView.viewTreeObserver.removeOnPreDrawListener(this)   //WHEN IMAGE IS READY 
                            ActivityCompat.startPostponedEnterTransition(activity!!)   // RESUME POSTPONEDED TRANSITION
                            return true
                        }
                    })
                    return false
                }

            })
            .into(imageView)
```
   * If you are returning from activity you inform MainActivity from where you are returning as it can perform exit transition on that.
```
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
    
//onCreate
ActivityCompat.setExitSharedElementCallback(this, exitElementCallback)


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
 
 ```
 Thats it. :)

     
