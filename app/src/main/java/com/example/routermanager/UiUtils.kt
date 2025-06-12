package com.example.routermanager

import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun toggleButtonContainer(view: View, fab: FloatingActionButton) {
    val visible = view.isVisible
    if (visible) {
        view.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction { view.visibility = View.GONE }
            .start()
        fab.setImageResource(android.R.drawable.ic_menu_more)
        fab.contentDescription = fab.context.getString(R.string.action_expand)
    } else {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate().alpha(1f).setDuration(200).start()
        fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        fab.contentDescription = fab.context.getString(R.string.action_collapse)
    }
}
