package com.mhmd.alexon.base

import com.mhmd.alexon.R
import com.mhmd.alexon.domain.models.DrawerMenuModel

class Constants {

    val drawerMenuItems = listOf(
        DrawerMenuModel(id = 1, name = "Search Products", R.drawable.doc),
        DrawerMenuModel(id = 2, name = "Cart", R.drawable.menu_profile),
        DrawerMenuModel(id = 3, name = "Delivery Address", R.drawable.location),
        DrawerMenuModel(id = 4, name = "Payment Methods", R.drawable.wallet),
        DrawerMenuModel(id = 5, name = "Contact us", R.drawable.message),
        DrawerMenuModel(id = 6, name = "Settings", R.drawable.setting),
        DrawerMenuModel(id = 7, name = "Help & FAQs", R.drawable.help),
    )

}