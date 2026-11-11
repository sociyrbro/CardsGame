package cn.edu.sicnu.cs.stu.guolijie.cardsgame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.R
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.Card
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.CardMatchingGame
import com.google.android.material.card.MaterialCardView

/**
 * 翻牌游戏的适配器：把模型里的每一张牌绑定到 item_card 布局上。
 * 通过 [OnCardClickListener] 把点击事件回调给 Fragment（控制器），
 * 适配器本身不修改模型，只负责显示。
 * 两张牌玩法与三张牌玩法共用这一个适配器，只需要传不同的 [CardMatchingGame]。
 */
class CardAdapter(
    private val game: CardMatchingGame,
    private val spanCount: Int,
    private val listener: OnCardClickListener
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private companion object {
        /** 与 item_card.xml 中的 layout_margin 保持一致。 */
        const val CARD_MARGIN_DP = 4
    }

    /** 点击回调：由 Fragment 实现，按下标处理翻牌逻辑。 */
    fun interface OnCardClickListener {
        fun onCardClicked(position: Int)
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView as MaterialCardView
        private val face: TextView = itemView.findViewById(R.id.tvCardFace)

        init {
            // 这里就是对回调的处理：把点击转成 position 交给控制器
            cardView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCardClicked(position)
                }
            }
        }

        fun bind(card: Card) {
            val context = itemView.context
            when {
                card.isMatched -> {
                    face.text = card.toString()
                    face.setTextColor(context.getColor(R.color.cardMatchedText))
                    cardView.setCardBackgroundColor(context.getColor(R.color.cardMatched))
                    cardView.alpha = 0.85f
                }

                card.isChosen -> {
                    face.text = card.toString()
                    face.setTextColor(suitColor(card.toString()))
                    cardView.setCardBackgroundColor(context.getColor(R.color.cardFace))
                    cardView.alpha = 1f
                }

                else -> {
                    face.text = context.getString(R.string.card_back_symbol)
                    face.setTextColor(context.getColor(R.color.cardBackSymbol))
                    cardView.setCardBackgroundColor(context.getColor(R.color.cardBack))
                    cardView.alpha = 1f
                }
            }
            itemView.contentDescription =
                context.getString(R.string.content_desc_card, bindingAdapterPosition + 1)
        }

        /** 红桃、方块用红色显示，黑桃、梅花用黑色显示。 */
        private fun suitColor(cardText: String): Int {
            val suit = cardText.substring(0, 1)
            val context = itemView.context
            return if (suit == "♥" || suit == "♦") {
                context.getColor(R.color.suitRed)
            } else {
                context.getColor(R.color.suitBlack)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        val metrics = parent.context.resources.displayMetrics
        val marginPx = (CARD_MARGIN_DP * metrics.density).toInt()
        // 按列宽算出“理想高度”（扑克牌比例约 1.35），
        // 再按可用高度压缩，保证所有行都能在一屏内显示完整。
        val cellWidth = if (parent.width > 0) {
            parent.width / spanCount
        } else {
            metrics.widthPixels / spanCount
        }
        val rows = (game.cardCount() + spanCount - 1) / spanCount
        val availableHeight = parent.height - parent.paddingTop - parent.paddingBottom
        val byHeight = if (availableHeight > 0 && rows > 0) {
            (availableHeight - marginPx * 2 * rows) / rows
        } else {
            Int.MAX_VALUE
        }
        val byWidth = (cellWidth * 1.35f).toInt()
        view.layoutParams.height = minOf(byWidth, byHeight).coerceAtLeast(1)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(game.cardAtIndex(position))
    }

    override fun getItemCount(): Int = game.cardCount()
}
