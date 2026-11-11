package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerProfile
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 游戏模型的单元测试（在开发机上直接运行，不需要设备）。
 * 实验六给 CardMatchingGame 增加了 cardsToMatch 参数与 Serializable 接口，
 * 这里在原有测试之外补充「两张牌玩法」和「序列化往返」两方面的测试。
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

    /** 实验六：两张牌玩法只需翻开 2 张就判定一次。 */
    @Test
    fun twoCardGame_judgesAfterTwoCards() {
        val game = CardMatchingGame(16, 2)
        assertEquals(2, game.cardsToMatch)
        assertEquals(16, game.cardCount())

        game.chooseCardAtIndex(0)
        // 只翻开一张时还不会判定
        assertTrue(game.cardAtIndex(0).isChosen)

        game.chooseCardAtIndex(1)
        // 判定之后无论成功还是失败，都不会有处于「翻开」状态的牌
        (0 until game.cardCount()).forEach {
            assertFalse("第 $it 张牌在判定后应已合上", game.cardAtIndex(it).isChosen)
        }
    }

    /** 实验六：三张牌玩法的参数与两张牌玩法互不影响。 */
    @Test
    fun threeCardGame_keepsCardsToMatchThree() {
        val game = CardMatchingGame(24, 3)
        assertEquals(3, game.cardsToMatch)

        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        // 三张牌玩法翻开两张还不会判定，两张牌仍处于翻开状态
        assertTrue(game.cardAtIndex(0).isChosen)
        assertTrue(game.cardAtIndex(1).isChosen)
    }

    /** 实验六核心：牌局可以序列化后完整恢复（写入文件时用的就是这一套）。 */
    @Test
    fun game_serialization_roundTripKeepsState() {
        val game = CardMatchingGame(24, 3)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(5)

        val buffer = ByteArrayOutputStream()
        ObjectOutputStream(buffer).use { it.writeObject(game) }
        val restored = ObjectInputStream(
            ByteArrayInputStream(buffer.toByteArray())
        ).use { it.readObject() as CardMatchingGame }

        assertEquals(game.score, restored.score)
        assertEquals(game.cardCount(), restored.cardCount())
        assertEquals(game.cardsToMatch, restored.cardsToMatch)
        (0 until game.cardCount()).forEach { index ->
            val expected = game.cardAtIndex(index)
            val actual = restored.cardAtIndex(index)
            assertEquals(expected.toString(), actual.toString())
            assertEquals(expected.isChosen, actual.isChosen)
            assertEquals(expected.isMatched, actual.isMatched)
        }
    }

    /** 实验六：个人信息对象同样可以序列化后完整恢复。 */
    @Test
    fun playerProfile_serialization_roundTripKeepsFields() {
        val profile = PlayerProfile(
            name = "郭理杰",
            gender = "男",
            studentId = "2024110213",
            phone = "13800138000",
            email = "guolijie@example.com",
            hobbies = mutableListOf("旅游", "阅读", "游戏"),
            highScore = 42
        )

        val buffer = ByteArrayOutputStream()
        ObjectOutputStream(buffer).use { it.writeObject(profile) }
        val restored = ObjectInputStream(
            ByteArrayInputStream(buffer.toByteArray())
        ).use { it.readObject() as PlayerProfile }

        assertEquals(profile.name, restored.name)
        assertEquals(profile.gender, restored.gender)
        assertEquals(profile.studentId, restored.studentId)
        assertEquals(profile.phone, restored.phone)
        assertEquals(profile.email, restored.email)
        assertEquals(profile.hobbies, restored.hobbies)
        assertEquals(profile.highScore, restored.highScore)
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
