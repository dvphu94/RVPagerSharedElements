package dvp.demo.pagersharedelements

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(
    private val fragment: Fragment,
    private val list: List<ItemModel>,
    val page: Int = -1
) :
    RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val li = LayoutInflater.from(parent.context)
        val itemView = li.inflate(R.layout.rv_item_test, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private var onItemClicked: ((Int, View) -> Unit)? = null

    fun setItemClick(onItemClicked: (Int, View) -> Unit) {
        this.onItemClicked = onItemClicked
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItem: TextView = itemView.findViewById(R.id.textView)
        private val parent: CardView = itemView.findViewById(R.id.parent)


        init {
            itemView.setOnClickListener {
                onItemClicked?.invoke(adapterPosition, it)
            }
        }

        fun bind(item: ItemModel) {
            tvItem.text = item.text
            itemView.tag = "Item_${page}_$adapterPosition"
            itemView.transitionName = "Item_${page}_$adapterPosition"

            if (adapterPosition == FragmentOne.currentPosition) {
                itemView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        itemView.viewTreeObserver.removeOnPreDrawListener(this)
                        fragment.startPostponedEnterTransition()
                        return true
                    }
                })
            }
        }

    }

}