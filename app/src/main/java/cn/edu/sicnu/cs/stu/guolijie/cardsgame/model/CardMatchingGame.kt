package cn.edu.sicnu.cs.stu.guolijie.cardsgame.model

/**
 * 翻牌游戏模型（MVC 中的 Model）。
 * 只负责规则与状态：发牌、计分、翻开、配对，不涉及任何界面代码。
 *
 * 实验五起改为「三张牌」玩法：连续翻开三张牌，
 * 三张点数相同（或三张花色相同）即配对成功。
 */
class CardMatchingGame(private val count: Int) {
    var score = 0
        private set

    private val cards: MutableList<Card> = mutableListOf()

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /** 需要连续翻开多少张牌才进行一次判定。 */
    val CARDS_TO_MATCH = 3

    init {
        deal()
    }

    /** 重新发牌（不重置得分，供 reset() 复用）。 */
    private fun deal() {
        cards.clear()
        val deck = Deck()
        repeat(count) {
            deck.drawRandomCard()?.let { card -> cards.add(card) }
        }
    }

    /** 重新开始一局：清空得分并重新发牌。 */
    fun reset() {
        score = 0
        deal()
    }

    fun cardAtIndex(index: Int): Card {
        return cards[index]
    }

    /** 本局牌数（RecyclerView 适配器需要）。 */
    fun cardCount(): Int = cards.size

    /**
     * 翻开第 index 张牌：
     * 1. 已配对的牌不能再点；
     * 2. 已翻开的牌再点一次会合上；
     * 3. 每翻开一张扣 1 分；
     * 4. 当同时翻开 3 张牌时判定：点数全相同得 8 分、花色全相同得 2 分，
     *    配对成功则三张牌都标记为已配对，失败则扣 2 分并全部合上。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) {
            return
        }
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        card.isChosen = true
        score -= COST_TO_CHOOSE

        val chosenCards = cards.filter { it.isChosen && !it.isMatched }
        if (chosenCards.size < CARDS_TO_MATCH) {
            return
        }

        val matchScore = chosenCards[0].match(chosenCards.drop(1).toTypedArray())
        if (matchScore > 0) {
            score += matchScore * MATCH_BONUS
            chosenCards.forEach {
                it.isMatched = true
                it.isChosen = false
            }
        } else {
            score -= MISMATCH_PENALTY
            chosenCards.forEach { it.isChosen = false }
        }
    }
}
