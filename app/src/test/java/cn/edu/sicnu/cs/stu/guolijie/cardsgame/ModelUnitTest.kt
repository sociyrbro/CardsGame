package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.Card
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.Deck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 游戏模型的单元测试（在开发机上直接运行，不需要设备）。
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
    fun match_sameRank_scoresFour() {
        val first = Card("♥", "A")
        val second = Card("♠", "A")
        assertEquals(4, first.match(arrayOf(second)))
    }

    @Test
    fun match_sameSuit_scoresOne() {
        val first = Card("♥", "A")
        val second = Card("♥", "K")
        assertEquals(1, first.match(arrayOf(second)))
    }

    @Test
    fun match_differentSuitAndRank_scoresZero() {
        val first = Card("♥", "A")
        val second = Card("♠", "K")
        assertEquals(0, first.match(arrayOf(second)))
    }

    @Test
    fun chooseCard_costsOnePoint_andSecondTapClosesIt() {
        val game = CardMatchingGame(16)

        game.chooseCardAtIndex(0)
        assertEquals(-1, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)

        game.chooseCardAtIndex(0)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertEquals(-1, game.score)
    }

    @Test
    fun chooseMatchingPair_addsBonus_andMarksBothMatched() {
        val game = CardMatchingGame(16)
        val pair = findPairWithSameRank(game, 16)
        assertNotNull("16 张牌必然存在点数相同的两张", pair)
        val (first, second) = pair!!

        game.chooseCardAtIndex(first)
        assertEquals(-1, game.score)

        game.chooseCardAtIndex(second)
        // 配对 +4 * MATCH_BONUS(4) = +16，再扣一次翻牌代价 1
        assertEquals(14, game.score)
        assertTrue(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isMatched)
    }

    @Test
    fun cardCount_matchesRequestedCount() {
        val game = CardMatchingGame(16)
        assertEquals(16, game.cardCount())
    }

    /** 16 张牌只有 13 种点数，由抽屉原理一定存在点数相同的两张。 */
    private fun findPairWithSameRank(game: CardMatchingGame, count: Int): Pair<Int, Int>? {
        for (i in 0 until count) {
            for (j in i + 1 until count) {
                val rankA = game.cardAtIndex(i).toString().drop(1)
                val rankB = game.cardAtIndex(j).toString().drop(1)
                if (rankA == rankB) {
                    return i to j
                }
            }
        }
        return null
    }
}
