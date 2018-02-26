package pl.rjuszczyk.rjarchitecture.main;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import pl.rjuszczyk.rjarchitecture.abs.model.StateStorage;
import pl.rjuszczyk.rjarchitecture.main.model.MainItem;
import pl.rjuszczyk.rjarchitecture.main.model.MainState;

@SuppressLint("ApplySharedPref")
public class MainStateStorage implements StateStorage<MainState> {
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final long presenterUniqueId;

    public MainStateStorage(long presenterUniqueId, SharedPreferences sharedPreferences, Gson gson) {
        this.presenterUniqueId = presenterUniqueId;
        this.sharedPreferences = sharedPreferences;
        this.gson = gson;
    }

    @Override
    public Completable store(final MainState mainState) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if(mainState instanceof MainState.MainStateLoading) {
                    sharedPreferences.edit()
                            .putInt("presenter_" + presenterUniqueId + "_state", 0)
                            .commit();
                }
                if(mainState instanceof MainState.MainStateFailed) {
                    MainState.MainStateFailed mainStateFailed = (MainState.MainStateFailed) mainState;
                    sharedPreferences.edit()
                            .putInt("presenter_" + presenterUniqueId + "_state", 1)
                            .putString("presenter_" + presenterUniqueId + "_errorMessage", mainStateFailed.getErrorMessage())
                            .commit();
                }
                if(mainState instanceof MainState.MainStateLoaded) {
                    MainState.MainStateLoaded mainStateLoaded = (MainState.MainStateLoaded) mainState;
                    sharedPreferences.edit()
                            .putInt("presenter_" + presenterUniqueId + "_state", 2)
                            .putString("presenter_" + presenterUniqueId + "_items", gson.toJson(mainStateLoaded.getItems()))
                            .commit();
                }
            }
        });
    }

    @Override
    public Single<MainState> restore() {
        return Single.fromCallable(new Callable<MainState>() {
            @Override
            public MainState call() throws Exception {
                int stateType = sharedPreferences.getInt("presenter_" + presenterUniqueId + "_state", -1);
                if(stateType == -1) {
                    return null;
                }
                if(stateType == 0) {
                    return new MainState.MainStateLoading();
                }
                if(stateType == 1) {
                    String errorMessage = sharedPreferences.getString("presenter_" + presenterUniqueId + "_errorMessage", null);
                    return new MainState.MainStateFailed(errorMessage);
                }
                if(stateType == 2) {
                    String itemsGson = sharedPreferences.getString("presenter_" + presenterUniqueId + "_items", null);
                    List<MainItem> items = gson.fromJson(itemsGson, new TypeToken<List<MainItem>>(){}.getType());
                    return new MainState.MainStateLoaded(items);
                }

                throw new RuntimeException("impossible");
            }
        });
    }

    @Override
    public void clean() {
        sharedPreferences.edit()
                .remove("presenter_" + presenterUniqueId + "_state")
                .remove("presenter_" + presenterUniqueId + "_items")
                .remove("presenter_" + presenterUniqueId + "_errorMessage")
                .commit();
    }
}
