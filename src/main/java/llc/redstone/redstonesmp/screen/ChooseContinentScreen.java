package llc.redstone.redstonesmp.screen;

import io.github.apace100.origins.Origins;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class ChooseContinentScreen extends ContinentDisplayScreen {

	private final List<String> layerList;
	private final List<String> originSelection;

	private final int currentLayerIndex;


	private int currentOriginIndex = 0;
	private int maxSelection = 0;


	public ChooseContinentScreen(List<String> layerList, int currentLayerIndex, boolean showDirtBackground) {
		super(Text.translatable(Origins.MODID + ".screen.choose_continent"), showDirtBackground);

		this.layerList = layerList;
		this.currentLayerIndex = currentLayerIndex;
		this.originSelection = new ArrayList<>(layerList.size());

		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) {
			return;
		}

		originSelection.addAll(getContinents());
		originSelection.add("random");

		maxSelection = 1;

		String newOrigin = getCurrentOrigin();
		showOrigin(newOrigin, Objects.equals(newOrigin, "random"));

	}

//	private void openNextLayerScreen() {
//		MinecraftClient.getInstance().setScreen(new WaitForNextLayerScreen(layerList, currentLayerIndex, this.showDirtBackground));
//	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {

		super.init();
		if (maxSelection <= 0) {
			return;
		}

		//	Draw the select origin button
		addDrawableChild(ButtonWidget.builder(
			Text.translatable(Origins.MODID + ".gui.select"),
			button -> {

				String continentId = super.getCurrentOrigin();

//				if (continentId.equals("random")) {
//					//	Choose a random continent
//					String randomContinent = getContinents().get(new Random().nextInt(getContinents().size()));
//					ClientPlayNetworking.send(new ChooseContinentC2SPacket(Origins.identifier(randomContinent)));
//				} else {
//					ClientPlayNetworking.send(new ChooseContinentC2SPacket(Origins.identifier(continentId)));
//				}

			}
		).dimensions(guiLeft + ContinentDisplayScreen.WINDOW_WIDTH / 2 - 50, guiTop + ContinentDisplayScreen.WINDOW_HEIGHT + 5, 100, 20).build());

		if (maxSelection <= 1) {
			return;
		}

		//	Draw the previous origin button
		addDrawableChild(ButtonWidget.builder(
			Text.of("<"),
			button -> {

				currentOriginIndex = (currentOriginIndex - 1 + maxSelection) % maxSelection;
				String newOrigin = originSelection.get(currentOriginIndex);

				showOrigin(newOrigin, Objects.equals(newOrigin, "random"));

			}
		).dimensions(guiLeft - 40, height / 2 - 10, 20, 20).build());

		//	Draw the next origin button
		addDrawableChild(ButtonWidget.builder(
			Text.of(">"),
			button -> {

				currentOriginIndex = (currentOriginIndex + 1) % maxSelection;
				String newOrigin = originSelection.get(currentOriginIndex);

				showOrigin(newOrigin, Objects.equals(newOrigin, "random"));
			}
		).dimensions(guiLeft + ContinentDisplayScreen.WINDOW_WIDTH + 20, height / 2 - 10, 20, 20).build());

	}


	public String getCurrentContinent() {

		if (currentOriginIndex == originSelection.size()) {
			return "random";
		}

		return originSelection.get(currentOriginIndex);

	}

	public static List<String> getContinents() {
		return Arrays.asList("africa", "asia", "europe", "north_america", "oceania", "south_america", "antarctica");
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
			super.render(context, mouseX, mouseY, delta);
	}

}
