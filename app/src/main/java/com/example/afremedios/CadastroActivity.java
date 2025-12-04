package com.example.afremedios;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.afremedios.model.Remedio;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CadastroActivity extends AppCompatActivity {

    private EditText edtNome, edtDescricao, edtHorario;
    private FirebaseFirestore db;
    private String idEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        db = FirebaseFirestore.getInstance();

        edtNome = findViewById(R.id.editTextNomeMedicamento);
        edtDescricao = findViewById(R.id.editTextDescricaoMedicamento);
        edtHorario = findViewById(R.id.editTextHorarioMedicamento);
        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarLivro());

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        idEditando = getIntent().getStringExtra("id");

        if (idEditando != null) {
            toolbar.setTitle(getString(R.string.titulo_editar));
            preencherCampos();
        } else {
            toolbar.setTitle(getString(R.string.titulo_adicionar));
        }
    }

    private void preencherCampos() {
        edtNome.setText(getIntent().getStringExtra("nome"));
        edtDescricao.setText(getIntent().getStringExtra("descricao"));
        edtHorario.setText(getIntent().getStringExtra("horario"));
    }

    private void salvarLivro() {
        String nome = edtNome.getText().toString();
        String descricao = edtDescricao.getText().toString();
        String horario = edtHorario.getText().toString();

        if (nome.isEmpty() || descricao.isEmpty() || horario.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idEditando == null) {
            Remedio novo = new Remedio(null, nome, descricao, horario, false);

            db.collection("remedios").add(novo)
                    .addOnSuccessListener(doc -> {
                        novo.setId(doc.getId());
                        agendarNotificacao(novo);
                        Toast.makeText(this, "Remédio adicionado!", Toast.LENGTH_SHORT).show();
                        finish();
                    });

        } else {
            Remedio editado = new Remedio(
                    idEditando,
                    nome,
                    descricao,
                    horario,
                    getIntent().getBooleanExtra("tomado", false)
            );

            db.collection("remedios")
                    .document(idEditando)
                    .set(editado)
                    .addOnSuccessListener(aVoid -> {
                        agendarNotificacao(editado);
                        Toast.makeText(this, "Remédio atualizado!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
    private void agendarNotificacao(Remedio r) {
        if (r.getHorario() == null || r.getHorario().isEmpty()) return;

        try {
            String[] partes = r.getHorario().split(":");
            int hora = Integer.parseInt(partes[0]);
            int minuto = Integer.parseInt(partes[1]);

            Calendar c = Calendar.getInstance();
            c.set(java.util.Calendar.HOUR_OF_DAY, hora);
            c.set(java.util.Calendar.MINUTE, minuto);
            c.set(java.util.Calendar.SECOND, 0);

            if (c.getTimeInMillis() <= System.currentTimeMillis()) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("nome", r.getNome());
            intent.putExtra("descricao", r.getDescricao());
            intent.putExtra("id", r.getId());

            int pendingIntentId = r.getId() != null ? r.getId().hashCode() : (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    pendingIntentId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            try {

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        c.getTimeInMillis(),
                        pendingIntent
                );
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Não é possível agendar alarmes exatos neste dispositivo.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao agendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
