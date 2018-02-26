package pl.rjuszczyk.rjarchitecture.abs.presenter;

import android.os.Bundle;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pl.rjuszczyk.rjarchitecture.abs.BaseView;
import pl.rjuszczyk.rjarchitecture.abs.action.AbsAction;
import pl.rjuszczyk.rjarchitecture.abs.model.AbsState;
import pl.rjuszczyk.rjarchitecture.abs.model.StateStorage;
import pl.rjuszczyk.rjarchitecture.abs.ViewWrapper;

public abstract class AbsPresenterFactory<Presenter extends AbsPresenter<Action, State>, Action extends AbsAction, State extends AbsState> {
    private final StateStorage<State> mainStateStorage;
    private final Scheduler mainThread;
    private final Scheduler subscribeOn;

    private ViewWrapper<State> mainViewWrapped;
    private Presenter cachedPresenter = null;
    private Disposable viewDisposable = null;

    public AbsPresenterFactory(
            StateStorage<State> mainStateStorage,
            Scheduler mainThread,
            Scheduler subscribeOn
    ) {
        this.mainStateStorage = mainStateStorage;
        this.mainThread = mainThread;
        this.subscribeOn = subscribeOn;
    }

    public Presenter providePresenter(Bundle savedInstanceState, BaseView<State> mainView) {
        if(cachedPresenter == null) {
            mainViewWrapped = new ViewWrapper<>();
            if(savedInstanceState == null) {
                cachedPresenter = createInstance(mainStateStorage);
            } else {
                cachedPresenter = createInstance(mainStateStorage);
                mainStateStorage.restore()
                        .subscribeOn(subscribeOn)
                        .observeOn(mainThread)
                        .subscribe(new Consumer<State>() {
                            @Override
                            public void accept(State restoredState) throws Exception {
                                mainViewWrapped.render(restoredState);
                                cachedPresenter.setLastStateAndRestore(restoredState);
                            }
                        });
            }

            viewDisposable = cachedPresenter.view().subscribeOn(subscribeOn).observeOn(mainThread).subscribe(new Consumer<State>() {
                @Override
                public void accept(State mainState) throws Exception {
                    mainViewWrapped.render(mainState);
                }
            });
        }

        mainViewWrapped.setWrapped(mainView);

        return cachedPresenter;
    }

    public void destroyPresenter() {
        mainStateStorage.clean();
        cachedPresenter = null;
        if(viewDisposable != null) {
            viewDisposable.dispose();
            viewDisposable = null;
        }
    }

    protected abstract Presenter createInstance(StateStorage<State> stateStorage);
}

