package pl.rjuszczyk.rjarchitecture.abs.presenter;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import pl.rjuszczyk.rjarchitecture.abs.action.AbsAction;
import pl.rjuszczyk.rjarchitecture.abs.model.AbsState;
import pl.rjuszczyk.rjarchitecture.abs.model.StateStorage;

public abstract class AbsPresenter<Action extends AbsAction, State extends AbsState> {
    private final Subject<Action> actionPublisher;
    private final Observable<State> viewStateObservable;

    private State currentState;
    private final StateStorage<State> stateStorage;

    public AbsPresenter(StateStorage<State> stateStorage) {
        this.stateStorage = stateStorage;
        this.actionPublisher = ReplaySubject.create();
        this.viewStateObservable = actionPublisher
                .map(new Function<Action, State>() {
                    @Override
                    public State apply(Action mainAction) throws Exception {
                        return interact(currentState, mainAction);
                    }
                })
                .flatMap(new Function<State, ObservableSource<State>>() {
                    @Override
                    public ObservableSource<State> apply(State mainState) throws Exception {
                        currentState = mainState;
                        return AbsPresenter.this.stateStorage.store(mainState).andThen(Observable.just(mainState));
                    }
                });
        this.currentState = createInitialState();
    }

    final void setLastStateAndRestore(State currentState) {
        this.currentState = currentState;

        restore(currentState);
    }

    protected abstract State createInitialState();

    protected abstract void restore(State restoredState);

    protected abstract State interact(State currentState, Action action);

    public Subject<Action> actionPublisher() {
        return actionPublisher;
    }

    public Observable<State> view() {
        return viewStateObservable;
    }
}
