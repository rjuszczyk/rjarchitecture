package pl.rjuszczyk.rjarchitecture.abs;

import pl.rjuszczyk.rjarchitecture.abs.model.AbsState;

public class ViewWrapper<State extends AbsState> {
    private  BaseView<State> wrapped = null;
    private State lastState = null;

    public void render(State mainState) {
        if(wrapped != null) {
            wrapped.render(mainState);
        }
        lastState = mainState;
    }

    public void setWrapped(BaseView<State> view) {
        this.wrapped = view;
        if(lastState != null) {
            this.wrapped.render(lastState);
        }
    }
}
