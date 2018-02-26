package pl.rjuszczyk.rjarchitecture.action;


import java.util.List;

import pl.rjuszczyk.rjarchitecture.abs.action.AbsAction;
import pl.rjuszczyk.rjarchitecture.main.model.MainItem;

public interface MainAction extends AbsAction {
    class MainActionStart implements MainAction {

    }

    class MainActionStop implements MainAction {

    }

    class MainActionError implements MainAction {
        private final Throwable throwable;

        public MainActionError(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }

    class MainActionLoaded implements MainAction {
        private final List<MainItem> loadedItems;

        public MainActionLoaded(List<MainItem> loadedItems) {
            this.loadedItems = loadedItems;
        }

        public List<MainItem> getLoadedItems() {
            return loadedItems;
        }
    }
}
