package edu.hitsz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import edu.hitsz.data.RankAdapter;
import edu.hitsz.data.ScoreDao;
import edu.hitsz.data.ScoreDaoImpl;

public class RankActivity extends AppCompatActivity {

    private ScoreDao scoreDao;
    private RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        // 初始化 ScoreDaoImpl 并传入 context，以确保文件路径正确
        scoreDao = new ScoreDaoImpl(this);
        
        RecyclerView recyclerView = findViewById(R.id.rank_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new RankAdapter(scoreDao.getAllScores());
        recyclerView.setAdapter(adapter);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        Button btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(v -> {
            scoreDao.clearScores();
            adapter.updateData(scoreDao.getAllScores());
        });
    }
}
