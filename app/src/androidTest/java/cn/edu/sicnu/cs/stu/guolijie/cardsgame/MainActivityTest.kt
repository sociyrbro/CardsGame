package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 界面（UI）测试：启动 Activity 后检查控件是否按要求显示，
 * 并模拟“进入游戏”“修改资料并保存”两条主要交互路径。
 */
@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val context = instrumentation.targetContext
    private val packageName = context.packageName

    @Before
    fun resetPlayer() {
        PlayerStore.reset()
    }

    @Test
    fun mainScreen_showsPlayerInfoAndBothButtons() {
        ActivityScenario.launch(MainActivity::class.java).use {
            val name = context.getString(R.string.default_name)
            val studentId = context.getString(R.string.default_student_id)

            assertTrue(
                "主界面应显示姓名",
                device.wait(Until.hasObject(By.text(name)), 5000)
            )
            assertTrue(
                "主界面应显示学号",
                device.wait(Until.hasObject(By.text(studentId)), 5000)
            )
            assertNotNull(device.findObject(By.res(packageName, "btnEditInfo")))
            assertNotNull(device.findObject(By.res(packageName, "btnCardGame")))
        }
    }

    @Test
    fun cardGameButton_opensGameScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            device.findObject(By.res(packageName, "btnCardGame")).click()

            assertTrue(
                "点击“翻牌游戏”后应进入游戏界面",
                device.wait(Until.hasObject(By.res(packageName, "gridCards")), 5000)
            )
            assertTrue(
                "新开一局时得分应为 0",
                device.wait(
                    Until.hasObject(By.text(context.getString(R.string.score_format, 0))),
                    5000
                )
            )
        }
    }

    @Test
    fun editInfoAndSave_updatesMainScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            device.findObject(By.res(packageName, "btnEditInfo")).click()

            val nameBox = device.wait(
                Until.findObject(By.res(packageName, "etName")),
                5000
            )
            assertNotNull("应打开信息修改界面", nameBox)

            nameBox.text = "郭理杰"
            device.findObject(By.res(packageName, "btnSave")).click()

            assertTrue(
                "保存后主界面应显示新的姓名",
                device.wait(Until.hasObject(By.text("郭理杰")), 5000)
            )
        }
    }

    @Test
    fun resetButton_keepsScoreAtZero() {
        ActivityScenario.launch(MainActivity::class.java).use {
            device.findObject(By.res(packageName, "btnCardGame")).click()
            device.wait(Until.hasObject(By.res(packageName, "btnReset")), 5000)

            device.findObject(By.res(packageName, "btnReset")).click()

            assertTrue(
                "重置后得分应回到 0",
                device.wait(
                    Until.hasObject(By.text(context.getString(R.string.score_format, 0))),
                    5000
                )
            )
        }
    }

    @Test
    fun exitButton_returnsToMainScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            device.findObject(By.res(packageName, "btnCardGame")).click()
            device.wait(Until.hasObject(By.res(packageName, "btnExit")), 5000)

            device.findObject(By.res(packageName, "btnExit")).click()

            assertTrue(
                "点击退出后应回到主界面",
                device.wait(Until.hasObject(By.res(packageName, "btnEditInfo")), 5000)
            )
        }
    }
}
