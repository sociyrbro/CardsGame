package cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui

/**
 * Fragment 被底部导航栏重新切回时调用，用来刷新界面上的数据。
 * MainActivity 只依赖这个接口，不需要知道具体是哪一个 Fragment。
 */
interface Refreshable {
    fun onShownAgain()
}
