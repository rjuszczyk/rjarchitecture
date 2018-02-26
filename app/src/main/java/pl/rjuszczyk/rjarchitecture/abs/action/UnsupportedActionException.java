package pl.rjuszczyk.rjarchitecture.abs.action;

import pl.rjuszczyk.rjarchitecture.abs.model.AbsState;
import pl.rjuszczyk.rjarchitecture.abs.presenter.AbsPresenter;

public class UnsupportedActionException extends RuntimeException {
    private final AbsPresenter absPresenter;
    private final AbsAction action;
    private final AbsState absState;

    public UnsupportedActionException(AbsPresenter absPresenter, AbsAction action, AbsState absState) {
        this.absPresenter = absPresenter;
        this.action = action;
        this.absState = absState;
    }

    @Override
    public String getMessage() {
        return String.format(
                "%s not able to handle %s action while in %s state",
                absPresenter.getClass().getSimpleName(),
                action.getClass().getSimpleName(),
                absState.getClass().getSimpleName());
    }
}
