package hu.bme.aut.pictureteam.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.pictureteam.R
import kotlinx.android.synthetic.main.list_view.view.*
import kotlinx.android.synthetic.main.recycler_view.view.*

class ListView : Fragment() {
    private lateinit var pageViewModel: PageViewModel

    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.list_view, container, false)
        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
//            textView.text = it
        })

        root.btnSearch.setOnClickListener {
            Toast.makeText(context,"Not yet implemented!", Toast.LENGTH_SHORT).show()
            //TODO("Not yet implemented!")
        }

        root.recyclerview

        return root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(): ListView {
            return ListView().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, 1)
                }
            }
        }
    }
}