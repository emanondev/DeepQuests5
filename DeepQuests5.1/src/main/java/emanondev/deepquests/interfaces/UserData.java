package emanondev.deepquests.interfaces;

public interface UserData<T extends User<T>> extends Navigable{

	public T getUser();

	public void reset();
	public void erase();
}
