package com.armijoruiz.alberto.mykotlinapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.armijoruiz.alberto.mykotlinapp.R


/**
 * A simple [Fragment] subclass.
 * Use the [FabFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FabFragment : Fragment() {
    private var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = it.getString("PATH")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fab, container, false)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment FabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                FabFragment().apply {
                    arguments = Bundle().apply {
                        putString("PATH", param1)
                    }
                }
    }
}
