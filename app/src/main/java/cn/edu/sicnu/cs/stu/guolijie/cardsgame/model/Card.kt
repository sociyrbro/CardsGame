package cn.edu.sicnu.cs.stu.guolijie.cardsgame.model

import java.io.Serializable

/**
 * 一张扑克牌。花色、点数为私有属性，对外通过 toString() 输出 "♥A" 这样的文本。
 * isChosen 表示是否被翻开，isMatched 表示是否已经配对成功。
 *
 * 实验六要求把牌局写入文件保存，因此本类实现 Serializable 接口，
 * 这样整个牌局（包括每张牌的状态）都可以用 ObjectOutputStream 直接写出。
 */
class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {

    companion object {
        /** 序列化版本号，保证写入与读取时类的结构一致。 */
        private const val serialVersionUID = 1L

        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String {
        return suit + rank
    }

    /**
     * 比较匹配程度。
     *
     * otherCards.size == 1：两张牌比大小，点数相同得 4 分，花色相同得 1 分（两张牌玩法）。
     * otherCards.size == 2：三张牌一起比，三张点数全相同得 8 分，三张花色全相同得 2 分
     *                      （三张牌玩法）。
     */
    fun match(otherCards: Array<Card>): Int {
        return when (otherCards.size) {
            1 -> {
                val otherCard = otherCards[0]
                when {
                    otherCard.rank == rank -> 4
                    otherCard.suit == suit -> 1
                    else -> 0
                }
            }

            2 -> {
                val group = mutableListOf(this)
                group.addAll(otherCards)
                val sameRank = group.all { it.rank == rank }
                val sameSuit = group.all { it.suit == suit }
                when {
                    sameRank -> 8
                    sameSuit -> 2
                    else -> 0
                }
            }

            else -> 0
        }
    }
}
