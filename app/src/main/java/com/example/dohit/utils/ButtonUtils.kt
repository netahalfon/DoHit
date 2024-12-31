package com.example.dohit.utils
import android.view.View

// פונקציה כללית לאנימציה על כפתור עם פעולה מותאמת
fun <T : View> setupButtonWithAnimation(button: T, action: () -> Unit) {
    button.setOnClickListener {
        button.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(150)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .withEndAction {
                        action()
                    }.start()
            }.start()
    }
}

