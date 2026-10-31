package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.CardMatchingGame
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * 翻牌游戏 Activity（MVC 中的 Controller）：
 * 接收用户点击 → 调用 CardMatchingGame 处理规则 → 把模型状态同步到界面。
 */
class CardGameActivity : AppCompatActivity() {

    private companion object {
        const val CARD_COUNT = 16
    }

    private lateinit var gridCards: GridLayout
    private lateinit var tvScore: TextView
    private lateinit var game: CardMatchingGame

    private val cardViews = mutableListOf<MaterialCardView>()
    private val cardFaces = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)
        applyContentInsets(findViewById(R.id.root))

        gridCards = findViewById(R.id.gridCards)
        tvScore = findViewById(R.id.tvScore)

        createCardViews()

        findViewById<MaterialButton>(R.id.btnReset).setOnClickListener { startNewGame() }
        findViewById<MaterialButton>(R.id.btnExit).setOnClickListener { finish() }

        startNewGame()
    }

    /** 按 4×4 的网格创建 16 张牌，并统一绑定点击事件。 */
    private fun createCardViews() {
        val inflater = LayoutInflater.from(this)
        for (index in 0 until CARD_COUNT) {
            val cardView =
                inflater.inflate(R.layout.item_card, gridCards, false) as MaterialCardView
            val face = cardView.findViewById<TextView>(R.id.tvCardFace)
            cardView.contentDescription = getString(R.string.content_desc_card, index + 1)
            cardView.setOnClickListener { onCardClicked(index) }
            gridCards.addView(cardView)
            cardViews.add(cardView)
            cardFaces.add(face)
        }
    }

    /** 重新开始一局：重新发牌并刷新界面。 */
    private fun startNewGame() {
        game = CardMatchingGame(CARD_COUNT)
        refreshUi()
    }

    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)
        refreshUi()

        val allMatched = (0 until CARD_COUNT).all { game.cardAtIndex(it).isMatched }
        if (allMatched) {
            Toast.makeText(this, R.string.msg_all_matched, Toast.LENGTH_SHORT).show()
        }
    }

    /** 把模型的牌面、配对状态和得分同步到 View。 */
    private fun refreshUi() {
        for (index in 0 until CARD_COUNT) {
            val card = game.cardAtIndex(index)
            val cardView = cardViews[index]
            val face = cardFaces[index]

            when {
                card.isMatched -> {
                    face.text = card.toString()
                    face.setTextColor(getColor(R.color.cardMatchedText))
                    cardView.setCardBackgroundColor(getColor(R.color.cardMatched))
                    cardView.alpha = 0.85f
                }

                card.isChosen -> {
                    face.text = card.toString()
                    face.setTextColor(suitColorOf(card.toString()))
                    cardView.setCardBackgroundColor(getColor(R.color.cardFace))
                    cardView.alpha = 1f
                }

                else -> {
                    face.text = getString(R.string.card_back_symbol)
                    face.setTextColor(getColor(R.color.cardBackSymbol))
                    cardView.setCardBackgroundColor(getColor(R.color.cardBack))
                    cardView.alpha = 1f
                }
            }
        }

        tvScore.text = getString(R.string.score_format, game.score)
        if (game.score > PlayerStore.highScore) {
            PlayerStore.highScore = game.score
        }
    }

    /** 红桃、方块用红色显示，黑桃、梅花用黑色显示。 */
    private fun suitColorOf(cardText: String): Int {
        val suit = cardText.substring(0, 1)
        return if (suit == "♥" || suit == "♦") {
            getColor(R.color.suitRed)
        } else {
            getColor(R.color.suitBlack)
        }
    }
}
