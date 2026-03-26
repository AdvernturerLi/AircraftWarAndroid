package edu.hitsz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.hitsz.data.Medal;
import edu.hitsz.data.ScoreDao;
import edu.hitsz.data.ScoreDaoImpl;
import edu.hitsz.data.ScoreRecord;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView rvMedals;
    private MedalAdapter adapter;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        scoreDao = new ScoreDaoImpl(this);
        rvMedals = findViewById(R.id.rv_medals);
        rvMedals.setLayoutManager(new LinearLayoutManager(this));

        List<Medal> unlockedMedals = checkUnlockedMedals();
        adapter = new MedalAdapter(unlockedMedals);
        rvMedals.setAdapter(adapter);

        TextView tvUsername = findViewById(R.id.tv_username);
        List<ScoreRecord> allScores = scoreDao.getAllScores();
        if (!allScores.isEmpty()) {
            tvUsername.setText(allScores.get(0).getUserName());
        }
    }

    private List<Medal> checkUnlockedMedals() {
        List<Medal> medals = new ArrayList<>();
        List<ScoreRecord> scores = scoreDao.getAllScores();
        
        int maxScore = 0;
        for (ScoreRecord r : scores) {
            if (r.getScore() > maxScore) maxScore = r.getScore();
        }

        // 示例勋章逻辑
        if (maxScore >= 100) {
            medals.add(new Medal("初出茅庐", "得分超过100分", R.drawable.prop_blood));
        }
        if (maxScore >= 500) {
            medals.add(new Medal("王牌飞行员", "得分超过500分", R.drawable.prop_bullet));
        }
        if (maxScore >= 1000) {
            medals.add(new Medal("空战之神", "得分超过1000分", R.drawable.prop_bomb));
        }
        
        if (medals.isEmpty()) {
            medals.add(new Medal("继续努力", "尚未获得任何勋章", R.drawable.mob));
        }

        return medals;
    }

    private static class MedalAdapter extends RecyclerView.Adapter<MedalAdapter.ViewHolder> {
        private final List<Medal> medals;

        MedalAdapter(List<Medal> medals) { this.medals = medals; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Medal medal = medals.get(position);
            holder.tvName.setText(medal.getName());
            holder.tvDesc.setText(medal.getDescription());
            holder.ivIcon.setImageResource(medal.getIconResId());
        }

        @Override
        public int getItemCount() { return medals.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName, tvDesc;

            ViewHolder(View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_medal_icon);
                tvName = itemView.findViewById(R.id.tv_medal_name);
                tvDesc = itemView.findViewById(R.id.tv_medal_desc);
            }
        }
    }
}
