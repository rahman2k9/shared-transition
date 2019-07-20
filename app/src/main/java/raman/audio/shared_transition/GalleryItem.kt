package raman.audio.shared_transition

data class GalleryItem(
    val id: Long,
    val imageUrl: String
) {

    companion object {
        fun transitionName(id: Long) = "item$id"
    }
}