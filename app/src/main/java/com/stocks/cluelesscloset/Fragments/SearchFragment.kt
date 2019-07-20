package com.stocks.cluelesscloset.Fragments


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stocks.cluelesscloset.R
import kotlinx.android.synthetic.main.fragment_search.*


/**
 * Fragment used to get a quick search from the user.
 */
class SearchFragment : DialogFragment() {


    /**
     * Callback interface.
     */
    var searchCompleteListener: SearchCompleteListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        send_button.setOnClickListener {
            searchCompleteListener?.searchCompleted(search_field.text.toString())
            dismiss()
        }
    }

    /**
     * Callback interface.
     */
    public interface SearchCompleteListener {
        fun searchCompleted(s: String)
    }
}
