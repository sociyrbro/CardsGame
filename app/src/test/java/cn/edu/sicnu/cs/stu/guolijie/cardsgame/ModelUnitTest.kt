package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.Card
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.Deck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 游戏模型的单元测试（在开发机上直接运行，不需要设备）。
 * 实验五起模型改为「三张牌」玩法，这里同时覆盖两张牌与三张牌的匹配规则。
 */
class ModelUnitTest {

    @Test
    fun deck_hasFiftyTwoDistinctCards() {
        val deck = Deck()
        assertEquals(52, deck.size())

        val drawn = mutableSetOf<String>()
        repeat(52) {
            val card = deck.drawRandomCard()
            assertNotNull(card)
            drawn.add(card!!.toString())
        }

        assertEquals(52, drawn.size)
        assertEquals(0, deck.size())
        assertNull(deck.drawRandomCard())
    }

    @Test
    fun match_twoCards_sameRankScoresFour() {
        val first = Card("♥", "A")
        val second = Card("♠", "A")
        assertEquals(4, first.match(arrayOf(second)))
    }

    @Test
    fun match_twoCards_sameSuitScoresOne() {
        val first = Card("♥", "A")
        val second = Card("♥", "K")
        assertEquals(1, first.match(arrayOf(second)))
    }

    @Test
    fun match_twoCards_differentScoresZero() {
        val first = Card("♥", "A")
        val second = Card("♠", "K")
        assertEquals(0, first.match(arrayOf(second)))
    }

    @Test
    fun match_threeCards_sameRankScoresEight() {
        val first = Card("♥", "7")
        val second = Card("♠", "7")
        val third = Card("♦", "7")
        assertEquals(8, first.match(arrayOf(second, third)))
    }

    @Test
    fun match_threeCards_sameSuitScoresTwo() {
        val first = Card("♥", "7")
        val second = Card("♥", "9")
        val third = Card("♥", "K")
        assertEquals(2, first.match(arrayOf(second, third)))
    }

    @Test
    fun match_threeCards_mixedScoresZero() {
        val first = Card("♥", "7")
        val second = Card("♠", "9")
        val third = Card("♦", "7")
        assertEquals(0, first.match(arrayOf(second, third)))
    }

    @Test
    fun chooseOneCard_costsOnePoint_andSecondTapClosesIt() {
        val game = CardMatchingGame(24)

        game.chooseCardAtIndex(0)
        assertEquals(-1, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)

        game.chooseCardAtIndex(0)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertEquals(-1, game.score)
    }

    @Test
    fun chooseThreeSameSuitCards_scoresBonus_andMarksAllMatched() {
        val game = CardMatchingGame(24)
        val triple = findSameSuitTriple(game, 24)
        assertNotNull("24 张牌只有 4 种花色，必然存在三张同花色", triple)

        triple!!.forEach { game.chooseCardAtIndex(it) }

        // 三张同花色：2 分 × MATCH_BONUS(4) = 8 分，再扣三次翻牌 3 分
        assertEquals(5, game.score)
        triple.forEach { assertTrue(game.cardAtIndex(it).isMatched) }
    }

    @Test
    fun chooseThreeNonMatchingCards_penalizes_andClosesAll() {
        val game = CardMatchingGame(24)
        val triple = findNonMatchingTriple(game, 24)
        assertNotNull("24 张牌中一定能找到三张花色和点数都不同的牌", triple)

        triple!!.forEach { game.chooseCardAtIndex(it) }

        // 翻三张扣 3 分，配对失败再扣 2 分
        assertEquals(-5, game.score)
        triple.forEach {
            assertFalse(game.cardAtIndex(it).isMatched)
            assertFalse(game.cardAtIndex(it).isChosen)
        }
    }

    @Test
    fun reset_clearsScoreAndRedeals() {
        val game = CardMatchingGame(24)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertNotEquals(0, game.score)

        game.reset()

        assertEquals(0, game.score)
        assertEquals(24, game.cardCount())
        (0 until game.cardCount()).forEach {
            assertFalse(game.cardAtIndex(it).isChosen)
            assertFalse(game.cardAtIndex(it).isMatched)
        }
    }

    @Test
    fun cardCount_matchesRequestedCount() {
        assertEquals(24, CardMatchingGame(24).cardCount())
        assertEquals(18, CardMatchingGame(18).cardCount())
    }

    /** 找三张花色相同的牌。 */
    private fun findSameSuitTriple(game: CardMatchingGame, count: Int): List<Int>? {
        val bySuit = mutableMapOf<Char, MutableList<Int>>()
        for (i in 0 until count) {
            val suit = game.cardAtIndex(i).toString().first()
            bySuit.getOrPut(suit) { mutableListOf() }.add(i)
        }
        return bySuit.values.firstOrNull { it.size >= 3 }?.take(3)
    }

    /** 找三张花色、点数都互不相同的牌。 */
    private fun findNonMatchingTriple(game: CardMatchingGame, count: Int): List<Int>? {
        for (i in 0 until count) {
            for (j in i + 1 until count) {
                for (k in j + 1 until count) {
                    val cards = listOf(i, j, k).map { game.cardAtIndex(it) }
                    val suits = cards.map { it.toString().first() }.toSet()
                    val ranks = cards.map { it.toString().drop(1) }.toSet()
                    if (suits.size == 3 && ranks.size == 3) {
                        return listOf(i, j, k)
                    }
                }
            }
        }
        return null
    }
}
