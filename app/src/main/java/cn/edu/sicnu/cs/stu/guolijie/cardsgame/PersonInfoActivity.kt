package cn.edu.sicnu.cs.stu.guolijie.cardsgame

import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.guolijie.cardsgame.data.PlayerStore
import com.google.android.material.button.MaterialButton

/**
 * 信息修改 Activity：编辑姓名、学号、电话、邮箱、性别和爱好。
 * 按实验要求，修改后的资料只保存在内存中，不写入文件。
 */
class PersonInfoActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var cbTravel: CheckBox
    private lateinit var cbReading: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShopping: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        applyContentInsets(findViewById(R.id.root))

        PlayerStore.initIfNeeded(this)
        bindViews()
        showPlayerInfo()

        findViewById<ImageButton>(R.id.btnCamera).setOnClickListener {
            Toast.makeText(this, R.string.msg_camera_demo, Toast.LENGTH_SHORT).show()
        }
        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { savePlayerInfo() }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun bindViews() {
        etName = findViewById(R.id.etName)
        etStudentId = findViewById(R.id.etStudentId)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        rgGender = findViewById(R.id.rgGender)
        cbTravel = findViewById(R.id.cbTravel)
        cbReading = findViewById(R.id.cbReading)
        cbGame = findViewById(R.id.cbGame)
        cbShopping = findViewById(R.id.cbShopping)
    }

    private fun showPlayerInfo() {
        etName.setText(PlayerStore.name)
        etStudentId.setText(PlayerStore.studentId)
        etPhone.setText(PlayerStore.phone)
        etEmail.setText(PlayerStore.email)

        val isFemale = PlayerStore.gender == getString(R.string.gender_female)
        rgGender.check(if (isFemale) R.id.rbFemale else R.id.rbMale)

        cbTravel.isChecked = PlayerStore.hobbies.contains(getString(R.string.hobby_travel))
        cbReading.isChecked = PlayerStore.hobbies.contains(getString(R.string.hobby_reading))
        cbGame.isChecked = PlayerStore.hobbies.contains(getString(R.string.hobby_game))
        cbShopping.isChecked = PlayerStore.hobbies.contains(getString(R.string.hobby_shopping))
    }

    private fun savePlayerInfo() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.msg_name_empty, Toast.LENGTH_SHORT).show()
            return
        }

        PlayerStore.name = name
        PlayerStore.studentId = etStudentId.text.toString().trim()
        PlayerStore.phone = etPhone.text.toString().trim()
        PlayerStore.email = etEmail.text.toString().trim()
        PlayerStore.gender = getString(
            if (rgGender.checkedRadioButtonId == R.id.rbFemale) {
                R.string.gender_female
            } else {
                R.string.gender_male
            }
        )

        PlayerStore.hobbies.clear()
        if (cbTravel.isChecked) {
            PlayerStore.hobbies.add(getString(R.string.hobby_travel))
        }
        if (cbReading.isChecked) {
            PlayerStore.hobbies.add(getString(R.string.hobby_reading))
        }
        if (cbGame.isChecked) {
            PlayerStore.hobbies.add(getString(R.string.hobby_game))
        }
        if (cbShopping.isChecked) {
            PlayerStore.hobbies.add(getString(R.string.hobby_shopping))
        }

        Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}
