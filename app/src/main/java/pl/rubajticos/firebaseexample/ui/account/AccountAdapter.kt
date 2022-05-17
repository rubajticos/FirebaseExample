package pl.rubajticos.firebaseexample.ui.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.rubajticos.firebaseexample.databinding.AccountListItemBinding

class AccountAdapter(
    private var items: List<AccountInfoUiItem>
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = AccountListItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun update(newItems: List<AccountInfoUiItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: AccountListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AccountInfoUiItem) {
            binding.itemLabel.text = item.label
            binding.itemValue.text = item.value
        }

    }
}

