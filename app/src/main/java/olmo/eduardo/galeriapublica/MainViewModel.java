package olmo.eduardo.galeriapublica;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {
    //gardando a opcao de navegacao selecionada (grid ou list)
    int navigationOpSelected = R.id.gridViewOp;

    //construtor que recebe como parametro uma instancia da aplicacao
    public MainViewModel (@NonNull Application application) {
        super(application);
    }

    //metodo para pegar a opcao de visualizacao selecionada
    public int getNavigationOpSelected() {
        return navigationOpSelected;
    }

    //metodo para estabelecer a opcao de navegacao selecionada
    public void setNavigationOpSelected(int navigationOpSelected) {
        this.navigationOpSelected = navigationOpSelected;
    }

}
