package name.modid;

import net.fabricmc.api.ClientModInitializer;

public class PacifistRouteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModEntities.registerEntities();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}