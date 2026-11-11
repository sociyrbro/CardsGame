package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui.PersonInfoFragment
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui.Refreshable
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui.ThreeCardGameFragment
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui.TwoCardGameFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 实验六：多 Fragment 应用的主界面。
 *
 * 与实验四、实验五的三个 Activity 不同，这里整个 App 只有一个 Activity，
 * 页面全部交给 Fragment 完成：
 *   - PersonInfoFragment  个人信息（可编辑，序列化保存）
 *   - TwoCardGameFragment 两张牌的翻牌游戏（序列化保存棋局）
 *   - ThreeCardGameFragment 三张牌的翻牌游戏（序列化保存棋局）
 * 页面切换由 BottomNavigationView + FragmentTransaction 的 show/hide 完成，
 * 不再使用 startActivity 跳转。
 */
class MainActivity : AppCompatActivity() {

    private companion object {
        const val TAG_PERSON = "fragment_person"
        const val TAG_TWO = "fragment_two"
        const val TAG_THREE = "fragment_three"
        const val KEY_TAG = "current_tag"
    }

    private lateinit var bottomNav: BottomNavigationView

    /** 当前显示的 Fragment 的 tag。 */
    private var currentTag = TAG_PERSON

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applyContentInsets(findViewById(R.id.root))

        // 从文件恢复个人信息（第一次运行时使用默认资料）
        PlayerStore.initIfNeeded(this)

        bottomNav = findViewById(R.id.bottomNav)

        if (savedInstanceState == null) {
            // 三个 Fragment 一次加入容器，默认只显示第一个
            val person = PersonInfoFragment.newInstance()
            val two = TwoCardGameFragment.newInstance()
            val three = ThreeCardGameFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, person, TAG_PERSON)
                .add(R.id.fragmentContainer, two, TAG_TWO)
                .add(R.id.fragmentContainer, three, TAG_THREE)
                .hide(two)
                .hide(three)
                .commit()
            currentTag = TAG_PERSON
        } else {
            // 旋转屏幕等导致 Activity 重建时 FragmentManager 已经恢复三个 Fragment，
            // 这里只需要恢复当前选中的是哪一页。
            currentTag = savedInstanceState.getString(KEY_TAG) ?: TAG_PERSON
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_person -> showFragment(TAG_PERSON)
                R.id.nav_two -> showFragment(TAG_TWO)
                R.id.nav_three -> showFragment(TAG_THREE)
            }
            true
        }

        // 同步底部导航栏的选中状态（会触发一次上面的监听，但 tag 相同会直接返回）
        bottomNav.selectedItemId = menuIdOf(currentTag)
        supportActionBar?.title = getString(titleOf(currentTag))
    }

    /**
     * 切换到 tag 对应的 Fragment：隐藏其余 Fragment，再显示目标 Fragment。
     * 用 show/hide 而不是 replace，切换页签时已经打开的牌局不会被销毁。
     */
    private fun showFragment(tag: String) {
        if (tag == currentTag) {
            return
        }
        val target = supportFragmentManager.findFragmentByTag(tag) ?: return
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment !== target) {
                transaction.hide(fragment)
            }
        }
        transaction.show(target).commit()
        currentTag = tag
        supportActionBar?.title = getString(titleOf(tag))
        // 让刚显示出来的界面刷新一次（例如切回来时最高分可能已经变化）
        (target as? Refreshable)?.onShownAgain()
    }

    private fun menuIdOf(tag: String): Int = when (tag) {
        TAG_TWO -> R.id.nav_two
        TAG_THREE -> R.id.nav_three
        else -> R.id.nav_person
    }

    private fun titleOf(tag: String): Int = when (tag) {
        TAG_TWO -> R.string.title_two_card_game
        TAG_THREE -> R.string.title_three_card_game
        else -> R.string.title_person_info
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TAG, currentTag)
    }

    /** 创建标题栏右侧的菜单。 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** 菜单项点击：选中「关于」时弹出对话框。 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                showAboutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(R.string.about_ok, null)
            .show()
    }
}
