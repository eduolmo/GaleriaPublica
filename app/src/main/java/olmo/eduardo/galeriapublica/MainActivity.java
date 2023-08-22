package olmo.eduardo.galeriapublica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    static int RESULT_REQUEST_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        bottomNavigationView = findViewById(R.id.btNav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                vm.setNavigationOpSelected(item.getItemId());
                switch (item.getItemId()) {
                    //configurando a navegacao para a visualizacao em grid
                    case R.id.gridViewOp:
                        GridViewFragment gridViewFragment = GridViewFragment.newInstance();
                        setFragment(gridViewFragment);
                        break;
                    //configurando a navegacao para a visualizacao em list
                    case R.id.listViewOp:
                        ListViewFragment listViewFragment = ListViewFragment.newInstance();
                        setFragment(listViewFragment);
                        break;
                }
                return true;

            }
        });
    }

    //metodo para mudanca de fragment
    void setFragment(Fragment fragment) {
        //iniciando a transacao de fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //substituindo fragment
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        // indicamos que esse fragmento agora faz parte da pilha de tela do botão voltar do Android
        fragmentTransaction.addToBackStack(null);
        //realizando commit do fragment
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        checkForPermissions(permissions);
    }

    private void checkForPermissions(List<String> permissions) {
        //criando uma lista para as permissoes nao concedidas
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions){
            //caso aquela permissao nao seja concedida ela é colocada dentro da lista de permissoes nao aprovadas
            if(!hasPermission(permission)){
                permissionsNotGranted.add(permission);
            }
        }

        //as permissoes nao concedidas sao requisitadas ao usuario
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsNotGranted.size() > 0){
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
            else {
                MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
                int navigationOpSelected = vm.getNavigationOpSelected();
                bottomNavigationView.setSelectedItemId(navigationOpSelected);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        //criando lista de permissoes nao concedidas
        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION){
            for(String permission : permissions){
                //caso uma permissao nao tenha sido concedida ela é colocada dentro da lista de permissoes nao concedidas
                if(!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    //mostra uma mensagem para o usuario caso um permissao nao tenha sido concedida e ela seja necessaria para a app
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //pede permissao novamente, caso seja necessario para a app
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]),RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
        else {
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
            int navigationOpSelected = vm.getNavigationOpSelected();
            bottomNavigationView.setSelectedItemId(navigationOpSelected);
        }
    }


    //verifica se uma permissao ja foi concedida ou nao
    private boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this,permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


}