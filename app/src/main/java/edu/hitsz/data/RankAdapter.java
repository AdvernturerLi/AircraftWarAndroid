package edu.hitsz.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.hitsz.R;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private List<ScoreRecord> scoreRecords;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RankAdapter(List<ScoreRecord> scoreRecords) {
        this.scoreRecords = scoreRecords;
    }

    public void updateData(List<ScoreRecord> newData) {
        this.scoreRecords = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScoreRecord record = scoreRecords.get(position);
        holder.text1.setText(String.format("第 %d 名: %s - 分数: %d", position + 1, record.getUserName(), record.getScore()));
        holder.text2.setText("时间: " + record.getRecordTime().format(formatter));
        
        // 设置文字颜色，前三名醒目一点
        if (position == 0) holder.text1.setTextColor(0xFFFFD700); // Gold
        else if (position == 1) holder.text1.setTextColor(0xFFC0C0C0); // Silver
        else if (position == 2) holder.text1.setTextColor(0xFFCD7F32); // Bronze
        else holder.text1.setTextColor(0xFFFFFFFF);
        
        holder.text2.setTextColor(0xFFAAAAAA);
    }

    @Override
    public int getItemCount() {
        return scoreRecords.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;

        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
