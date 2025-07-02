package jp.ac.nkc_cta09.graduationwork_store

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddMemoActivity : AppCompatActivity() {

    private lateinit var editTextStyle: EditText
    private lateinit var editTextLength: EditText
    private lateinit var spinnerPurpose: Spinner
    private lateinit var editTextStaff: EditText
    private lateinit var editTextMemo: EditText
    private lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memo)

        val userId = intent.getStringExtra("userId") ?: return

        editTextStyle = findViewById(R.id.editTextStyle)
        editTextLength = findViewById(R.id.editTextLength)
        spinnerPurpose = findViewById(R.id.spinnerPurpose)
        editTextStaff = findViewById(R.id.editTextStaff)
        editTextMemo = findViewById(R.id.editTextMemo)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        // スピナーに選択肢を設定
        val purposes = arrayOf("カットのみ", "カット＋カラー", "パーマ", "トリートメント")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, purposes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPurpose.adapter = adapter

        buttonSubmit.setOnClickListener {
            val style = editTextStyle.text.toString().trim()
            val length = editTextLength.text.toString().trim()
            val purpose = spinnerPurpose.selectedItem.toString()
            val staff = editTextStaff.text.toString().trim()
            val memo = editTextMemo.text.toString().trim()
            val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

            val memoData = mapOf(
                "date" to date,
                "style" to style,
                "length" to length,
                "purpose" to purpose,
                "staff" to staff,
                "memo" to memo
            )

            val database = FirebaseDatabase.getInstance().reference
            database.child("users").child(userId).child("notes").push().setValue(memoData)
                .addOnSuccessListener {
                    Toast.makeText(this, "メモ追加完了", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish() // 画面を閉じて前の画面に戻る
                }
                .addOnFailureListener {
                    Toast.makeText(this, "保存失敗", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
