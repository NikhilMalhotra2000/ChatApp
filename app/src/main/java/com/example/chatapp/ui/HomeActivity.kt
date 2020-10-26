package com.example.chatapp.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.chatapp.R
import com.example.chatapp.ui.home.SectionsPagerAdapter
import com.example.chatapp.ui.home.PermissionsDialogFragment
import com.example.chatapp.ui.home.PermissionsFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File


const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L

private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)


/** Use external media if it is available, our app's file directory otherwise */
class HomeActivity : AppCompatActivity(), PermissionsDialogFragment.PermissionsDialogListener,
    PermissionsDialogFragment.PermissionsDialogBackListener {

    private lateinit var viewPager: ViewPager2
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter

    private val tabNames = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tabLayout: TabLayout = findViewById(R.id.tabs)

        viewPager = findViewById(R.id.view_pager)

        sectionsPagerAdapter = SectionsPagerAdapter(this, items)
        viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.icon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_photo_camera_24,
                    theme
                )
                else -> tab.text = getString(tabNames[position - 1])
            }
        }.attach()

        tabLayout.setTabWidthAsWrapContent(0)  //custom extension function

        val appBar = findViewById<AppBarLayout>(R.id.appBar)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val hideAppBarAtPosition = 0
                if (appBar != null) {
                    var mBottom = appBar.bottom.toFloat()

                    when {
                        position == hideAppBarAtPosition -> {
                            var y = positionOffset * mBottom - mBottom
                            if (y == -mBottom) {
                                val h = appBar.height
                                if (mBottom < h) mBottom = h.toFloat()
                                y = -mBottom - mBottom / 8f
                            }
                            appBar.translationY = y
                        }
                        position == hideAppBarAtPosition - 1 -> appBar.translationY =
                            -(positionOffset * mBottom)
                        appBar.translationY != 0f -> appBar.translationY = 0f
                    }

                    if (appBar.translationY == -375.75f)
                        appBar.visibility = View.GONE
                    else
                        appBar.visibility = View.VISIBLE
                }

            }
        })

        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

    private fun TabLayout.setTabWidthAsWrapContent(tabPosition: Int) {
        val layout = (this.getChildAt(0) as LinearLayout).getChildAt(tabPosition) as LinearLayout
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 0f
        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.layoutParams = layoutParams
    }

    override fun onResume() {
        super.onResume()
        // Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
        // be trying to set app to immersive mode before it's ready and the flags do not stick
        viewPager.postDelayed({
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
                viewPager.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    companion object {
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

    fun changeDataSet(performChanges: () -> Unit) {
        val idsOld = items.createIdSnapshot()
        performChanges()
        val idsNew = items.createIdSnapshot()
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = idsOld.size
            override fun getNewListSize(): Int = idsNew.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldItemPosition != 1
            //idsOld[oldItemPosition] == idsNew[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areItemsTheSame(oldItemPosition, newItemPosition)
        }, true).dispatchUpdatesTo(viewPager.adapter!!)
    } /*else {
            */

    /** without [DiffUtil] *//*
            val oldPosition = viewPager.currentItem
            val currentItemId = items.itemId(oldPosition)
            performChanges()
            viewPager.adapter!!.notifyDataSetChanged()
            if (items.contains(currentItemId)) {
                val newPosition =
                    (0 until items.size).indexOfFirst { items.itemId(it) == currentItemId }
                viewPager.setCurrentItem(newPosition, false)
            }
        }*/


    override fun onDialogPositiveClick(dialog: DialogFragment) {
        if (!PermissionsFragment.hasPermissions(this)) {
            // Request camera-related permissions
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            // If permissions have already been granted, proceed
            /*
            TODO()
            Navigation.findNavController(requireActivity(), R.id.view_pager).navigate(
                    PermissionsFragmentDirections.actionPermissionsFragmentToCameraFragment())*/

        }
        //changeDataSet { items.replaceAtPos("PermissionFragment", 0) }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        viewPager.currentItem = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                // Take the user to the success fragment when permission is granted
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
                /*val fragment: Fragment = CameraFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.view_pager, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()*/


                /*
                TODO()
                Navigation.findNavController(requireActivity(), R.id.view_pager).navigate(
                        PermissionsFragmentDirections.actionPermissionsFragmentToCameraFragment())*/
            } else {
                viewPager.currentItem = 1
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun backPress(dialog: DialogInterface) {
        Toast.makeText(this, "ckPressed", Toast.LENGTH_SHORT).show()
        Log.e("onBackPressed", "ckPressed")
        dialog.dismiss()
        viewPager.currentItem = 1
    }


    val items: ItemsViewModel by viewModels()

}


class ItemsViewModel : ViewModel() {
    private var nextValue = 1L

    private val items = mutableListOf(
        "CameraFragment" + "#" + nextValue++,
        "ChatsFragment" + "#" + nextValue++,
        "PlaceHolderFragment" + "#" + nextValue++
    )

    /*fun getItemById(id: Long): String = items[id.toInt()]
    fun itemId(position: Int): Long = position.toLong()
    */
    fun getItemById(id: Long): String = items.first { itemToLong(it) == id }
    fun itemId(position: Int): Long = itemToLong(items[position])
    fun replaceAtPos(itemText: String, position: Int) =
        items.set(0, itemTextToItem(itemText, (position + 1).toLong()))

    fun contains(itemId: Long): Boolean = items.any { itemToLong(it) == itemId }
    fun createIdSnapshot(): List<Long> = (0 until size).map { position -> itemId(position) }
    val size: Int get() = items.size

    fun itemToItemText(value: String) = value.split("#")[0]
    private fun itemTextToItem(item: String, value: Long): String = "$item#$value"
    private fun itemToLong(value: String): Long = value.split("#")[1].toLong()
}

