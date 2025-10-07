package com.permissionx.mingdev

import androidx.fragment.app.FragmentActivity

object PermissionX {

    private const val FRAGMENT_TAG = "invisibleFragment"

    @JvmStatic
    fun requestPermission(
        activity: FragmentActivity,
        vararg permission: String,
        callback: PermissionCallback?
    ) {
        val supportFragmentManager = activity.supportFragmentManager
        val existedFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        val fragment = if (existedFragment == null) {
            val invisibleFragment = InvisibleFragment.newInstance()
            supportFragmentManager.beginTransaction().add(invisibleFragment, FRAGMENT_TAG)
                .commitNow()
            invisibleFragment
        } else {
            existedFragment as? InvisibleFragment
        }
        fragment?.requestNow(callback, *permission)
    }
}