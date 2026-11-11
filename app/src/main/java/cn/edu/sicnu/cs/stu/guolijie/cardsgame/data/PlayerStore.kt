package cn.edu.sicnu.cs.stu.guolijie.cardsgame.data

import android.content.Context
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.R
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 玩家的个人信息（实验六新增，可序列化）。
 * 与实验四、实验五只放在内存里不同，本次实验要求个人信息也写入文件，
 * 所以把资料抽成一个实现了 Serializable 的数据类，整个对象一次写出、一次读回。
 */
class PlayerProfile(
    var name: String = "",
    var gender: String = "",
    var studentId: String = "",
    var phone: String = "",
    var email: String = "",
    var hobbies: MutableList<String> = mutableListOf(),
    var highScore: Int = 0
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 个人信息的内存 + 文件存储（单例）。
 *
 * 写入：context.openFileOutput(FILE_NAME, MODE_PRIVATE) + ObjectOutputStream.writeObject()
 * 读取：context.openFileInput(FILE_NAME) + ObjectInputStream.readObject()
 *
 * 三个 Fragment 共享同一份 PlayerProfile，界面刷新时直接读这个单例即可。
 */
object PlayerStore {

    /** 保存个人信息的文件名。 */
    private const val FILE_NAME = "player_profile.dat"

    var profile: PlayerProfile = PlayerProfile()
        private set

    private var initialized = false

    /**
     * 第一次使用时先从文件读取；文件不存在（或读取失败）时用默认资料，
     * 并把默认资料写入文件。
     */
    fun initIfNeeded(context: Context) {
        if (initialized) {
            return
        }
        val restored = read(context)
        profile = restored ?: defaultProfile(context)
        initialized = true
        if (restored == null) {
            save(context)
        }
    }

    /** 把当前资料序列化写入文件。 */
    fun save(context: Context) {
        try {
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { output ->
                ObjectOutputStream(output).use { objectOutputStream ->
                    objectOutputStream.writeObject(profile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 从文件反序列化读取资料；文件不存在或格式不对时返回 null。 */
    fun read(context: Context): PlayerProfile? {
        return try {
            context.openFileInput(FILE_NAME).use { input ->
                ObjectInputStream(input).use { objectInputStream ->
                    objectInputStream.readObject() as PlayerProfile
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** 「还原」按钮：丢弃内存中的修改，重新从文件读取。 */
    fun reload(context: Context) {
        val restored = read(context)
        profile = restored ?: defaultProfile(context)
    }

    /** 修改资料（Fragment 里直接把输入值写回来），并立即写入文件。 */
    fun update(context: Context, newProfile: PlayerProfile) {
        profile = newProfile
        save(context)
    }

    /** 记录最高分（只改内存，随 save() 一起写入文件）。 */
    fun noteScore(score: Int) {
        if (score > profile.highScore) {
            profile.highScore = score
        }
    }

    /** 用字符串资源里的默认资料初始化。 */
    fun defaultProfile(context: Context): PlayerProfile {
        return PlayerProfile(
            name = context.getString(R.string.default_name),
            gender = context.getString(R.string.gender_male),
            studentId = context.getString(R.string.default_student_id),
            phone = context.getString(R.string.default_phone),
            email = context.getString(R.string.default_email),
            hobbies = mutableListOf(
                context.getString(R.string.hobby_travel),
                context.getString(R.string.hobby_reading)
            ),
            highScore = 0
        )
    }

    /** 仅供单元测试 / 界面测试使用：恢复成未初始化的状态。 */
    fun resetForTest() {
        initialized = false
        profile = PlayerProfile()
    }
}
