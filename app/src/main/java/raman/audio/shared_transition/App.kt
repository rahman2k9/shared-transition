package raman.audio.shared_transition

import android.app.Application
import com.bumptech.glide.request.target.ViewTarget

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        ViewTarget.setTagId(R.id.glide_request);
    }
}