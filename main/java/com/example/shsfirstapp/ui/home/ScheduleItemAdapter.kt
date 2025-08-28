import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shsfirstapp.R
import com.example.shsfirstapp.data.Alarm

class ScheduleItemAdapter(
    private val schedules: List<Alarm>,
    private val onDeleteClick: (Alarm) -> Unit
) : RecyclerView.Adapter<ScheduleItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(alarm: Alarm) {
            itemView.findViewById<TextView>(R.id.textEvent).text = alarm.event
            itemView.findViewById<TextView>(R.id.textTime).text = alarm.time
            itemView.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
                onDeleteClick(alarm)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    override fun getItemCount() = schedules.size
}

