package cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui

import cn.edu.sicnu.cs.stu.guolijie.cardsgame.R

/**
 * 第 3 个 Fragment：三张牌的翻牌游戏。
 *
 * 每次翻开 3 张牌判定一次：三张点数全相同得 8 分，三张花色全相同得 2 分，
 * 否则扣 2 分并合上。棋局保存在 three_card_game.dat。
 */
class ThreeCardGameFragment : BaseGameFragment() {

    override val initialCardCount: Int = 24
    override val cardsToMatch: Int = 3
    override val dataFileName: String = "three_card_game.dat"
    override val ruleTextRes: Int = R.string.rule_three_card

    companion object {
        @JvmStatic
        fun newInstance(): ThreeCardGameFragment = ThreeCardGameFragment()
    }
}
