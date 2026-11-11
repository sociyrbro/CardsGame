package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 界面（UI）测试：启动唯一的 MainActivity 后检查三个 Fragment 是否都能正确显示，
 * 底部导航栏能否在三个页面之间切换，以及「保存」按钮是否把资料写进了文件。
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val context = instrumentation.targetContext
    private val packageName = context.packageName

    @Before
    fun resetPlayer() {
        PlayerStore.resetForTest()
        context.deleteFile("player_profile.dat")
    }

    @Test
    fun defaultTab_showsPersonInfoFragment() {
        ActivityScenario.launch(MainActivity::class.java).use {
            assertTrue(
                "默认应显示个人信息 Fragment 的姓名输入框",
                device.wait(Until.hasObject(By.res(packageName, "etName")), 5000)
            )
            assertTrue(
                "个人信息 Fragment 上应显示最高分",
                device.wait(Until.hasObject(By.res(packageName, "tvHighScore")), 5000)
            )
            assertNotNull(
                "底部导航栏应存在",
                device.findObject(By.res(packageName, "bottomNav"))
            )
        }
    }

    @Test
    fun bottomNav_switchesBetweenThreeFragments() {
        ActivityScenario.launch(MainActivity::class.java).use {
            device.findObject(By.res(packageName, "nav_two")).click()
            assertTrue(
                "切换到两张牌 Fragment 后应显示牌桌",
                device.wait(Until.hasObject(By.res(packageName, "recyclerCards")), 5000)
            )
            assertTrue(
                "两张牌 Fragment 应显示得分",
                device.wait(Until.hasObject(By.res(packageName, "tvScore")), 5000)
            )

            device.findObject(By.res(packageName, "nav_three")).click()
            assertTrue(
                "切换到三张牌 Fragment 后应显示重置按钮",
                device.wait(Until.hasObject(By.res(packageName, "btnReset")), 5000)
            )

            device.findObject(By.res(packageName, "nav_person")).click()
            assertTrue(
                "切换回个人信息 Fragment 后应显示姓名输入框",
                device.wait(Until.hasObject(By.res(packageName, "etName")), 5000)
            )
        }
    }

    @Test
    fun saveButton_writesProfileToFile() {
        ActivityScenario.launch(MainActivity::class.java).use {
            assertTrue(device.wait(Until.hasObject(By.res(packageName, "btnSave")), 5000))
            device.findObject(By.res(packageName, "btnSave")).click()

            val restored = PlayerStore.read(context)
            assertNotNull("点击保存后 player_profile.dat 中应能读出资料", restored)
            assertEquals(
                context.getString(R.string.default_name),
                restored!!.name
            )
            assertEquals(
                context.getString(R.string.default_student_id),
                restored.studentId
            )
        }
    }
}
