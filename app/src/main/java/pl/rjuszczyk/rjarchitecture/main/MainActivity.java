package pl.rjuszczyk.rjarchitecture.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pl.rjuszczyk.rjarchitecture.R;
import pl.rjuszczyk.rjarchitecture.action.MainAction;
import pl.rjuszczyk.rjarchitecture.main.model.MainRepository;
import pl.rjuszczyk.rjarchitecture.main.presenter.MainPresenter;
import pl.rjuszczyk.rjarchitecture.main.model.MainState;
import pl.rjuszczyk.rjarchitecture.main.presenter.MainPresenterFactory;
import pl.rjuszczyk.rjarchitecture.main.view.MainView;

public class MainActivity extends AppCompatActivity implements MainView {

    private RecyclerView list;
    private ProgressBar progressBar;
    private TextView errorMessage;

    private MainListAdapter mainListAdapter;

    private MainPresenter mainPresenter;
    private static MainPresenterFactory mainPresenterFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        progressBar = findViewById(R.id.progressBar);
        errorMessage = findViewById(R.id.errorMessage);

        if(mainPresenterFactory == null) {
            MainStateStorage mainStateStorage = new MainStateStorage(0L, getApplicationContext().getSharedPreferences("default", MODE_PRIVATE), new Gson());
            mainPresenterFactory = new MainPresenterFactory(mainStateStorage, new MainRepository(), AndroidSchedulers.mainThread(), AndroidSchedulers.mainThread());//Schedulers.io());
        }

        initListAdapter();

        mainPresenter = mainPresenterFactory.providePresenter(savedInstanceState, this);

        if(savedInstanceState == null) {
            mainPresenter.actionPublisher().onNext(new MainAction.MainActionStart());
        }
    }

    private void initListAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        mainListAdapter = new MainListAdapter();
        list.setAdapter(mainListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            mainPresenter.actionPublisher().onNext(new MainAction.MainActionStop());
            mainPresenterFactory.destroyPresenter();
        }
    }

    @Override
    public void render(MainState mainState) {
        if(mainState instanceof MainState.MainStateLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else if(mainState instanceof MainState.MainStateLoaded) {
            MainState.MainStateLoaded mainStateLoaded = (MainState.MainStateLoaded) mainState;
            progressBar.setVisibility(View.INVISIBLE);
            mainListAdapter.setItems(mainStateLoaded.getItems());
        } else if(mainState instanceof MainState.MainStateFailed) {
            MainState.MainStateFailed mainStateFailed = (MainState.MainStateFailed) mainState;
            progressBar.setVisibility(View.INVISIBLE);
            errorMessage.setText(mainStateFailed.getErrorMessage());
        }
    }
}
