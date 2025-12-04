package com.example.afremedios;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afremedios.adapter.RemedioAdapter;
import com.example.afremedios.model.Remedio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnTomado;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdicionar;
    private RemedioAdapter adapter;
    private List<Remedio> listaRemedios;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewLista);
        btnTomado = findViewById(R.id.btnTomado);
        btnAdicionar = findViewById(R.id.fabAdicionar);

        listaRemedios = new ArrayList<>();
        adapter = new RemedioAdapter(listaRemedios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        carregarRemedios();

        btnTomado.setOnClickListener(v -> salvarAlteracoes());

        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroActivity.class);
            startActivity(intent);
        });

        adapter.setOnItemClickListener(remedio -> {
            Intent intent = new Intent(this, CadastroActivity.class);
            intent.putExtra("id", remedio.getId());
            intent.putExtra("nome", remedio.getNome());
            intent.putExtra("descricao", remedio.getDescricao());
            intent.putExtra("horario", remedio.getHorario());
            intent.putExtra("tomado", remedio.isTomado());
            startActivity(intent);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
            }
        }


    }

    private void carregarRemedios() {
        db.collection("remedios").get().addOnSuccessListener(query -> {
            listaRemedios.clear();
            for (QueryDocumentSnapshot doc : query) {
                Remedio r = doc.toObject(Remedio.class);
                r.setId(doc.getId());
                listaRemedios.add(r);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void salvarAlteracoes() {
        List<Remedio> lista = adapter.getRemedios();
        for (Remedio r : lista) {
            if (r.getId() == null || r.getId().isEmpty()) {
                db.collection("remedios").add(r).addOnSuccessListener(doc -> {
                    r.setId(doc.getId());
                    agendarNotificacaoDireta(r);
                    Toast.makeText(this, "Remédio salvo!", Toast.LENGTH_SHORT).show();
                });

            } else {
                db.collection("remedios").document(r.getId()).set(r)
                        .addOnSuccessListener(aVoid -> {
                            agendarNotificacaoDireta(r);
                            Toast.makeText(this, "Remédio atualizado!", Toast.LENGTH_SHORT).show();
                        });
            }
        }
        carregarRemedios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarRemedios();
    }

    private void agendarNotificacaoDireta(Remedio r) {
        String[] partes = r.getHorario().split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        //Calendar c = Calendar.getInstance();
        //c.set(Calendar.HOUR_OF_DAY, hora);
        //c.set(Calendar.MINUTE, minuto);
        //c.set(Calendar.SECOND, 0);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 10);


        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("nome", r.getNome());
        intent.putExtra("descricao", r.getDescricao());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(),
                pendingIntent
        );


    }


}