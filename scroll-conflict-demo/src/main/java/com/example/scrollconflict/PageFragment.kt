package com.example.scrollconflict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.scrollconflict.databinding.FragmentPageBinding

/**
 * Simple fragment to display a page number in ViewPager2.
 * 
 * This fragment is used in the opposite direction conflict demo to show
 * horizontal paging within a vertically scrollable container.
 */
class PageFragment : Fragment() {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get page number from arguments and display it
        val pageNumber = arguments?.getInt(ARG_PAGE_NUMBER, 0) ?: 0
        binding.textPageNumber.text = (pageNumber + 1).toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PAGE_NUMBER = "page_number"

        /**
         * Creates a new instance of PageFragment with the specified page number.
         * 
         * @param pageNumber The zero-based page index
         * @return A new PageFragment instance
         */
        fun newInstance(pageNumber: Int): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PAGE_NUMBER, pageNumber)
                }
            }
        }
    }
}
