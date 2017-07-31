package pl.elpassion.eltc.details.viewholder

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.section_item.view.*
import pl.elpassion.eltc.details.DetailsSection

class SectionViewHolder(itemView: View) : ViewHolderBinder<Any>(itemView) {

    override fun bind(item: Any) = with(item as DetailsSection) {
        itemView.sectionName.text = item.name
    }
}