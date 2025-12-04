package com.example.afremedios;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.annotations.SerializedName;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RickMortyActivity extends AppCompatActivity {

    private ImageView imgPersonagem;
    private TextView txtNome, txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rick_morty);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        imgPersonagem = findViewById(R.id.imgPersonagem);
        txtNome = findViewById(R.id.txtNome);
        txtStatus = findViewById(R.id.txtStatus);

        carregarPersonagemAleatorio();
    }

    private void carregarPersonagemAleatorio() {
        int idAleatorio = new Random().nextInt(826) + 1;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://rickandmortyapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RickMortyService service = retrofit.create(RickMortyService.class);

        service.getCharacter(idAleatorio).enqueue(new Callback<Character>() {
            @Override
            public void onResponse(Call<Character> call, Response<Character> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Character character = response.body();

                    txtNome.setText(character.name);
                    txtStatus.setText(character.status + " - " + character.species);

                    Glide.with(RickMortyActivity.this)
                            .load(character.image)
                            .circleCrop()
                            .into(imgPersonagem);
                }
            }

            @Override
            public void onFailure(Call<Character> call, Throwable t) {
                Toast.makeText(RickMortyActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    interface RickMortyService {
        @GET("character/{id}")
        Call<Character> getCharacter(@Path("id") int id);
    }
    static class Character {
        @SerializedName("name") String name;
        @SerializedName("status") String status;
        @SerializedName("species") String species;
        @SerializedName("image") String image;
    }
}