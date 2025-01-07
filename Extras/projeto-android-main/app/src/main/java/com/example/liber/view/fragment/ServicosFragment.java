package com.example.liber.view.fragment;

        import android.os.Bundle;
        import android.view.View;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.FragmentManager;

        import com.example.liber.controller.PesquisarClickInterface;
        import com.example.liber.model.Servico;
        import com.example.liber.model.Usuario;

        import org.jetbrains.annotations.NotNull;

public class ServicosFragment extends PesquisarFragment implements PesquisarClickInterface {

    private static final String TAG = "ServicosFragment";

    private FragmentManager fragmentManager;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.loadServicosRecyclerView(ServicosFragment.this);
        swipeListener();
        clickListener();
    }

    // Faz o vínculo dos ítens da view
    @Override
    protected void bindOnView(View view) {
        super.bindOnView(view);
        tvTitle.setText("Serviços:");
        tvVazio.setText("Nenhum serviço, por enquanto :)");
        fragmentManager = getParentFragmentManager();
    }

    // Detecta o movimento de swipe e recarrega a recycler view
    private void swipeListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            super.loadServicosRecyclerView(ServicosFragment.this);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Detecta o click do botão de pesquisar
    private void clickListener() {
        btnSearch.setOnClickListener(v -> {
            if (etSearch.getText().toString().equals("")) {
                super.loadServicosRecyclerView(ServicosFragment.this);
            } else {
                super.getServicoBySearch(etSearch.getText().toString().trim(), ServicosFragment.this);
            }
        });
    }

    // Abre a tela com os detalhes da publicação, quando houver o click
    @Override
    public void onCardClick(Servico servico) {
        PublicacaoDialogFragment dialog = new PublicacaoDialogFragment(servico);
        dialog.show(fragmentManager, "Dialog");
    }

    @Override
    public void onSeguirClick(int seguidorId) {}

    @Override
    public void onSeguindoClick(int seguidorId) {}

    @Override
    public void onCardClick(Usuario usuario) {

    }
}

