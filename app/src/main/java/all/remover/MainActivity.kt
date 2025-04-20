package all.remover

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            setupViewPager()
        } else {
            Toast.makeText(this, "لطفاً دسترسی به حافظه را فراهم کنید", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        requestStoragePermissions()
    }

    private fun requestStoragePermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest)
        } else {
            setupViewPager()
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Images"
                1 -> "Videos"
                2 -> "Audio"
                else -> null
            }
        }.attach()

        // تنظیم استایل تب‌ها
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    it.view.setBackgroundResource(R.drawable.tab_selected_background) // پس‌زمینه تب فعال
                    it.text?.let { text ->
                        it.view.findViewById<android.widget.TextView>(android.R.id.text1)?.apply {
                            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
                            textSize = 16f // بزرگ‌تر کردن متن تب فعال
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    it.view.setBackgroundResource(R.drawable.tab_unselected_background) // پس‌زمینه تب غیرفعال
                    it.view.findViewById<android.widget.TextView>(android.R.id.text1)?.apply {
                        setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.black))
                        textSize = 14f // کوچک‌تر کردن متن تب غیرفعال
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // تنظیم تب اولیه
        tabLayout.getTabAt(0)?.view?.setBackgroundResource(R.drawable.tab_selected_background)
        tabLayout.getTabAt(0)?.view?.findViewById<android.widget.TextView>(android.R.id.text1)?.apply {
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            textSize = 16f
        }
    }
}