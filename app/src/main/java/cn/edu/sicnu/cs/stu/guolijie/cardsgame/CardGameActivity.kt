package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.adapter.CardAdapter
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.CardMatchingGame
import com.google.android.material.button.MaterialButton

/**
 * 翻牌游戏 Activity（MVC 中的 Controller）：
 * 接收用户点击 → 调用 CardMatchingGame 处理规则 → 通知适配器刷新界面。
 *
 * 竖屏 4 列 24 张牌，横屏 6 列 18 张牌，旋转屏幕后自动按新布局重新开局。
 */
class CardGameActivity : AppCompatActivity(), CardAdapter.OnCardClickListener {

    private companion object {
        const val PORTRAIT_COLUMNS = 4
        const val PORTRAIT_CARDS = 24
        const val LANDSCAPE_COLUMNS = 9
        const val LANDSCAPE_CARDS = 18
    }

    private lateinit var recyclerCards: RecyclerView
    private lateinit var tvScore: TextView
    private lateinit var game: CardMatchingGame
    private lateinit var cardAdapter: CardAdapter

    private var spanCount = PORTRAIT_COLUMNS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)
        applyContentInsets(findViewById(R.id.root))

        val isPortrait =
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        spanCount = if (isPortrait) PORTRAIT_COLUMNS else LANDSCAPE_COLUMNS
        val cardCount = if (isPortrait) PORTRAIT_CARDS else LANDSCAPE_CARDS

        recyclerCards = findViewById(R.id.recyclerCards)
        tvScore = findViewById(R.id.tvScore)

        game = CardMatchingGame(cardCount)
        cardAdapter = CardAdapter(game, spanCount, this)
        recyclerCards.layoutManager = GridLayoutManager(this, spanCount)
        recyclerCards.adapter = cardAdapter
        recyclerCards.setHasFixedSize(true)

        findViewById<MaterialButton>(R.id.btnReset).setOnClickListener { resetGame() }
        findViewById<MaterialButton>(R.id.btnExit).setOnClickListener { finish() }

        updateScore()
    }

    /** 适配器回调：用户点了第 position 张牌。 */
    override fun onCardClicked(position: Int) {
        game.chooseCardAtIndex(position)
        cardAdapter.notifyDataSetChanged()
        updateScore()

        val allMatched = (0 until game.cardCount()).all { game.cardAtIndex(it).isMatched }
        if (allMatched) {
            Toast.makeText(this, R.string.msg_all_matched, Toast.LENGTH_SHORT).show()
        }
    }

    /** 重新开始一局：模型 reset()，适配器整体刷新。 */
    private fun resetGame() {
        game.reset()
        cardAdapter.notifyDataSetChanged()
        updateScore()
    }

    private fun updateScore() {
        tvScore.text = getString(R.string.score_format, game.score)
        if (game.score > PlayerStore.highScore) {
            PlayerStore.highScore = game.score
        }
    }
}
