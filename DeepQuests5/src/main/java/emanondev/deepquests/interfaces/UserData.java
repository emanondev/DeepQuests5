package emanondev.deepquests.interfaces;

public interface UserData<T extends User<T>> extends Navigable {

    T getUser();

    void reset();

    void erase();
}
