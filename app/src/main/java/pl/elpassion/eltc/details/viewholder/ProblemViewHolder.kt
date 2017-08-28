package pl.elpassion.eltc.details.viewholder

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.problem_item.view.*
import pl.elpassion.eltc.ProblemOccurrence

class ProblemViewHolder(itemView: View) : ViewHolderBinder<Any>(itemView) {

    override fun bind(item: Any) = with(item as ProblemOccurrence) {
        itemView.problemType.text = type
        itemView.problemDetails.text = details
    }
}