package pl.rjuszczyk.rjarchitecture.abs.model;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface StateStorage<State extends AbsState> {
    Completable store(final State mainState);
    Single<State> restore();
    void clean();
}
