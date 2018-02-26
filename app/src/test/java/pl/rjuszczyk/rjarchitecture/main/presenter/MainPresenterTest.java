package pl.rjuszczyk.rjarchitecture.main.presenter;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;
import pl.rjuszczyk.rjarchitecture.action.MainAction;
import pl.rjuszczyk.rjarchitecture.main.MainStateStorage;
import pl.rjuszczyk.rjarchitecture.main.model.MainItem;
import pl.rjuszczyk.rjarchitecture.main.model.MainRepository;
import pl.rjuszczyk.rjarchitecture.main.model.MainState;
import pl.rjuszczyk.rjarchitecture.main.view.MainView;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    MainPresenter mainPresenter;
    @Mock
    MainStateStorage stateStorage;
    @Mock
    MainRepository mainRepository;
    @Mock
    MainView mainView;

    @Before
    public void setup() {
        when(stateStorage.store(any(MainState.class))).thenReturn(Completable.complete());
        MainPresenterFactory mainPresenterFactory = new MainPresenterFactory(stateStorage, mainRepository, Schedulers.trampoline(), Schedulers.trampoline());
        mainPresenter = mainPresenterFactory.providePresenter(null, mainView);
    }

    @Test
    public void progressShownWhenLoadingStarted() {
        when(mainRepository.loadMainItems()).thenReturn(Single.<List<MainItem>>never());
        mainPresenter.actionPublisher().onNext(new MainAction.MainActionStart());
        verify(mainView).render(any(MainState.MainStateLoading.class));
    }

    @Test
    public void loadingStartedWhenRestoredFromLoadingState() {
        when(stateStorage.restore()).thenReturn(Single.<MainState>just(new MainState.MainStateLoading()));
        when(mainRepository.loadMainItems()).thenReturn(Single.<List<MainItem>>never());

        MainPresenterFactory mainPresenterFactory = new MainPresenterFactory(stateStorage, mainRepository, Schedulers.trampoline(), Schedulers.trampoline());
        mainPresenter = mainPresenterFactory.providePresenter(new Bundle(), mainView);

        verify(mainView).render(any(MainState.MainStateLoading.class));
    }

    @Test
    public void resultShownWhenLoadingSuccess() {
        when(mainRepository.loadMainItems()).thenReturn(Single.<List<MainItem>>just(mock(List.class)));
        mainPresenter.actionPublisher().onNext(new MainAction.MainActionStart());
        verify(mainView).render(any(MainState.MainStateLoaded.class));
    }

    @Test
    public void errorShownWhenLoadingSuccess() {
        when(mainRepository.loadMainItems()).thenReturn(Single.<List<MainItem>>error(new Throwable()));
        mainPresenter.actionPublisher().onNext(new MainAction.MainActionStart());
        verify(mainView).render(any(MainState.MainStateFailed.class));
    }

    @Test
    public void successNotShownAfterStop() {
        SingleSubject<List<MainItem>> loadMainItems = SingleSubject.create();
        when(mainRepository.loadMainItems()).thenReturn(loadMainItems);
        mainPresenter.actionPublisher().onNext(new MainAction.MainActionStart());
        mainPresenter.actionPublisher().onNext(new MainAction.MainActionStop());
        loadMainItems.onSuccess(mock(List.class));
        verify(mainView, never()).render(any(MainState.MainStateLoaded.class));
    }

    @Test
    public void exceptionThrownWhenUnsupportedAction() throws Exception {
        mainPresenter = new MainPresenter(stateStorage, mainRepository);
        Consumer<MainState> consumerState = mock(Consumer.class);
        Consumer<Throwable> consumerError = mock(Consumer.class);
        when(mainRepository.loadMainItems()).thenReturn(Single.<List<MainItem>>never());
        mainPresenter.view().subscribe(consumerState, consumerError);
        mainPresenter.actionPublisher().onNext(new MainAction.MainActionStart());
        mainPresenter.actionPublisher().onNext(new MainAction.MainActionStart());

        verify(consumerError).accept((Throwable) any());
    }
}