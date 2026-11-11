package cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui

import cn.edu.sicnu.cs.stu.guolijie.cardsgame.R

/**
 * 第 2 个 Fragment：两张牌的翻牌游戏。
 *
 * 每次翻开 2 张牌判定一次：点数相同得 4 分，花色相同得 1 分，
 * 都不一样扣 2 分并合上。棋局保存在 two_card_game.dat。
 */
class TwoCardGameFragment : BaseGameFragment() {

    override val initialCardCount: Int = 16
    override val cardsToMatch: Int = 2
    override val dataFileName: String = "two_card_game.dat"
    override val ruleTextRes: Int = R.string.rule_two_card

    companion object {
        @JvmStatic
        fun newInstance(): TwoCardGameFragment = TwoCardGameFragment()
    }
}
