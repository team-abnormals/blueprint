package com.teamabnormals.blueprint.client;

import com.teamabnormals.blueprint.core.Blueprint;
import com.teamabnormals.blueprint.core.BlueprintConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;

public enum PlushCampaignHandler {
    INSTANCE;

    private static final ResourceLocation CAMPAIGN_TEXTURE = new ResourceLocation(Blueprint.MOD_ID, "textures/plush_campaign.png");
    private static final long CAMPAIGN_END_MILLIS = 1778515200000L;
    private static final String[] ELIGIBLE_MOD_IDS = {
            "blueprint_test",
            "abnormals_delight",
            "allurement",
            "atmospheric",
            "autumnity",
            "berry_good",
            "boatload",
            "buzzier_bees",
            "caverns_and_chasms",
            "clayworks",
            "endergetic",
            "environmental",
            "gallery",
            "incubation",
            "neapolitan",
            "personality",
            "pet_cemetery",
            "savage_and_ravage",
            "upgrade_aquatic",
            "woodworks"
    };

    private boolean campaignOpened;

    private static boolean isEligible() {
        return !isCampaignOver() && Arrays.stream(ELIGIBLE_MOD_IDS).anyMatch(id -> ModList.get().isLoaded(id));
    }

    private static boolean isCampaignOver() {
        return CAMPAIGN_END_MILLIS < System.currentTimeMillis();
    }

    public void setup() {
        if (PlushCampaignHandler.isEligible()) MinecraftForge.EVENT_BUS.addListener(this::onScreenInit);
    }

