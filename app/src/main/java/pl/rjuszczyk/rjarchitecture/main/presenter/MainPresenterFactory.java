package pl.rjuszczyk.rjarchitecture.main.presenter;

import io.reactivex.Scheduler;
import pl.rjuszczyk.rjarchitecture.abs.presenter.AbsPresenterFactory;
import pl.rjuszczyk.rjarchitecture.abs.model.StateStorage;
import pl.rjuszczyk.rjarchitecture.action.MainAction;
import pl.rjuszczyk.rjarchitecture.main.MainStateStorage;
import pl.rjuszczyk.rjarchitecture.main.model.MainRepository;
import pl.rjuszczyk.rjarchitecture.main.model.MainState;

public class MainPresenterFactory extends AbsPresenterFactory<MainPresenter, MainAction, MainState> {

    private final MainRepository mainRepository;

    public MainPresenterFactory(
            MainStateStorage mainStateStorage,
            MainRepository mainRepository,
            Scheduler mainThread,
            Scheduler subscribeOn
    ) {
        super(mainStateStorage, mainThread, subscribeOn);
        this.mainRepository = mainRepository;
    }

    @Override
    protected MainPresenter createInstance(StateStorage<MainState> stateStorage) {
        return new MainPresenter(stateStorage, mainRepository);
    }
}
