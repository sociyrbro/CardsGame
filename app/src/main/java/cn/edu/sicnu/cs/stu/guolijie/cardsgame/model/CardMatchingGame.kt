package cn.edu.sicnu.cs.stu.guolijie.cardsgame.model

/**
 * 翻牌游戏模型（MVC 中的 Model）。
 * 只负责规则与状态：计分、翻开、配对，不涉及任何界面代码。
 */
class CardMatchingGame(count: Int) {
    var score = 0
        private set

    private val cards: MutableList<Card>

    init {
        val deck = Deck()
        cards = mutableListOf()
        for (i in 1..count) {
            val card: Card? = deck.drawRandomCard()
            if (card != null) {
                cards.add(card)
            }
        }
    }

    fun cardAtIndex(index: Int): Card {
        return cards[index]
    }

    /** 本局牌数。 */
    fun cardCount(): Int = cards.size

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /**
     * 翻开第 index 张牌：
     * 已翻开的牌再点一次会合上；之后与其他已翻开的牌比较，
     * 配对成功两张牌都标记为已配对，失败则另一张合上并扣分。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (!card.isMatched) {
            if (card.isChosen) {
                card.isChosen = false
            } else {
                for (otherCard in cards) {
                    if (otherCard.isChosen && !otherCard.isMatched) {
                        val matchScore = card.match(arrayOf(otherCard))
                        if (matchScore > 0) {
                            score += matchScore * MATCH_BONUS
                            otherCard.isMatched = true
                            card.isMatched = true
                        } else {
                            score -= MISMATCH_PENALTY
                            otherCard.isChosen = false
                        }
                        break
                    }
                }
                score -= COST_TO_CHOOSE
                card.isChosen = true
            }
        }
    }
}
