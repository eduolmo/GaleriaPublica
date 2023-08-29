package olmo.eduardo.galeriapublica;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import kotlinx.coroutines.CoroutineScope;

public class MainViewModel extends AndroidViewModel {
    //gardando a opcao de navegacao selecionada (grid ou list)
    int navigationOpSelected = R.id.gridViewOp;

    LiveData<PagingData<ImageData>> pageLv;

    //construtor que recebe como parametro uma instancia da aplicacao
    public MainViewModel (@NonNull Application application) {
        super(application);
        // criamos uma instância de GalleryRepository, classe responsável por ler um bloco de fotos da galeria pública do celular
        GalleryRepository galleryRepository = new GalleryRepository(application);
        // criamos o GalleryPagingSource e passamos para ele a instância de GalleryRepository.
        // O GalleryPagingSource tem como função calcular qual bloco de dados será pedido para GalleryRepository,
        //calcular qual será a próxima página de dados e montar um objeto de resposta contendo os
        //dados da página atual e o número da próxima página calculado
        GalleryPagingSource galleryPagingSource = new GalleryPagingSource(galleryRepository);
        // iniciando a biblioteca de Paging 3 passado um objeto de configuração de paginação e a instância de GalleryPagingSource
        Pager<Integer,ImageData> pager = new Pager<>(new PagingConfig(10), () -> galleryPagingSource);
        // obtemos o objeto de escopo de MainViewModel, o qual será usado para guardar páginas de dados no cache.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        // obtemos o LiveData que foi gerado pela biblioteca de Paging 3 e guardamos esses dados no cache de MainViewModel.
        pageLv = PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
    }

    public LiveData<PagingData<ImageData>> getPageLv() {
        return pageLv;
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
