package edu.hitsz.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.hitsz.R;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private List<ScoreRecord> scoreRecords;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDelete(ScoreRecord record, int position);
    }

    public RankAdapter(List<ScoreRecord> scoreRecords) {
        this.scoreRecords = scoreRecords;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void updateData(List<ScoreRecord> newData) {
        this.scoreRecords = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rank_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScoreRecord record = scoreRecords.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvNameScore.setText(String.format("%s - %d", record.getUserName(), record.getScore()));
        holder.tvTime.setText(record.getRecordTime().format(formatter));
        
        // 设置文字颜色
        if (position == 0) holder.tvRank.setTextColor(0xFFFFD700);
        else if (position == 1) holder.tvRank.setTextColor(0xFFC0C0C0);
        else if (position == 2) holder.tvRank.setTextColor(0xFFCD7F32);
        else holder.tvRank.setTextColor(0xFFFFFFFF);
        
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(record, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scoreRecords.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank;
        TextView tvNameScore;
        TextView tvTime;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvNameScore = itemView.findViewById(R.id.tv_name_score);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
