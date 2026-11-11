package cn.edu.sicnu.cs.stu.guolijie.cardsgame.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.R
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerProfile
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import com.google.android.material.button.MaterialButton

/**
 * 第 1 个 Fragment：个人信息（实验六要求）。
 *
 * 与实验四、实验五的 PersonInfoActivity 相比，两个变化：
 *   1. 页面从 Activity 改成 Fragment，由 MainActivity 的底部导航栏切换；
 *   2. 资料不再只放在内存里，点击「保存」时用 ObjectOutputStream
 *      把 PlayerProfile 对象写入文件 player_profile.dat，重新进入程序时再读回来。
 */
class PersonInfoFragment : Fragment(), Refreshable {

    private lateinit var etName: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var cbTravel: CheckBox
    private lateinit var cbReading: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShopping: CheckBox
    private lateinit var tvHighScore: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_person_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)

        // 第一次进入时从文件恢复资料
        PlayerStore.initIfNeeded(requireContext())
        showProfile()

        view.findViewById<ImageButton>(R.id.btnCamera).setOnClickListener {
            Toast.makeText(requireContext(), R.string.msg_camera_demo, Toast.LENGTH_SHORT).show()
        }
        view.findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { saveProfile() }
        view.findViewById<MaterialButton>(R.id.btnReload).setOnClickListener {
            // 丢弃输入框中的修改，重新从文件读取
            PlayerStore.reload(requireContext())
            showProfile()
            Toast.makeText(requireContext(), R.string.msg_reloaded, Toast.LENGTH_SHORT).show()
        }
    }

    /** 从别的 Fragment 切回来时，最高分可能已经变化，这里重新显示一次。 */
    override fun onShownAgain() {
        if (!::etName.isInitialized) {
            return
        }
        showProfile()
    }

    private fun bindViews(view: View) {
        etName = view.findViewById(R.id.etName)
        etStudentId = view.findViewById(R.id.etStudentId)
        etPhone = view.findViewById(R.id.etPhone)
        etEmail = view.findViewById(R.id.etEmail)
        rgGender = view.findViewById(R.id.rgGender)
        cbTravel = view.findViewById(R.id.cbTravel)
        cbReading = view.findViewById(R.id.cbReading)
        cbGame = view.findViewById(R.id.cbGame)
        cbShopping = view.findViewById(R.id.cbShopping)
        tvHighScore = view.findViewById(R.id.tvHighScore)
    }

    private fun showProfile() {
        val profile = PlayerStore.profile

        etName.setText(profile.name)
        etStudentId.setText(profile.studentId)
        etPhone.setText(profile.phone)
        etEmail.setText(profile.email)

        val isFemale = profile.gender == getString(R.string.gender_female)
        rgGender.check(if (isFemale) R.id.rbFemale else R.id.rbMale)

        cbTravel.isChecked = profile.hobbies.contains(getString(R.string.hobby_travel))
        cbReading.isChecked = profile.hobbies.contains(getString(R.string.hobby_reading))
        cbGame.isChecked = profile.hobbies.contains(getString(R.string.hobby_game))
        cbShopping.isChecked = profile.hobbies.contains(getString(R.string.hobby_shopping))

        tvHighScore.text = getString(R.string.high_score_format, profile.highScore)
    }

    /** 收集界面上的输入，组成一个可序列化的 PlayerProfile 并写入文件。 */
    private fun saveProfile() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msg_name_empty, Toast.LENGTH_SHORT).show()
            return
        }

        val hobbies = mutableListOf<String>()
        if (cbTravel.isChecked) {
            hobbies.add(getString(R.string.hobby_travel))
        }
        if (cbReading.isChecked) {
            hobbies.add(getString(R.string.hobby_reading))
        }
        if (cbGame.isChecked) {
            hobbies.add(getString(R.string.hobby_game))
        }
        if (cbShopping.isChecked) {
            hobbies.add(getString(R.string.hobby_shopping))
        }

        val profile = PlayerProfile(
            name = name,
            gender = getString(
                if (rgGender.checkedRadioButtonId == R.id.rbFemale) {
                    R.string.gender_female
                } else {
                    R.string.gender_male
                }
            ),
            studentId = etStudentId.text.toString().trim(),
            phone = etPhone.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            hobbies = hobbies,
            highScore = PlayerStore.profile.highScore
        )

        // 写入文件 player_profile.dat
        PlayerStore.update(requireContext(), profile)
        Toast.makeText(requireContext(), R.string.msg_saved, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(): PersonInfoFragment = PersonInfoFragment()
    }
}
