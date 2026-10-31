package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore

/**
 * 主 Activity（MVC 中的 View）：显示玩家基本信息，并提供进入另外两个界面的入口。
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applyContentInsets(findViewById(R.id.root))

        PlayerStore.initIfNeeded(this)

        findViewById<View>(R.id.btnEditInfo).setOnClickListener {
            startActivity(Intent(this, PersonInfoActivity::class.java))
        }
        findViewById<View>(R.id.btnCardGame).setOnClickListener {
            startActivity(Intent(this, CardGameActivity::class.java))
        }
    }

    /** 每次回到主界面都重新读取资料，保证修改后立即生效。 */
    override fun onResume() {
        super.onResume()
        showPlayerInfo()
    }

    private fun showPlayerInfo() {
        findViewById<TextView>(R.id.tvName).text = PlayerStore.name
        findViewById<TextView>(R.id.tvGender).text = PlayerStore.gender
        findViewById<TextView>(R.id.tvStudentId).text = PlayerStore.studentId
        findViewById<TextView>(R.id.tvPhone).text = PlayerStore.phone
        findViewById<TextView>(R.id.tvEmail).text = PlayerStore.email
        findViewById<TextView>(R.id.tvHobby).text =
            PlayerStore.hobbies.joinToString(getString(R.string.hobby_separator))
        findViewById<TextView>(R.id.tvHighScore).text = PlayerStore.highScore.toString()
    }
}
