package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding

/**
 * targetSdk 35 起系统强制“边到边”显示，页面内容会绘制到状态栏和 ActionBar 下面。
 * 这里根据系统栏高度和 ActionBar 高度给内容根部留出安全区域，
 * 并把状态栏文字改成深色，避免白底上看不清时间。
 */
fun AppCompatActivity.applyContentInsets(root: View) {
    val typedValue = TypedValue()
    theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
    val actionBarHeight =
        TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(top = bars.top + actionBarHeight, bottom = bars.bottom)
        insets
    }
}
