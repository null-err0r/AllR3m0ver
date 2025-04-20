package all.remover


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MediaFragment.newInstance(MediaFragment.MediaType.IMAGE)
            1 -> MediaFragment.newInstance(MediaFragment.MediaType.VIDEO)
            2 -> MediaFragment.newInstance(MediaFragment.MediaType.AUDIO)
            else -> throw IllegalStateException("Invalid position")
        }
    }
}