package cn.edu.sicnu.cs.stu.guolijie.cardsgame.data

import android.content.Context
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.R

/**
 * 玩家资料的内存存储（单例）。
 * 实验要求个人信息不写入文件，因此三个 Activity 之间通过它共享数据。
 */
object PlayerStore {

    var name: String = ""
    var gender: String = ""
    var studentId: String = ""
    var phone: String = ""
    var email: String = ""
    val hobbies: MutableList<String> = mutableListOf()
    var highScore: Int = 0

    private var initialized = false

    /** 第一次使用时用字符串资源里的默认资料初始化。 */
    fun initIfNeeded(context: Context) {
        if (initialized) {
            return
        }
        name = context.getString(R.string.default_name)
        gender = context.getString(R.string.gender_male)
        studentId = context.getString(R.string.default_student_id)
        phone = context.getString(R.string.default_phone)
        email = context.getString(R.string.default_email)
        hobbies.clear()
        hobbies.add(context.getString(R.string.hobby_travel))
        hobbies.add(context.getString(R.string.hobby_reading))
        initialized = true
    }

    /** 仅供单元测试使用：把资料恢复成默认值。 */
    fun reset() {
        initialized = false
        highScore = 0
        hobbies.clear()
    }
}
