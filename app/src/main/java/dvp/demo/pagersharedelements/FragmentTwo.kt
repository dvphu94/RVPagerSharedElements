package dvp.demo.pagersharedelements


import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import dvp.demo.pagersharedelements.Data.fakeData
import kotlinx.android.synthetic.main.fragment_fragment_two.*


class FragmentTwo : Fragment() {

    companion object {
        fun newInstance(page: Int) = FragmentTwo()
            .apply {
                val args = Bundle()
                args.putSerializable("page", page)
                arguments = args
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()
        prepareSharedElementTransition()
        postponeEnterTransition()
    }

    private fun prepareSharedElementTransition() {
        val transition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)
        sharedElementEnterTransition = transition
        setEnterSharedElementCallback(enterElementCallback)
    }

    private val enterElementCallback: SharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(FragmentOne.currentPosition) ?: return
            val element = viewHolder.itemView
            sharedElements[names[0]] = element
            Log.d("TEST", "Two >> Start: ${FragmentOne.currentPosition} - Name: ${element.transitionName}")
        }
    }

    private fun setupRV() {
        val page = arguments!!.getInt("page", 0)

        recyclerView.apply {
            setSlideOnFling(true)
            setItemTransformer(ScaleTransformer.Builder().setMinScale(0.8f).build())
            adapter = RVAdapter(this@FragmentTwo, fakeData(), page)
            recyclerView.scrollToPosition(FragmentOne.currentPosition)
            addOnItemChangedListener { _, currentPosition ->
                FragmentOne.currentPosition = currentPosition
            }
        }

        fadeOtherItems()
    }

    private fun fadeOtherItems() {
        recyclerView.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                    for (i in 1..recyclerView.childCount) {//skip center view (selected item)
                        val v = recyclerView.getChildAt(i) ?: break
                        v.alpha = 0f
                        v.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .start()
                    }
                    return true
                }
            })
    }

}