    private void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen) || PlushCampaignHandler.isCampaignOver() || BlueprintConfig.CLIENT.disableSlabfishPlushCampaignValue.get())
            return;

        event.getListenersList().stream()
                .filter(listener -> listener instanceof AbstractWidget)
                .map(listener -> (AbstractWidget) listener)
                .filter(widget -> widget.getMessage().getContents() instanceof TranslatableContents contents && contents.getKey().equals("menu.online"))
                .findFirst()
                .ifPresent(widget -> event.addListener(new CampaignButton(widget.getX() + widget.getWidth() + 4, widget.getY(), Tooltip.create(Component.literal("plush")), button -> {
                    this.campaignOpened = true;
                    Minecraft.getInstance().setScreen(new CampaignScreen(event.getScreen()));
                })));
    }

    private final class CampaignButton extends Button {
        CampaignButton(int x, int y, Tooltip tooltip, Button.OnPress pressCallback) {
            super(x, y, 20, 20, GameNarrator.NO_TITLE, pressCallback, Button.DEFAULT_NARRATION);
            this.setTooltip(tooltip);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.blit(PlushCampaignHandler.CAMPAIGN_TEXTURE, this.getX(), this.getY(), 60, 15, 20, 20, 256, 256);

            if (!PlushCampaignHandler.this.campaignOpened) {
                guiGraphics.blit(PlushCampaignHandler.CAMPAIGN_TEXTURE, this.getX() + this.getWidth() - 5, this.getY() - 5, 60, 35, 10, 10, 256, 256);
            }
        }

        @Override
        public void renderString(GuiGraphics p_283366_, Font p_283054_, int p_281656_) {
        }
    }

    private final class CampaignScreen extends Screen {
        private static final int PANEL_TOP = 32;
        private static final int PANEL_WIDTH = 298;
        private static final int FOOTER_HEIGHT = 37;
        private static final int ACCENT_COLOR = 0x78C750;
        private static final String PROMO_LINK_URL = "https://makeship.com/petitions/slabfish-plush";
        private static final Component PROMO_LINK_COMPONENT = Component.literal("makeship.com/petitions/slabfish-plush")
                .withStyle(style -> style.withColor(ACCENT_COLOR));
        private static final Component BODY_TEXT = Component.empty()
                .append("Team Abnormals is working with Makeship to turn Slabfish into a physical plushie!")
                .append("\n\n")
                .append("In order for these to be put into production, we need to reach our petition goal of ")
                .append(Component.literal("200 supporters by May 11th").withStyle(style -> style.withColor(ACCENT_COLOR)))
                .append(".")
                .append("\n\n")
                .append("If you enjoy mods by Team Abnormals, please consider pledging!");
        private static final Component NEVER_SHOW_AGAIN_COMPONENT = Component.literal("Do not show this screen again");

        private final Screen parent;
        private Checkbox neverShowAgainCheckbox;
        private MultiLineLabel bodyLabel = MultiLineLabel.EMPTY;
        private int panelLeft;
        private int panelRight;
        private int panelBottom;
        private int centerX;
        private int titleY;
        private int headerX;
        private int headerY;
        private int promoX;
        private int promoY;
        private int bodyLabelY;
        private int promoLinkX;
        private int promoLinkY;
        private int promoLinkWidth;
        private int promoLinkHeight;

        CampaignScreen(Screen parent) {
            super(Component.literal("Slabfish Plush Petition"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.neverShowAgainCheckbox = new Checkbox(0, 0, this.font.width(NEVER_SHOW_AGAIN_COMPONENT) + 24, 20, NEVER_SHOW_AGAIN_COMPONENT, false);

            GridLayout footerLayout = new GridLayout().spacing(8);
            footerLayout.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
                        if (this.neverShowAgainCheckbox != null && this.neverShowAgainCheckbox.selected()) {
                            BlueprintConfig.CLIENT.disableSlabfishPlushCampaignValue.set(true);
                            BlueprintConfig.CLIENT_SPEC.save();
                            this.getMinecraft().setScreen(new TitleScreen());
                            return;
                        }
                        this.onClose();
                    })
                    .size(150, 20)
                    .build(), 0, 0);
            footerLayout.addChild(this.neverShowAgainCheckbox, 0, 1);
            footerLayout.arrangeElements();
            footerLayout.setPosition((this.width - footerLayout.getWidth()) / 2, (this.height - FOOTER_HEIGHT) + (FOOTER_HEIGHT - footerLayout.getHeight()) / 2);
            footerLayout.visitChildren(layoutElement -> {
                if (layoutElement instanceof AbstractWidget widget) this.addRenderableWidget(widget);
            });


            this.centerX = this.width / 2;

            this.panelLeft = this.centerX - (PANEL_WIDTH / 2);
            this.panelRight = this.panelLeft + PANEL_WIDTH;
            this.panelBottom = this.height - FOOTER_HEIGHT;
            this.titleY = (PANEL_TOP - this.font.lineHeight) / 2;

            this.headerX = this.centerX - 112;
            this.headerY = PANEL_TOP + (64 - 23) / 2;
            int headerBottom = this.headerY + 23;

            int promoWidth = 60;
            int promoHeight = 70;
            this.promoX = this.panelRight - (promoWidth / 2);
            this.promoY = this.panelBottom - promoHeight + 8;

            this.promoLinkWidth = this.font.width(PROMO_LINK_COMPONENT);
            this.promoLinkHeight = this.font.lineHeight;
            this.promoLinkX = this.centerX - (this.promoLinkWidth / 2);
            this.promoLinkY = this.promoY + (promoHeight - this.promoLinkHeight) / 2;

            this.bodyLabel = MultiLineLabel.create(this.font, BODY_TEXT, 220);
            int labelsHeight = this.bodyLabel.getLineCount() * this.font.lineHeight;
            this.bodyLabelY = headerBottom + (this.promoLinkY - headerBottom - labelsHeight) / 2;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            this.renderBackground(guiGraphics);

            guiGraphics.setColor(0.125F, 0.125F, 0.125F, 1.0F);
            guiGraphics.blit(Screen.BACKGROUND_LOCATION, this.panelLeft, PANEL_TOP, this.panelRight, this.panelBottom, PANEL_WIDTH, this.height, 32, 32);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            guiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
            guiGraphics.blit(Screen.BACKGROUND_LOCATION, 0, 0, 0.0F, 0.0F, this.width, PANEL_TOP, 32, 32);
            guiGraphics.blit(Screen.BACKGROUND_LOCATION, 0, this.panelBottom, 0.0F, this.panelBottom, this.width, FOOTER_HEIGHT, 32, 32);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.fillGradient(RenderType.guiOverlay(), this.panelLeft, PANEL_TOP, this.panelRight, PANEL_TOP + 4, -16777216, 0, 0);
            guiGraphics.fillGradient(RenderType.guiOverlay(), this.panelLeft, this.panelBottom - 4, this.panelRight, this.panelBottom, 0, -16777216, 0);

            guiGraphics.drawCenteredString(this.font, this.title, this.centerX, this.titleY, 16777215);


            guiGraphics.blit(PlushCampaignHandler.CAMPAIGN_TEXTURE, this.headerX, this.headerY, 224, 23, 0, 0, 149, 15, 256, 256);

            guiGraphics.blit(PlushCampaignHandler.CAMPAIGN_TEXTURE, this.promoX, this.promoY, 0, 15, 60, 70, 256, 256);

            this.bodyLabel.renderCentered(guiGraphics, this.centerX, this.bodyLabelY);

            guiGraphics.drawString(this.font,
                    mouseX >= this.promoLinkX && mouseX < this.promoLinkX + this.promoLinkWidth && mouseY >= this.promoLinkY && mouseY < this.promoLinkY + this.promoLinkHeight ?
                            PROMO_LINK_COMPONENT.copy().withStyle(ChatFormatting.UNDERLINE) :
                            PROMO_LINK_COMPONENT,
                    this.promoLinkX, this.promoLinkY, ACCENT_COLOR, false);


            super.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && mouseX >= this.promoLinkX && mouseX < this.promoLinkX + this.promoLinkWidth && mouseY >= this.promoLinkY && mouseY < this.promoLinkY + this.promoLinkHeight) {
                this.getMinecraft().setScreen(new ConfirmLinkScreen((confirm) -> {
                    if (confirm) {
                        Util.getPlatform().openUri(PROMO_LINK_URL);
                    }

                    this.getMinecraft().setScreen(this);
                }, PROMO_LINK_URL, true));
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void onClose() {
            this.getMinecraft().setScreen(this.parent);
        }
    }
}
