package pl.elpassion.eltc.details.viewholder

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.tests_section_item.view.*
import pl.elpassion.eltc.R
import pl.elpassion.eltc.details.TestsSection

class TestsSectionViewHolder(itemView: View, private val onClick: (TestsSection) -> Unit)
    : ViewHolderBinder<Any>(itemView) {

    override fun bind(item: Any) = with(item as TestsSection) {
        val icon = if (item.isExpanded) R.drawable.ic_expanded else R.drawable.ic_collapsed
        itemView.sectionIcon.setImageResource(icon)
        itemView.sectionName.text = item.name
        itemView.setOnClickListener { onClick(item) }
    }
}