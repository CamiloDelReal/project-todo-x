package org.xapps.apps.todox.views.adapters
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.recyclerview.widget.RecyclerView
//import kotlinx.android.synthetic.main.item_summary.view.*
//import org.xapps.apps.mangaxdatabase.R
//import org.xapps.apps.mangaxdatabase.databinding.ItemSummaryBinding
//import org.xapps.apps.todox.R
//
//
//class SummaryAdapter (
//    private val listener: Listener
//) : RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {
//
//    var references: ArrayList<ReferenceItemSummary> = ArrayList()
//
//    interface Listener {
//        fun clicked(referenceId: Long)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
//        val inflatedView: ItemSummaryBinding =
//            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_summary, parent, false)
//        return SummaryViewHolder(inflatedView)
//    }
//
//    fun add(references: List<ReferenceItemSummary>) {
//        this.references.addAll(references)
//        notifyDataSetChanged()
//    }
//
//    fun replaceDevices(references: List<ReferenceItemSummary>) {
//        this.references.clear()
//        this.references.addAll(references)
//        notifyDataSetChanged()
//    }
//
//    fun clear() {
//        this.references.clear()
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount() = references.size
//
//    override fun onBindViewHolder(holder: SummaryViewHolder, index: Int) {
//        holder.bindInfo(references[index], listener)
//    }
//
//    class SummaryViewHolder(
//        private val itemBinding: ItemSummaryBinding
//    ) : RecyclerView.ViewHolder(itemBinding.root) {
//
//        private var referenceWrapper: ReferenceItemSummary? = null
//
//        fun bindInfo(referenceWrapper: ReferenceItemSummary, listener: Listener) {
//            this.referenceWrapper = referenceWrapper
//            itemBinding.referenceWrapper = referenceWrapper
//            itemBinding.root.btnItem.setOnClickListener {
//                listener.clicked(this.referenceWrapper!!.reference.id)
//            }
//        }
//    }
//}