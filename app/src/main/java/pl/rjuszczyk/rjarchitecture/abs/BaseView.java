package pl.rjuszczyk.rjarchitecture.abs;

import pl.rjuszczyk.rjarchitecture.abs.model.AbsState;

public interface BaseView<State extends AbsState> {
    void render(State state);
}
