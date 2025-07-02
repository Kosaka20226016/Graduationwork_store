package jp.ac.nkc_cta09.graduationwork_store

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_notice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val titleEditText: EditText = findViewById(R.id.editTextNoticeTitle)
        val bodyEditText: EditText = findViewById(R.id.editTextNoticeBody)
        val postButton: Button = findViewById(R.id.buttonPostNotice)

        postButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val body = bodyEditText.text.toString().trim()
            val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

            if(title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "すべての項目を入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val notice = Notice(title,body,date)

            val database = FirebaseDatabase.getInstance().reference
            database.child("notices").push().setValue(notice)
                .addOnSuccessListener {
                    Toast.makeText(this,"お知らせを投稿しました",Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"投稿に失敗しました",Toast.LENGTH_SHORT).show()
                }
        }
    }
}