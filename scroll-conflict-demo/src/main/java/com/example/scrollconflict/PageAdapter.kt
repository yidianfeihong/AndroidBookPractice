package com.example.scrollconflict

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * ViewPager2 adapter for displaying demo pages in the opposite direction conflict scene.
 * 
 * This adapter creates a series of pages to demonstrate horizontal paging within
 * a vertically scrollable parent container.
 * 
 * @param fragmentActivity The host FragmentActivity
 * @param pageCount The number of pages to display (default: 5)
 */
class PageAdapter(
    fragmentActivity: FragmentActivity,
    private val pageCount: Int = 5
) : FragmentStateAdapter(fragmentActivity) {

    /**
     * Returns the total number of pages in the adapter.
     * 
     * @return The page count
     */
    override fun getItemCount(): Int = pageCount

    /**
     * Creates a Fragment for the specified position.
     * 
     * Each fragment displays its page number to help users track
     * paging progress during the demo.
     * 
     * @param position The zero-based page index
     * @return A new PageFragment instance for the specified position
     */
    override fun createFragment(position: Int): Fragment {
        return PageFragment.newInstance(position)
    }
}
