package pl.elpassion.eltc.details.viewholder

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.change_item.view.*
import pl.elpassion.eltc.Change
import pl.elpassion.eltc.details.util.toTime

class ChangeViewHolder(itemView: View) : ViewHolderBinder<Any>(itemView) {

    override fun bind(item: Any) = with(item as Change) {
        itemView.buildVersion.text = item.version.take(7)
        itemView.buildAuthor.text = item.username
        itemView.buildTime.text = item.date.toTime()
        itemView.buildComment.text = item.comment
    }
}