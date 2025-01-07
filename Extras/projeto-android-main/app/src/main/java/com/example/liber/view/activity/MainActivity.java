package com.example.liber.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.liber.R;
import com.example.liber.model.Usuario;
import com.example.liber.utils.DrawableUtils;
import com.example.liber.view.fragment.BibliotecaFragment;
import com.example.liber.view.fragment.MapFragment;
import com.example.liber.view.fragment.PublicacoesFragment;
import com.example.liber.view.fragment.SeguidoresFragment;
import com.example.liber.view.fragment.SeguindoFragment;
import com.example.liber.view.fragment.ServicosFragment;
import com.example.liber.view.fragment.UsuariosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    // Guarda uma referência do usuário logado.
    private static Usuario loggedUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pega os dados do usuário logado da tela de login
        Gson gson = new Gson();
        try{
            //loggedUsuario = gson.fromJson(getIntent().getExtras().getString("usuario"), Usuario.class);
            loggedUsuario = new Usuario(6, "Giovanni", "giovanni@gmail.com", "senha123", "2021-06-16", false);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            loggedUsuario = new Usuario(18, "Giovanni", "giovanni@gmail.com", "123", "2021-06-16", false);
        }

        bindToolBar();
        bindActionBar();
        bindNavigationView();
        bindBottomNavigation();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, PublicacoesFragment.class, null)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_bottom_publicacoes);
            navigationView.setCheckedItem(R.id.nav_books);
        }

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ServicosFragment.class, null)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_books);
        }*/
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, PublicacoesFragment.class, null)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_books);
        }*/
    }

    @Override
    public void onBackPressed() {
        // Fecha a gaveta ao pressionar o botão de voltar.
        super.onBackPressed();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Evita que o usuário volte para a tela de login ao pressionar o botão de voltar.
            moveTaskToBack(true);
        }
    }

    private void bindToolBar() {
        // Vincula a toolbar à view
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void bindActionBar() {
        // Vincula a action bar à view
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));
        toggle.syncState();
    }

    private void bindNavigationView() {
        // Vincula os itens da gaveta
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tvNavIcon = headerView.findViewById(R.id.tv_nav_icon);
        TextView tvNavNome = headerView.findViewById(R.id.tv_nav_header_nome);
        TextView tvNavEmail = headerView.findViewById(R.id.tv_nav_header_email);
        int hash = loggedUsuario.getNome().hashCode();
        tvNavIcon.setText(String.valueOf(loggedUsuario.getNome().charAt(0)));
        tvNavIcon.setBackground(DrawableUtils.oval(Color.rgb(hash, hash / 2, 0), tvNavIcon));
        tvNavNome.setText(loggedUsuario.getNome());
        tvNavEmail.setText(loggedUsuario.getEmail());
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void bindBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_bottom_publicacoes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, PublicacoesFragment.class, null)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.nav_bottom_servicos) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, ServicosFragment.class, null)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.nav_bottom_mapa) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MapFragment())
                        .commit();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        // Captura os cliques da gaveta e abre os fragments necessários
        if (item.getItemId() == R.id.nav_library) {
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, BibliotecaFragment.class, null)
                    .commit();*/
        } else if (item.getItemId() == R.id.nav_books) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, PublicacoesFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_services) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ServicosFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_exit) {
            // TODO: Dialog box de confirmação
            // Fecha a activity e volta para a tela de login
            finish();
        } else if (item.getItemId() == R.id.nav_about) {
            Toast.makeText(this, "App feito para o projeto de TCC", Toast.LENGTH_LONG).show();
        }

        /*
         if (item.getItemId() == R.id.nav_library) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, BibliotecaFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_books) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, PublicacoesFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_users) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, UsuariosFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_followers) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, SeguidoresFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_following) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, SeguindoFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_services) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ServicosFragment.class, null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_exit) {
            // TODO: Dialog box de confirmação
            // Fecha a activity e volta para a tela de login
            finish();
        } else if (item.getItemId() == R.id.nav_about) {
            Toast.makeText(this, "Liber App feito para o projeto interdisciplinar", Toast.LENGTH_LONG).show();
        }
        */

        // Fecha a gaveta após selecionar um item
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Usuario getLoggedUsuario() {
        return loggedUsuario;
    }
}
