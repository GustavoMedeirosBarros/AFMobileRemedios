package com.example.afremedios;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class RickMortyActivity extends AppCompatActivity {

    private ImageView imgPersonagem;
    private TextView txtNome, txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rick_morty);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        imgPersonagem = findViewById(R.id.imgPersonagem);
        txtNome = findViewById(R.id.txtNome);
        txtStatus = findViewById(R.id.txtStatus);

        carregarPersonagemAleatorio();
    }

    private void carregarPersonagemAleatorio() {
        new Thread(() -> {
            try {
                int idAleatorio = new Random().nextInt(826) + 1;

                URL url = new URL("https://rickandmortyapi.com/api/character/" + idAleatorio);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("GET");

                int responseCode = conexao.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String jsonString = response.toString();
                    JSONObject jsonObject = new JSONObject(jsonString);

                    String nome = jsonObject.getString("name");
                    String status = jsonObject.getString("status");
                    String species = jsonObject.getString("species");
                    String imagemUrl = jsonObject.getString("image");

                    URL urlImagem = new URL(imagemUrl);
                    InputStream inputStream = urlImagem.openStream();
                    Bitmap imagemBitmap = BitmapFactory.decodeStream(inputStream);

                    runOnUiThread(() -> {
                        txtNome.setText(nome);
                        txtStatus.setText(status + " - " + species);
                        imgPersonagem.setImageBitmap(imagemBitmap);
                    });

                } else {
                    Log.e("API", "Erro de conexão: " + responseCode);
                    runOnUiThread(() ->
                            Toast.makeText(RickMortyActivity.this, "Erro ao buscar dados", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(RickMortyActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}