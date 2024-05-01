package com.example.diabetescontrol.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.diabetescontrol.R;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diabetescontrol.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private WebView webView;

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
        webView.loadUrl("https://atencionmedica.com.mx/pro/noticias");

        // Establecer un WebViewClient para que la navegación se maneje dentro del WebView
        webView.setWebViewClient(new WebViewClient());

        // Inicializar el VideoView
        VideoView videoView = root.findViewById(R.id.videoView);

        // Configurar la ruta del video
        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.diabetestipo2v;

        // Establecer la ruta del video en el VideoView
        videoView.setVideoPath(videoPath);

        // Inicializar el controlador de medios
        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(videoView);

        // Configurar el controlador de medios en el VideoView
        videoView.setMediaController(mediaController);

        // Suscribirse al LiveData del ViewModel para actualizar la vista
        homeViewModel.getText().observe(getViewLifecycleOwner(), newText -> {
            // Aquí puedes hacer algo con el texto si es necesario
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
