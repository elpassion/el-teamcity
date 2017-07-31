package pl.elpassion.eltc.details.viewholder

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.test_item.view.*
import pl.elpassion.eltc.R
import pl.elpassion.eltc.TestDetails

class TestDetailsViewHolder(itemView: View) : ViewHolderBinder<Any>(itemView) {

    override fun bind(item: Any) = with(item as TestDetails) {
        itemView.testName.text = item.name
        itemView.testStatusBg.setImageResource(getTestStatusBgResId(item))
        itemView.testStatusIcon.setImageResource(getTestStatusIconResId(item))
    }

    private fun getTestStatusBgResId(test: TestDetails) = when (test.status) {
        "SUCCESS" -> R.drawable.test_success_bg
        "FAILURE" -> R.drawable.test_failure_bg
        else -> R.drawable.test_ignored_bg
    }

    private fun getTestStatusIconResId(test: TestDetails) = when (test.status) {
        "SUCCESS" -> R.drawable.ic_success
        "FAILURE" -> R.drawable.ic_failure
        else -> R.drawable.ic_ignored
    }
}