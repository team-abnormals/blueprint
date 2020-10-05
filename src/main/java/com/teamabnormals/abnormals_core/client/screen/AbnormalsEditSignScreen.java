package com.teamabnormals.abnormals_core.client.screen;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamabnormals.abnormals_core.client.tile.AbnormalsSignTileEntityRenderer;
import com.teamabnormals.abnormals_core.common.blocks.sign.AbnormalsStandingSignBlock;
import com.teamabnormals.abnormals_core.common.tileentity.AbnormalsSignTileEntity;
import com.teamabnormals.abnormals_core.core.util.NetworkUtil;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AbnormalsEditSignScreen extends Screen {
	private final AbnormalsSignTileEntityRenderer.SignModel signModel = new AbnormalsSignTileEntityRenderer.SignModel();
	private final AbnormalsSignTileEntity tileSign;
	private int updateCounter;
	private int editLine;
	private TextInputUtil textInputUtil;
	private final String[] field_238846_r_ = Util.make(new String[4], (array) -> {
		Arrays.fill(array, "");
	});

	public AbnormalsEditSignScreen(AbnormalsSignTileEntity sign) {
		super(new TranslationTextComponent("sign.edit"));
		this.tileSign = sign;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, DialogTexts.GUI_DONE, (p_238847_1_) -> {
			this.close();
		}));
		this.tileSign.setEditable(false);
		this.textInputUtil = new TextInputUtil(() -> {
			return this.field_238846_r_[this.editLine];
		}, (p_238850_1_) -> {
			this.field_238846_r_[this.editLine] = p_238850_1_;
			this.tileSign.setText(this.editLine, new StringTextComponent(p_238850_1_));
		}, TextInputUtil.getClipboardTextSupplier(this.minecraft), TextInputUtil.getClipboardTextSetter(this.minecraft), (p_238848_1_) -> {
			return this.minecraft.fontRenderer.getStringWidth(p_238848_1_) <= 90;
		});
	}

	@Override
	public void onClose() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);
		NetworkUtil.setNewSignText(this.tileSign.getPos(), this.tileSign.getText(0), this.tileSign.getText(1), this.tileSign.getText(2), this.tileSign.getText(3));

		this.tileSign.setEditable(true);
	}

	@Override
	public void tick() {
		this.updateCounter++;
		if (!this.tileSign.getType().isValidBlock(this.tileSign.getBlockState().getBlock())) {
			this.close();
		}
	}

	private void close() {
		this.tileSign.markDirty();
		this.minecraft.displayGuiScreen(null);
	}

	@Override
	public boolean charTyped(char char1, int char2) {
		this.textInputUtil.putChar(char1);
		return true;
	}

	@Override
	public void closeScreen() {
		this.close();
	}

	@Override
	public boolean keyPressed(int firstKey, int secondKey, int thirdKey) {
		if (firstKey == 265) {
			this.editLine = this.editLine - 1 & 3;
			this.textInputUtil.moveCursorToEnd();
			return true;
		} else if (firstKey != 264 && firstKey != 257 && firstKey != 335) {
			return this.textInputUtil.specialKeyPressed(firstKey) ? true : super.keyPressed(firstKey, secondKey, thirdKey);
		} else {
			this.editLine = this.editLine + 1 & 3;
			this.textInputUtil.moveCursorToEnd();
			return true;
		}
	}

	@Override
	public void render(MatrixStack matrixstack, int buttonR, int buttonG, float buttonB) {
		RenderHelper.setupGuiFlatDiffuseLighting();
		this.renderBackground(matrixstack);
		drawCenteredString(matrixstack, this.font, this.title, this.width / 2, 40, 16777215);
		matrixstack.push();
		matrixstack.translate((double) (this.width / 2), 0.0D, 50.0D);
		matrixstack.scale(93.75F, -93.75F, 93.75F);
		matrixstack.translate(0.0D, -1.3125D, 0.0D);
		BlockState blockstate = this.tileSign.getBlockState();
		boolean flag = blockstate.getBlock() instanceof AbnormalsStandingSignBlock;
		if (!flag) {
			matrixstack.translate(0.0D, -0.3125D, 0.0D);
		}

		boolean flag1 = this.updateCounter / 6 % 2 == 0;
		matrixstack.push();
		matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.getRenderTypeBuffers().getBufferSource();
		IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(RenderType.getEntityCutoutNoCull(AbnormalsSignTileEntityRenderer.getTexture(blockstate)));
		this.signModel.signBoard.render(matrixstack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
		if (flag) {
			this.signModel.signStick.render(matrixstack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
		}

		matrixstack.pop();
		matrixstack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
		matrixstack.scale(0.010416667F, -0.010416667F, 0.010416667F);

		int i = this.tileSign.getTextColor().getTextColor();
		int j = this.textInputUtil.getEndIndex();
		int k = this.textInputUtil.getStartIndex();
		int l = this.editLine * 10 - this.field_238846_r_.length * 5;
		Matrix4f matrix4f = matrixstack.getLast().getMatrix();

		for (int i1 = 0; i1 < this.field_238846_r_.length; ++i1) {
			String s = this.field_238846_r_[i1];
			if (s != null) {
				if (this.font.getBidiFlag()) {
					s = this.font.bidiReorder(s);
				}

				float f3 = (float) (-this.minecraft.fontRenderer.getStringWidth(s) / 2);
				this.minecraft.fontRenderer.func_238411_a_(s, f3, (float) (i1 * 10 - this.field_238846_r_.length * 5), i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880, false);
				if (i1 == this.editLine && j >= 0 && flag1) {
					int j1 = this.minecraft.fontRenderer.getStringWidth(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
					int k1 = j1 - this.minecraft.fontRenderer.getStringWidth(s) / 2;
					if (j >= s.length()) {
						this.minecraft.fontRenderer.func_238411_a_("_", (float) k1, (float) l, i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880, false);
					}
				}
			}
		}

		irendertypebuffer$impl.finish();

		for (int i3 = 0; i3 < this.field_238846_r_.length; ++i3) {
			String s1 = field_238846_r_[i3];
			if (s1 != null && i3 == this.editLine && k >= 0) {
				int j3 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
				int k3 = j3 - this.minecraft.fontRenderer.getStringWidth(s1) / 2;
				if (flag1 && j < s1.length()) {
					fill(matrixstack, k3, l - 1, k3 + 1, l + 9, -16777216 | i);
				}

				if (k != j) {
					int l3 = Math.min(j, k);
					int l1 = Math.max(j, k);
					int i2 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, l3)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2;
					int j2 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, l1)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2;
					int k2 = Math.min(i2, j2);
					int l2 = Math.max(i2, j2);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuffer();
					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
					bufferbuilder.pos(matrix4f, (float) k2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.pos(matrix4f, (float) l2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.pos(matrix4f, (float) l2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.pos(matrix4f, (float) k2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.finishDrawing();
					WorldVertexBufferUploader.draw(bufferbuilder);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrixstack.pop();
		RenderHelper.setupGui3DDiffuseLighting();
		super.render(matrixstack, buttonR, buttonG, buttonB);
	}
}