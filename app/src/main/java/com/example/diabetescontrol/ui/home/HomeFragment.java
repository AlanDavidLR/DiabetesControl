package com.example.diabetescontrol.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.diabetescontrol.ImageSlide;
import com.example.diabetescontrol.ImageSliderAdapter;
import com.example.diabetescontrol.R;
import com.example.diabetescontrol.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private WebView webView;
    private Handler sliderHandler;
    private ViewPager2 viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Asignar el ViewModel al layout
        binding.setViewModel(homeViewModel);

        // Inicializar el WebView
        webView = root.findViewById(R.id.webView);
        // Configurar el WebView
        String url = "http://www.imss.gob.mx/salud-en-linea/diabetes-mellitus";
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webView.setInitialScale(1);

        // Ajustar el contenido al tamaño de la pantalla
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() { " +
                        "var metas = document.getElementsByTagName('meta'); " +
                        "for (var i = 0; i < metas.length; i++) { " +
                        "if (metas[i].name.toLowerCase() === 'viewport') { " +
                        "metas[i].parentNode.removeChild(metas[i]); " +
                        "break; " +
                        "} " +
                        "} " +
                        "var meta = document.createElement('meta'); " +
                        "meta.name = 'viewport'; " +
                        "meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'; " +
                        "document.getElementsByTagName('head')[0].appendChild(meta); " +
                        "})()");
            }
        });

        webView.loadUrl(url);

        // Cargar la URL en el WebView en un hilo separado
        new LoadWebViewTask().execute(url);

        // Inicializar el VideoView
        VideoView videoView = root.findViewById(R.id.videoView);

        // Configurar la ruta del video en un hilo separado
        new LoadVideoViewTask(videoView).execute("android.resource://" + requireActivity().getPackageName() + "/" + R.raw.diabetestipo2v);

        // Obtener el nombre del usuario de las SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String nombreUsuario = sharedPreferences.getString("nombreUsuario", "");
        String apellidosUsuario = sharedPreferences.getString("apellidosUsuario", "");

        // Encontrar el TextView en el diseño XML
        TextView inicioSesionTextView = root.findViewById(R.id.inicioSesionTextView);

        // Establecer el texto con el nombre del usuario si está disponible
        if (!nombreUsuario.isEmpty()) {
            inicioSesionTextView.setText("Inicio de sesión correcto: " + nombreUsuario + " " + apellidosUsuario);
        } else {
            inicioSesionTextView.setText("Inicio de sesión correcto");
        }

        // Inicializar ViewPager2 y su adaptador
        viewPager = root.findViewById(R.id.viewPager);
        List<ImageSlide> imageSlides = new ArrayList<>();
        // Agregar tus imágenes y descripciones aquí
        imageSlides.add(new ImageSlide(R.drawable.alimetos, "Cuida tu alimentacion, evita los alimentos altos en azucares, presiona sobre la imagen para conocer como cuidarte", "https://www.imss.gob.mx/sites/all/statics/salud/guias_salud/cartera-alimentacion.pdf"));
        imageSlides.add(new ImageSlide(R.drawable.ejercicio, "Conoce la  actividad fisica que puedes practicar, adecuada para personas con Diabetes", "https://www.imss.gob.mx/salud-en-linea/actividad-fisica"));
        imageSlides.add(new ImageSlide(R.drawable.pie, "Mejora el cuidado de tu pies, presiona sobre la imgagen para mayor información", "http://www.imss.gob.mx/salud-en-linea/pie-diabetico"));
        ImageSliderAdapter adapter = new ImageSliderAdapter(requireContext(), imageSlides);
        viewPager.setAdapter(adapter);

        // Configurar controles de reproducción para el VideoView
        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Configurar el tiempo de transición automática (cada 5 segundos)
        sliderHandler = new Handler(Looper.getMainLooper());
        sliderHandler.postDelayed(sliderRunnable, 5000);

        return root;
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int nextIndex = viewPager.getCurrentItem() + 1;
            if (nextIndex >= viewPager.getAdapter().getItemCount()) {
                nextIndex = 0; // Vuelve al principio
            }
            viewPager.setCurrentItem(nextIndex);
            sliderHandler.postDelayed(this, 5000); // Cambia de página cada 5 segundos
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // Detener el cambio automático de diapositivas al destruir la vista
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    private class LoadWebViewTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            // No hacer nada en el background para WebView
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String url = "http://www.imss.gob.mx/salud-en-linea/diabetes-mellitus";
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);
        }
    }

    private class LoadVideoViewTask extends AsyncTask<String, Void, String> {
        private VideoView videoView;

        LoadVideoViewTask(VideoView videoView) {
            this.videoView = videoView;
        }

        @Override
        protected String doInBackground(String... paths) {
            return paths[0];
        }

        @Override
        protected void onPostExecute(String videoPath) {
            videoView.setVideoPath(videoPath);
        }
    }
}


