package jp.ac.nkc_cta09.graduationwork_store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase

class MemoAdapter(
    private val memoList: List<Memo>,
    private val onEdit: (Memo, String) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewStyle: TextView = itemView.findViewById(R.id.textViewStyle)
        val textViewLength: TextView = itemView.findViewById(R.id.textViewLength)
        val textViewPurpose: TextView = itemView.findViewById(R.id.textViewPurpose)
        val textViewStaff: TextView = itemView.findViewById(R.id.textViewStaff)
        val textViewMemo: TextView = itemView.findViewById(R.id.textViewMemo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]

        holder.textViewDate.text = "日付: ${memo.date}"
        holder.textViewStyle.text = "髪型: ${memo.style}"
        holder.textViewLength.text = "何ミリ: ${memo.length}"
        holder.textViewPurpose.text = "施術内容: ${memo.purpose}"
        holder.textViewStaff.text = "担当者: ${memo.staff}"
        holder.textViewMemo.text = "メモ: ${memo.memo}"

        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("メモ操作")
                .setItems(arrayOf("編集", "削除")) { _, which ->
                    val memoId = memo.id ?: return@setItems
                    when (which) {
                        0 -> onEdit(memo, memoId)
                        1 -> onDelete(memoId)
                    }
                }
                .show()
            true
        }
    }

    override fun getItemCount(): Int = memoList.size
}
