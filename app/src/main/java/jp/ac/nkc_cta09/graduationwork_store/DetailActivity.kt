package jp.ac.nkc_cta09.graduationwork_store

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class DetailActivity : AppCompatActivity() {

    private lateinit var addMemoLauncher: ActivityResultLauncher<Intent>
    private lateinit var database: DatabaseReference
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var memoList: MutableList<Memo>
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = intent.getStringExtra("barcode_value") ?: return
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val phoneTextView = findViewById<TextView>(R.id.phoneTextView)
        val buttonBackHome = findViewById<Button>(R.id.buttonBackHome)
        val Addmemobtn = findViewById<Button>(R.id.Addmemobtn)

        database = FirebaseDatabase.getInstance().reference

        // 名前取得
        database.child("users").child(userId).child("name").get()
            .addOnSuccessListener {
                nameTextView.text = "名前: ${it.getValue(String::class.java) ?: "データが見つかりません"}"
            }
            .addOnFailureListener {
                nameTextView.text = "名前: データ取得失敗"
            }

        // 電話番号取得
        database.child("users").child(userId).child("phone").get()
            .addOnSuccessListener {
                phoneTextView.text = "電話番号: ${it.getValue(String::class.java) ?: "データが見つかりません"}"
            }
            .addOnFailureListener {
                phoneTextView.text = "電話番号: データ取得失敗"
            }

        // RecyclerView の準備
        val memoRecyclerView = findViewById<RecyclerView>(R.id.memoRecyclerView)
        memoRecyclerView.layoutManager = LinearLayoutManager(this)

        memoList = mutableListOf()
        memoAdapter = MemoAdapter(
            memoList,
            onEdit = { memo, memoId ->
                val input = EditText(this)
                input.setText(memo.memo)
                AlertDialog.Builder(this)
                    .setTitle("メモを編集")
                    .setView(input)
                    .setPositiveButton("保存") { _, _ ->
                        val newText = input.text.toString()
                        database.child("users").child(userId).child("notes").child(memoId)
                            .child("memo").setValue(newText)
                            .addOnSuccessListener {
                                Toast.makeText(this, "編集しました", Toast.LENGTH_SHORT).show()
                                loadMemos()
                            }
                    }
                    .setNegativeButton("キャンセル", null)
                    .show()
            },
            onDelete = { memoId ->
                AlertDialog.Builder(this)
                    .setTitle("削除しますか？")
                    .setPositiveButton("削除") { _, _ ->
                        database.child("users").child(userId).child("notes").child(memoId)
                            .removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show()
                                loadMemos()
                            }
                    }
                    .setNegativeButton("キャンセル", null)
                    .show()
            }
        )
        memoRecyclerView.adapter = memoAdapter

        // 初回読み込み
        loadMemos()

        // AddMemoActivity から戻ってきたときの処理
        addMemoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                loadMemos()
            }
        }

        Addmemobtn.setOnClickListener {
            val intent = Intent(this, AddMemoActivity::class.java)
            intent.putExtra("userId", userId)
            addMemoLauncher.launch(intent)
        }

        buttonBackHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun loadMemos() {
        database.child("users").child(userId).child("notes").get()
            .addOnSuccessListener { dataSnapshot ->
                memoList.clear()
                for (memoSnapshot in dataSnapshot.children) {
                    val memo = memoSnapshot.getValue(Memo::class.java)
                    val id = memoSnapshot.key
                    if (memo != null && id != null) {
                        memoList.add(memo.copy(id = id))
                    }
                }
                memoList.reverse()
                memoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "メモの取得失敗", Toast.LENGTH_SHORT).show()
            }
    }
}
