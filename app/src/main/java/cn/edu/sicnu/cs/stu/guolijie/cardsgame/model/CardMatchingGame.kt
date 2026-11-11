package cn.edu.sicnu.cs.stu.guolijie.cardsgame.model

import java.io.Serializable

/**
 * 翻牌游戏模型（MVC 中的 Model）。
 * 只负责规则与状态：发牌、计分、翻开、配对，不涉及任何界面代码。
 *
 * 实验六在上一个实验的基础上把「一次判定需要翻开几张牌」做成参数 cardsToMatch：
 *   - cardsToMatch = 2：两张牌玩法，点数相同得 4 分、花色相同得 1 分；
 *   - cardsToMatch = 3：三张牌玩法，三张点数全相同得 8 分、三张花色全相同得 2 分。
 *
 * 为了满足「退出程序后再打开可以恢复」的要求，本类实现 Serializable 接口，
 * 整个牌局对象（得分 + 每一张牌的状态）都可以被序列化写入文件。
 */
class CardMatchingGame(
    val count: Int,
    val cardsToMatch: Int = 3
) : Serializable {

    companion object {
        private const val serialVersionUID = 2L
    }

    var score = 0
        private set

    private val cards: MutableList<Card> = mutableListOf()

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    /** 本局一共发了几张牌（RecyclerView 适配器需要）。 */
    fun cardCount(): Int = cards.size

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

    /**
     * 翻开第 index 张牌：
     * 1. 已配对的牌不能再点；
     * 2. 已翻开的牌再点一次会合上；
     * 3. 每翻开一张扣 1 分；
     * 4. 当同时翻开 cardsToMatch 张牌时判定：按 Card.match() 计算分值，
     *    配对成功则这几张牌都标记为已配对，失败则扣 2 分并全部合上。
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
        if (chosenCards.size < cardsToMatch) {
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

    /** 是否所有牌都已经配对完成。 */
    fun isAllMatched(): Boolean = cards.all { it.isMatched }
}
