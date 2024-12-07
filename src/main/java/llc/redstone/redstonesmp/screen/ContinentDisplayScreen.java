package llc.redstone.redstonesmp.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.screen.widget.ScrollingTextWidget;
import io.github.apace100.apoli.util.TextAlignment;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.badge.Badge;
import io.github.apace100.origins.mixin.DrawContextAccessor;
import io.github.apace100.origins.origin.OriginLayer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ContinentDisplayScreen extends Screen {

    private static final Identifier WINDOW_BACKGROUND = Origins.identifier("choose_origin/background");
    private static final Identifier WINDOW_BORDER = Origins.identifier("choose_origin/border");
    private static final Identifier WINDOW_NAME_PLATE = Origins.identifier("choose_origin/name_plate");
    private static final Identifier WINDOW_SCROLL_BAR = Origins.identifier("choose_origin/scroll_bar");
    private static final Identifier WINDOW_SCROLL_BAR_PRESSED = Origins.identifier("choose_origin/scroll_bar/pressed");
    private static final Identifier WINDOW_SCROLL_BAR_SLOT = Origins.identifier("choose_origin/scroll_bar/slot");

    protected static final int WINDOW_WIDTH = 176;
    protected static final int WINDOW_HEIGHT = 182;

    private final LinkedList<RenderedBadge> renderedBadges = new LinkedList<>();

    protected final boolean showDirtBackground;

    private String origin;
    private String prevOrigin;
    private OriginLayer layer;
    private OriginLayer prevLayer;
    private Text randomOriginText;
    private ScrollingTextWidget originNameWidget;

    private boolean refreshOriginNameWidget = false;

    private boolean isOriginRandom;
    private boolean dragScrolling = false;

    private double mouseDragStart = 0;
    private float time = 0;

    private int currentMaxScroll = 0;
    private int scrollDragStart = 0;

    protected int guiTop, guiLeft;
    protected int scrollPos = 0;


    public ContinentDisplayScreen(Text title, boolean showDirtBackground) {
        super(title);
        this.showDirtBackground = showDirtBackground;
    }

    public void showOrigin(String origin, boolean isRandom) {
        this.origin = origin;
        this.isOriginRandom = isRandom;
        this.scrollPos = 0;
        this.time = 0;
    }

    public void setRandomOriginText(Text text) {
        this.randomOriginText = text;
    }

    @Override
    protected void init() {

        super.init();

        this.guiLeft = (this.width - WINDOW_WIDTH) / 2;
        this.guiTop = (this.height - WINDOW_HEIGHT) / 2;

        this.originNameWidget = new ScrollingTextWidget(guiLeft + 38, guiTop + 18, WINDOW_WIDTH - (62 + 3 * 8), 9, Text.empty(), true, textRenderer);
        this.refreshOriginNameWidget = true;

    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (showDirtBackground) {
            super.renderBackground(context, mouseX, mouseY, delta);
        }

        else {
            this.renderInGameBackground(context);
        }

    }

    @Override
    public void renderInGameBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, -5, 1678774288, -2112876528);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        renderedBadges.clear();
        this.time += delta;

        super.render(context, mouseX, mouseY, delta);
        this.renderOriginWindow(context, mouseX, mouseY, delta);

        if (origin != null) {
            renderScrollbar(context, mouseX, mouseY);
            renderBadgeTooltip(context, mouseX, mouseY);
        }

    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);
        if (cannotScroll()) {
            return mouseClicked;
        }

        this.dragScrolling = false;

        int scrollBarY = 36;
        int maxScrollBarOffset = 141;

        scrollBarY += (int) ((maxScrollBarOffset - scrollBarY) * (scrollPos / (float) currentMaxScroll));
        if (!canDragScroll(mouseX, mouseY, scrollBarY)) {
            return mouseClicked;
        }

        this.dragScrolling = true;
        this.scrollDragStart = scrollBarY;
        this.mouseDragStart = mouseY;

        return true;

    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

        boolean mouseDragged = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if (!dragScrolling) {
            return mouseDragged;
        }

        int delta = (int) (mouseY - mouseDragStart);
        int newScrollPos = Math.max(36, Math.min(141, scrollDragStart + delta));

        float part = (newScrollPos - 36) / (float) (141 - 36);
        this.scrollPos = (int) (part * currentMaxScroll);

        return mouseDragged;

    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {

        int newScrollPos = this.scrollPos - (int) vertical * 4;
        this.scrollPos = MathHelper.clamp(newScrollPos, 0, this.currentMaxScroll);

        return super.mouseScrolled(x, y, horizontal, vertical);

    }

    public String getCurrentOrigin() {
        return origin;
    }

    protected void renderScrollbar(DrawContext context, int mouseX, int mouseY) {

        if (cannotScroll()) {
            return;
        }

        context.drawGuiTexture(WINDOW_SCROLL_BAR_SLOT, guiLeft + 155, guiTop + 35, 8, 134);

        int scrollbarY = 36;
        int maxScrollbarOffset = 141;

        scrollbarY += (int) ((maxScrollbarOffset - scrollbarY) * (scrollPos / (float) currentMaxScroll));

        Identifier scrollBarTexture = this.dragScrolling || canDragScroll(mouseX, mouseY, scrollbarY) ? WINDOW_SCROLL_BAR_PRESSED : WINDOW_SCROLL_BAR;
        context.drawGuiTexture(scrollBarTexture, guiLeft + 156, guiTop + scrollbarY, 6, 27);

    }

    protected boolean cannotScroll() {
        return origin == null || currentMaxScroll <= 0;
    }

    protected boolean canDragScroll(double mouseX, double mouseY, int scrollBarY) {
        return (mouseX >= guiLeft + 156 && mouseX < guiLeft + 156 + 6)
            && (mouseY >= guiTop + scrollBarY && mouseY < guiTop + scrollBarY + 27);
    }

    protected void renderBadgeTooltip(DrawContext context, int mouseX, int mouseY) {

        for (RenderedBadge renderedBadge : renderedBadges) {

            if (canRenderBadgeTooltip(renderedBadge, mouseX, mouseY)) {
                int widthLimit = width - mouseX - 24;
                ((DrawContextAccessor) context).invokeDrawTooltip(textRenderer, renderedBadge.getTooltipComponents(textRenderer, widthLimit), mouseX, mouseY, HoveredTooltipPositioner.INSTANCE);
            }

        }

    }

    protected boolean canRenderBadgeTooltip(RenderedBadge renderedBadge, int mouseX, int mouseY) {
        return renderedBadge.hasTooltip()
            && (mouseX >= renderedBadge.x && mouseX < renderedBadge.x + 9)
            && (mouseY >= renderedBadge.y && mouseY < renderedBadge.y + 9);
    }

    protected Text getTitleText() {
        return Text.of("Origins");
    }

    protected void renderOriginWindow(DrawContext context, int mouseX, int mouseY, float delta) {

        context.drawGuiTexture(WINDOW_BACKGROUND, guiLeft, guiTop, -4, WINDOW_WIDTH, WINDOW_HEIGHT);

        context.drawGuiTexture(WINDOW_BORDER, guiLeft, guiTop, 2, WINDOW_WIDTH, WINDOW_HEIGHT);
        context.drawGuiTexture(WINDOW_NAME_PLATE, guiLeft + 10, guiTop + 10, 2, 150, 26);

        if (origin != null) {

            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 5);

            this.renderOriginName(context, mouseX, mouseY, delta);

            context.getMatrices().pop();
            context.drawCenteredTextWithShadow(this.textRenderer, getTitleText(), width / 2, guiTop - 15, 0xFFFFFF);

        }

    }

    protected boolean isHoveringOverImpact(int mouseX, int mouseY) {
        return (mouseX >= guiLeft + 128 && mouseX <= guiLeft + 158)
            && (mouseY >= guiTop + 19 && mouseY <= guiTop + 27);
    }

    protected void renderOriginName(DrawContext context, int mouseX, int mouseY, float delta) {

        if (refreshOriginNameWidget || (origin != prevOrigin || layer != prevLayer)) {

            Text name = origin.isEmpty() && layer != null && layer.getMissingName() != null
                ? layer.getMissingName()
                : Text.of(origin);

            originNameWidget = new ScrollingTextWidget(guiLeft + 38, guiTop + 18, WINDOW_WIDTH - (62 + 3 * 8), 9, name, true, textRenderer);
            originNameWidget.setAlignment(TextAlignment.LEFT);

            refreshOriginNameWidget = false;

            prevOrigin = origin;
            prevLayer = layer;

        }

        originNameWidget.render(context, mouseX, mouseY, delta);

        //create a player skull
        ItemStack iconStack = new ItemStack(Item.byRawId(397));
        if (iconStack.isOf(Items.PLAYER_HEAD) && !iconStack.contains(DataComponentTypes.PROFILE)) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "globe");
            profile.getProperties().put("assets/origins/textures", new Property("assets/origins/textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzEyMDgxNjNkYmJlZmU2YTlmNGQ0ZTE5YzM5Yzg5ZDg0ZDVjYjI4OTdlNWQ5MjQ5ODNiMzhmNmFiOTQxNjMifX19"));
            iconStack.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));
        }

        context.drawItem(iconStack, guiLeft + 15, guiTop + 15);

    }

    protected class RenderedBadge {

        private final Power power;
        private final Badge badge;

        private final int x;
        private final int y;

        public RenderedBadge(Power power, Badge badge, int x, int y) {
            this.power = power;
            this.badge = badge;
            this.x = x;
            this.y = y;
        }

        public List<TooltipComponent> getTooltipComponents(TextRenderer textRenderer, int widthLimit) {
            return badge.getTooltipComponents(power, widthLimit, ContinentDisplayScreen.this.time, textRenderer);
        }

        public boolean hasTooltip() {
            return badge.hasTooltip();
        }

    }

}
