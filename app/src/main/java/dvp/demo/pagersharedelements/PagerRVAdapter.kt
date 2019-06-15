package dvp.demo.pagersharedelements

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dvp.demo.pagersharedelements.Data.fakeData

class PagerRVAdapter(val activity: Fragment, private val pages: List<Int>) :
    RecyclerView.Adapter<PagerRVAdapter.ViewHolder>() {

    private val pageScrolledPosition = mutableMapOf<Int, Int>()

    init {
        pageScrolledPosition.apply {
            pages.forEach { page ->
                this[page] = 0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val li = LayoutInflater.from(parent.context)
        val itemView = li.inflate(R.layout.layout_recycler_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = pages[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)

        init {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    pageScrolledPosition[adapterPosition] = dy
                }
            })
        }

        fun bind(page: Int) {
            val adapter = RVAdapter(fragment = activity, list = fakeData(), page = page)
            adapter.setItemClick { index, v ->
                val fragment = FragmentTwo.newInstance(page)
                FragmentOne.currentPosition = index
                activity.requireFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true) // Optimize for shared element transition
                    .addSharedElement(v, v.transitionName)
                    .replace(R.id.container, fragment, FragmentTwo::class.java.simpleName)
                    .addToBackStack(null)
                    .commit()
            }

            recyclerView.apply {
                recyclerView.layoutManager = GridLayoutManager(activity.context, 4)
                recyclerView.adapter = adapter
            }

            scrollToPosition(recyclerView)
            prepareTransitions(recyclerView)
            Log.d("TEST", "Page $adapterPosition - dy: ${pageScrolledPosition[adapterPosition]!!}")
        }

        private fun scrollToPosition(recyclerView: RecyclerView) {
            recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    recyclerView.removeOnLayoutChangeListener(this)
                    val layoutManager = recyclerView.layoutManager
                    val viewAtPosition = layoutManager!!.findViewByPosition(FragmentOne.currentPosition)
                    if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
                        recyclerView.post {
                            Log.d("TEST", "One >> Scroll to ${FragmentOne.currentPosition}")
                            layoutManager.scrollToPosition(FragmentOne.currentPosition)
                        }
                    }
                }
            })
        }

        private fun prepareTransitions(rv: RecyclerView) {
//        activity.exitTransition = TransitionInflater.from(activity.context)
//            .inflateTransition(R.transition.exit_transition)
            activity.setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(
                        names: MutableList<String>,
                        sharedElements: MutableMap<String, View>
                    ) {
                        val viewHolder = rv.findViewHolderForAdapterPosition(FragmentOne.currentPosition) ?: return
                        val element = viewHolder.itemView
                        sharedElements[names[0]] = element
                        Log.d(
                            "TEST",
                            "One >> Page: $adapterPosition - Back: ${FragmentOne.currentPosition} - Name: ${element.transitionName}"
                        )
                    }
                })
        }
    }


}