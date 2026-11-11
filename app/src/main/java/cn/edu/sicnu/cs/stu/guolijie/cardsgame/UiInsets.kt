package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding

/**
 * 处理系统栏（状态栏 / 导航栏）占位。
 *
 * 说明（实验六真机调试记录）：
 * 实验五这里的写法是 top = 状态栏高度 + ActionBar 高度。在 Android 16 真机上实测发现，
 * ViewCompat 分发到内容根部的 top inset 已经包含了标题栏的高度（实测 288px = 状态栏 + 标题栏），
 * 再加一次 ActionBar 高度会让页面顶部空出一条 (top=456px) 的空白，内容整体下移，
 * 因此这里改为只使用系统给出的 inset，底部同理。
 */
fun AppCompatActivity.applyContentInsets(root: View) {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(top = bars.top, bottom = bars.bottom)
        insets
    }
}
