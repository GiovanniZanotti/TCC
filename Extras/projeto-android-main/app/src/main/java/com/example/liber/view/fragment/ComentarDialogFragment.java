package com.example.liber.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.liber.R;
import com.example.liber.controller.ComentarioClickInterface;

import org.jetbrains.annotations.NotNull;

public class ComentarDialogFragment extends DialogFragment {

    private static final String TAG = "ComentarDialogFragment";

    private EditText etComentario;
    private Button btnComentar;
    private ComentarioClickInterface comentarioClickInterface;

    public ComentarDialogFragment(ComentarioClickInterface comentarioClickInterface) {
        this.comentarioClickInterface = comentarioClickInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_comentar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etComentario = view.findViewById(R.id.et_comentario);
        btnComentar = view.findViewById(R.id.btn_comentar);

        clickListener();
    }

    private void clickListener() {
        btnComentar.setOnClickListener(v -> {
            String comentario = etComentario.getText().toString().trim();
            if (comentario.equals("")) {
                Toast.makeText(getContext(), "Insira um comentário!", Toast.LENGTH_SHORT).show();
            } else {
                // Posta o comentário e fecha a tela
                comentarioClickInterface.onComentarClick(comentario);
                dismiss();
            }
        });
    }
}
