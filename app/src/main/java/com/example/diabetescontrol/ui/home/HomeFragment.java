package com.example.diabetescontrol.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Asignar el ViewModel al layout
        binding.setViewModel(homeViewModel);

        // Inicializar el WebView
        webView = root.findViewById(R.id.webView);

        // Habilitar JavaScript (opcional)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Configurar para que el contenido se ajuste a la pantalla
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Cargar la URL en el WebView
        webView.loadUrl("https://www.gob.mx/promosalud/acciones-y-programas/diabetes-en-mexico-284509");

        // Establecer un WebViewClient para que la navegación se maneje dentro del WebView
        webView.setWebViewClient(new WebViewClient());

        // Inicializar el VideoView
        VideoView videoView = root.findViewById(R.id.videoView);

        // Configurar la ruta del video
        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.diabetestipo2v;

        // Establecer la ruta del video en el VideoView
        videoView.setVideoPath(videoPath);

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
        imageSlides.add(new ImageSlide(R.drawable.alimetos, "Cuida tu alimentacion, evita los alimentos altos en azucares, presiona sobre", "https://tu-url-1.com"));
        imageSlides.add(new ImageSlide(R.drawable.ejercicio, "Conoce la  actividad fisica que puedes practicar, adecuada para personas con Diabetes", "https://tu-url-2.com"));
        imageSlides.add(new ImageSlide(R.drawable.pie, "Mejora el cuidado de tu pies, presiona sobre la imgagen para mayor infomración", "https://tu-url-2.com"));
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
}



