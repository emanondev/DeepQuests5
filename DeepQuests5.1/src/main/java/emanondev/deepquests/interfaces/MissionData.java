package emanondev.deepquests.interfaces;

public interface MissionData<T extends User<T>> extends ComplexData<T> {

	public Mission<T> getMission();


}
