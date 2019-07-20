package com.stocks.cluelesscloset.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.stocks.cluelesscloset.Endpoints.BASEURL
import com.stocks.cluelesscloset.POKO.Bottom
import com.stocks.cluelesscloset.R

/**
 * Adapter for displaying the user's indexed clothing.
 */
class BottomAdapter(private val dataList: MutableList<Bottom>, val context: Context): RecyclerView.Adapter<BottomAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: BottomAdapter.ViewHolder?, position: Int) {
        Picasso.with(context)
                .load("$BASEURL/clothes_images/${dataList[position].image}")
                .placeholder(R.drawable.ic_clothes_dark)
                .error(R.drawable.ic_err)
                .fit()
                .into(holder?.image_icon)

        holder?.clothing_label?.text = dataList[position].name
        holder?.trash_row?.setOnClickListener {
            removeItem(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent?.context)
                        .inflate(R.layout.new_clothing_row, parent, false))
    }

    /**
     * Fun custom ViewHolder
     */
    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        /**
         * Icon of image.
         */
        var image_icon: ImageView? = null
        /**
         * Label for clothing.
         */
        var clothing_label: TextView? = null
        /**
         * Icon to used to trash a row quickly.
         */
        var trash_row: ImageView? = null

        /**
         * Extra init to bind views to viewholder
         * Kotlin synthetic methods do not seem to work outside of fragments or activities.
         */
        init {
            image_icon = itemView?.findViewById(R.id.image_icon)
            clothing_label = itemView?.findViewById(R.id.clothing_label)
            trash_row = itemView?.findViewById(R.id.trash_row)
        }
    }

    /**
     * Helper method: Removes item from List.
     */
    private fun removeItem(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }
}