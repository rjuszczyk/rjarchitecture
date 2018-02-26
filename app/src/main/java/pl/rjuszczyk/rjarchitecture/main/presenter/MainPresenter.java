package pl.rjuszczyk.rjarchitecture.main.presenter;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pl.rjuszczyk.rjarchitecture.abs.action.UnsupportedActionException;
import pl.rjuszczyk.rjarchitecture.abs.presenter.AbsPresenter;
import pl.rjuszczyk.rjarchitecture.abs.model.StateStorage;
import pl.rjuszczyk.rjarchitecture.action.MainAction;
import pl.rjuszczyk.rjarchitecture.main.model.MainRepository;
import pl.rjuszczyk.rjarchitecture.main.model.MainItem;
import pl.rjuszczyk.rjarchitecture.main.model.MainState;

public class MainPresenter extends AbsPresenter<MainAction, MainState>{

    private final MainRepository mainRepository;
    private Disposable disposable;

    MainPresenter(StateStorage<MainState> mainStateStorage, MainRepository mainRepository) {
        super(mainStateStorage);
        this.mainRepository = mainRepository;
    }

    @Override
    protected MainState createInitialState() {
        return new MainState.MainStateStopped();
    }

    @Override
    protected void restore(MainState restoredState) {
        if(restoredState instanceof MainState.MainStateLoading) {
            start();
        }
    }

    protected MainState interact(MainState currentState, MainAction action) {
        if(action instanceof MainAction.MainActionStart) {
            if(currentState instanceof MainState.MainStateStopped) {
                start();
                return new MainState.MainStateLoading();
            }
        }
        if(action instanceof MainAction.MainActionStop) {
            if(currentState == null || currentState instanceof MainState.MainStateLoading || currentState instanceof MainState.MainStateLoaded) {
                stop();
                return new MainState.MainStateStopped();
            }
        }
        if(action instanceof MainAction.MainActionLoaded) {
            if(currentState != null && currentState instanceof MainState.MainStateLoading) {
                MainAction.MainActionLoaded mainActionLoaded = (MainAction.MainActionLoaded) action;
                return new MainState.MainStateLoaded(mainActionLoaded.getLoadedItems());
            }
        }
        if(action instanceof MainAction.MainActionError) {
            if(currentState != null && currentState instanceof MainState.MainStateLoading) {
                MainAction.MainActionError mainActionFailed = (MainAction.MainActionError) action;
                return new MainState.MainStateFailed(mainActionFailed.getThrowable().getMessage());
            }
        }
        throw new UnsupportedActionException(this, action, currentState);
    }

    private void start() {
        disposable = mainRepository.loadMainItems().subscribe(new Consumer<List<MainItem>>() {
            @Override
            public void accept(List<MainItem> mainItems) throws Exception {
                actionPublisher().onNext(new MainAction.MainActionLoaded(mainItems));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                actionPublisher().onNext(new MainAction.MainActionError(throwable));
            }
        });
    }

    private void stop() {
        if(disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }
}
