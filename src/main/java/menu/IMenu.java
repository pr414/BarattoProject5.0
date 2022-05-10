package menu;

import java.io.IOException;

public interface IMenu <T>{
	T start() throws LogoutInterrupt, IOException;
}
