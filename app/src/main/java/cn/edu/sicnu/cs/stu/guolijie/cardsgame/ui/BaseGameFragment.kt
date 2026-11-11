package cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.R
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.adapter.CardAdapter
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.model.CardMatchingGame
import com.google.android.material.button.MaterialButton
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 翻牌游戏 Fragment 的公共父类。
 *
 * 两张牌玩法与三张牌玩法的界面、计分逻辑完全一样，区别只有三个参数：
 *   - 发牌张数 initialCardCount
 *   - 判定需要翻开的张数 cardsToMatch
 *   - 保存棋局的文件名 dataFileName
 * 所以把公共部分放在基类里，两个子类只覆写这三个值，实现 Fragment 的复用。
 *
 * 序列化：onStop() 时用 ObjectOutputStream 把 CardMatchingGame 写入文件，
 * onViewCreated() 时用 ObjectInputStream 读回，实现「退出程序后再打开可以恢复」。
 */
abstract class BaseGameFragment : Fragment(), CardAdapter.OnCardClickListener, Refreshable {

    /** 发牌张数。 */
    protected abstract val initialCardCount: Int

    /** 一次判定需要翻开几张牌。 */
    protected abstract val cardsToMatch: Int

    /** 保存棋局的文件名。 */
    protected abstract val dataFileName: String

    /** 界面上方玩法说明的文字资源。 */
    protected abstract val ruleTextRes: Int

    protected lateinit var game: CardMatchingGame

    private lateinit var cardAdapter: CardAdapter
    private lateinit var tvScore: TextView

    /** 是否已经提示过「全部配对完成」，避免每次点击都弹一次。 */
    private var allMatchedToastShown = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PlayerStore.initIfNeeded(requireContext())

        val recyclerCards = view.findViewById<RecyclerView>(R.id.recyclerCards)
        tvScore = view.findViewById(R.id.tvScore)
        view.findViewById<TextView>(R.id.tvRule).text = getString(ruleTextRes)

        // 先从文件里读回上一次的棋局，没有历史记录（或读取失败）才重新发牌
        val restored = loadData()
        game = restored ?: CardMatchingGame(initialCardCount, cardsToMatch)

        val columns =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LANDSCAPE_COLUMNS
            } else {
                PORTRAIT_COLUMNS
            }
        cardAdapter = CardAdapter(game, columns, this)
        recyclerCards.layoutManager = GridLayoutManager(requireContext(), columns)
        recyclerCards.adapter = cardAdapter
        recyclerCards.setHasFixedSize(true)

        view.findViewById<MaterialButton>(R.id.btnReset).setOnClickListener {
            game.reset()
            allMatchedToastShown = false
            updateUi()
        }

        updateUi()
    }

    /** 适配器回调：用户点了第 position 张牌。 */
    override fun onCardClicked(position: Int) {
        game.chooseCardAtIndex(position)
        updateUi()

        if (game.isAllMatched() && !allMatchedToastShown) {
            allMatchedToastShown = true
            Toast.makeText(requireContext(), R.string.msg_all_matched, Toast.LENGTH_SHORT).show()
        }
    }

    /** 分数变化后刷新牌面与得分，并顺便记录最高分。 */
    protected fun updateUi() {
        if (!::cardAdapter.isInitialized) {
            return
        }
        cardAdapter.notifyDataSetChanged()
        tvScore.text = getString(R.string.score_format, game.score)
        PlayerStore.noteScore(game.score)
    }

    override fun onShownAgain() {
        updateUi()
    }

    /** 离开界面（切到后台、退出程序）时把棋局写入文件。 */
    override fun onStop() {
        super.onStop()
        saveData()
        PlayerStore.save(requireContext())
    }

    private fun saveData() {
        try {
            requireContext().openFileOutput(dataFileName, Context.MODE_PRIVATE).use { output ->
                ObjectOutputStream(output).use { objectOutputStream ->
                    objectOutputStream.writeObject(game)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadData(): CardMatchingGame? {
        return try {
            requireContext().openFileInput(dataFileName).use { input ->
                ObjectInputStream(input).use { objectInputStream ->
                    objectInputStream.readObject() as CardMatchingGame
                }
            }
        } catch (e: Exception) {
            // 第一次运行时文件还不存在，属于正常情况
            null
        }
    }

    companion object {
        /** 竖屏 4 列、横屏 6 列。 */
        private const val PORTRAIT_COLUMNS = 4
        private const val LANDSCAPE_COLUMNS = 6
    }
}
