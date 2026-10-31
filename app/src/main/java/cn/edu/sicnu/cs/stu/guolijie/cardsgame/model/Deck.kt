package cn.edu.sicnu.cs.stu.guolijie.cardsgame.model

import java.util.Random

/**
 * 一副 52 张的牌，可以随机抽取，抽到的牌会从牌堆里移除。
 */
class Deck() {
    private val cards = mutableListOf<Card>()
    private val r = Random()

    init {
        for (suit in Card.validSuits) {
            for (rank in Card.rankStrings) {
                val card = Card(suit = suit, rank = rank)
                cards.add(card)
            }
        }
    }

    /**
     * 随机抽一张牌；牌堆为空时返回 null。
     */
    fun drawRandomCard(): Card? {
        var randomCard: Card? = null
        if (cards.size > 0) {
            randomCard = cards.removeAt(r.nextInt(cards.size))
        }
        return randomCard
    }

    /** 牌堆中剩余的牌数（供测试与调试使用）。 */
    fun size(): Int = cards.size
}
