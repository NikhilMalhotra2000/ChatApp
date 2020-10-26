package com.example.chatapp.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.ui.ItemsViewModel

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fa: FragmentActivity, private val items: ItemsViewModel)
    : FragmentStateAdapter(fa) {


    override fun getItemCount() = items.size

    override fun createFragment(position: Int): Fragment {

        val itemId = items.itemId(position)
        val itemText = items.itemToItemText(items.getItemById(itemId))
        return when(position) {
            0 -> {
                return when (itemText) {
                    "CameraFragment" -> CameraFragment()
                    "GalleryFragment" -> GalleryFragment()
                    else -> PermissionsFragment()
                }
            }
            1 -> {
                ChatsFragment.newInstance()
            }
            else -> PlaceholderFragment.newInstance(position + 1)
        }
    }

    override fun getItemId(position: Int) = items.itemId(position)
    override fun containsItem(itemId: Long): Boolean = items.contains(itemId)

}