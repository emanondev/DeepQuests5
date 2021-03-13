package emanondev.deepquests.interfaces;

import emanondev.deepquests.utils.StringUtils;

public interface HasCooldown<T extends User<T>> {

	public long getCooldownMinutes();
	public default long getCooldownTime() {
		return getCooldownMinutes()*60*1000;
	}

	public long getCooldownLeft(T user);
	public default String getStringCooldownLeft(T user) {
		return StringUtils.getStringCooldown(getCooldownLeft(user));
	}
	

	public boolean isRepeatable();
	public void setCooldownMinutes(long cooldown);

	public void setRepeatable(boolean value);

}
