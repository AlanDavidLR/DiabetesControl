package com.example.diabetescontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.diabetescontrol.databinding.ActivityNavegacionBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class Navegacion extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavegacionBinding binding;
    private static final String TAG = "NavegacionActivity"; // Tag para los logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavegacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavegacion.toolbar);
        binding.appBarNavegacion.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navegacion);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Set the email in the header TextView
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("emailUsuario", "");
        String avatarUrl = sharedPreferences.getString("avatarUsuario", "");



        View headerView = navigationView.getHeaderView(0);
        TextView textViewEmail = headerView.findViewById(R.id.textView);
        textViewEmail.setText(userEmail);

        // Load the avatar image into the ImageView
        ImageView imageViewNav = headerView.findViewById(R.id.imageViewnav);
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // Decodificar la cadena base64 en un Bitmap
            byte[] decodedString = Base64.decode(avatarUrl, Base64.DEFAULT);
            Bitmap avatarBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // Redimensionar el Bitmap al tamaño deseado
            int targetSize = getResources().getDimensionPixelSize(R.dimen.avatar_icon_size); // Definir el tamaño deseado en dimens.xml
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(avatarBitmap, targetSize, targetSize, false);

            // Asignar el Bitmap al ImageView
            imageViewNav.setImageBitmap(resizedBitmap);

        } else {
            Log.d(TAG, "Avatar URL is empty or null.");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navegacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            // Lógica para cerrar sesión
            SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Redirigir a la actividad de Login
            Intent intent = new Intent(Navegacion.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_settings) {
            // Abrir actividad Ajustes
            Intent intent = new Intent(Navegacion.this, Ajustes.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navegacion);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}


