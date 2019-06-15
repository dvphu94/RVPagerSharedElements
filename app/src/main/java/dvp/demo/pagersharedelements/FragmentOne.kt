package dvp.demo.pagersharedelements


import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_fragment_one.*


class FragmentOne : Fragment() {

    companion object {
        var currentPosition = 0
    }

    private val pagesState = Bundle()

    private var prePage = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()
        postponeEnterTransition()
    }


    private fun setupRV() {
        val snapHelper = CustomSnapHelper{
            Log.d("TEST", "Current Page $it - Previous Page: $prePage")
            if (prePage != it)
                restorePageState(it)
            savePageState(prePage)
            prePage = it
        }
        snapHelper.attachToRV(pagerView)

        val adapter = PagerRVAdapter(this, arrayListOf(0, 1, 2))
        pagerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        pagerView.adapter = adapter
        pagerView.post {
            setDefaultState()
        }
    }

    private fun setDefaultState() {
        val viewHolder = pagerView.findViewHolderForAdapterPosition(0) ?: return
        val layoutManager = (viewHolder.itemView as RecyclerView).layoutManager!!
        val state = layoutManager.onSaveInstanceState() ?: return
        pagesState.putParcelable("default", state)
        Log.d("TEST", "Save default state")
    }

    private fun savePageState(page: Int) {
        val viewHolder = pagerView.findViewHolderForAdapterPosition(page) ?: return
        val layoutManager = (viewHolder.itemView as RecyclerView).layoutManager!!
        Log.d("TEST", "On Save Sate Page $page")
        val state = layoutManager.onSaveInstanceState() ?: return
        pagesState.putParcelable("pageState$page", state)
    }

    private fun restorePageState(page: Int) {
        val viewHolder = pagerView.findViewHolderForAdapterPosition(page) ?: return
        val layoutManager = (viewHolder.itemView as RecyclerView).layoutManager!!
        val stored = pagesState.getParcelable<Parcelable>("pageState$page")
        val default = pagesState.getParcelable<Parcelable>("default")

        Log.d("TEST", "On Restore Sate Page $page -- stored: $stored -- default $default")

        val state = pagesState.getParcelable<Parcelable>("pageState$page") ?: pagesState.getParcelable("default")!!
        layoutManager.onRestoreInstanceState(state)
    }
}
