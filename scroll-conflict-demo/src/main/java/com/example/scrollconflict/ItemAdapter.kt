package com.example.scrollconflict

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scrollconflict.databinding.ItemRecyclerBinding

/**
 * RecyclerView adapter for displaying demo items in the same direction conflict scene.
 * 
 * This adapter creates a list of items to demonstrate vertical scrolling within
 * a vertically scrollable parent container.
 * 
 * @param itemCount The number of items to display (default: 20)
 */
class ItemAdapter(
    private val itemCount: Int = 20
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    /**
     * ViewHolder for RecyclerView items using View Binding.
     * 
     * @param binding The view binding for item_recycler.xml layout
     */
    class ViewHolder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Creates a new ViewHolder by inflating the item layout.
     * 
     * @param parent The parent ViewGroup
     * @param viewType The view type (unused in this simple adapter)
     * @return A new ViewHolder instance
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     * 
     * Displays the item title and position number to help users track
     * scrolling progress during the demo.
     * 
     * @param holder The ViewHolder to bind data to
     * @param position The position in the adapter
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            textItemTitle.text = "Item ${position + 1}"
            textItemPosition.text = "Position: $position"
        }
    }

    /**
     * Returns the total number of items in the adapter.
     * 
     * @return The item count
     */
    override fun getItemCount(): Int = itemCount
}
