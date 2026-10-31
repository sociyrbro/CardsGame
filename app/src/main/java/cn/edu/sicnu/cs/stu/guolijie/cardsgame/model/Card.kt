package cn.edu.sicnu.cs.stu.guolijie.cardsgame.model

/**
 * 一张扑克牌。花色、点数为私有属性，对外通过 toString() 输出 "♥A" 这样的文本。
 * isChosen 表示是否被翻开，isMatched 表示是否已经配对成功。
 */
class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {
    companion object {
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String {
        return suit + rank
    }

    /**
     * 与另一张牌比较：点数相同得 4 分，花色相同得 1 分，都不同得 0 分。
     */
    fun match(otherCards: Array<Card>): Int {
        var score = 0
        if (otherCards.size == 1) {
            val otherCard = otherCards[0]
            if (otherCard.rank == rank) {
                score = 4
            } else if (otherCard.suit == suit) {
                score = 1
            }
        }
        return score
    }
}
