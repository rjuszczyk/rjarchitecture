package pl.rjuszczyk.rjarchitecture.main.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;

public class MainRepository {
    public Single<List<MainItem>> loadMainItems() {
        List<MainItem> fakeList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            fakeList.add(new MainItem(String.format("Item %s", i)));
        }
        return Single.just(fakeList).delay(15, TimeUnit.SECONDS);
    }
}
