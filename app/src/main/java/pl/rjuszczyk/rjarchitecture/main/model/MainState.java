package pl.rjuszczyk.rjarchitecture.main.model;

import java.util.List;

import pl.rjuszczyk.rjarchitecture.abs.model.AbsState;

public interface MainState extends AbsState {
    class MainStateStopped implements MainState {

    }

    class MainStateLoading implements MainState {

    }

    class MainStateLoaded implements MainState {
        final List<MainItem> items;
        public MainStateLoaded(List<MainItem> items) {
            this.items = items;
        }

        public List<MainItem> getItems() {
            return items;
        }
    }

    class MainStateFailed implements MainState {
        private final String errorMessage;

        public MainStateFailed(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
