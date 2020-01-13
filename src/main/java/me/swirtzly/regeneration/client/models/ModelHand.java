package me.swirtzly.regeneration.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.function.Consumer;

public class ModelHand extends Model {

    private ModelRenderer arm = null;

    public ModelHand(boolean isAlex) {
        super();
        textureWidth = 64;
        textureHeight = 64;

        if (isAlex) {
            arm = new ModelRenderer(this);
            arm.setRotationPoint(-2.0F, 12.0F, 0.0F);
            setRotationAngle(arm, 0.0F, 0.0F, 3.1416F);
            arm.addCuboid(new ModelRenderer.ModelBox(arm, 40, 16, -3.0F, -11.5F, 0.0F, 3, 12, 4, 0.0F, false));
            arm.addCuboid(new ModelRenderer.ModelBox(arm, 40, 32, -3.0F, -11.5F, 0.0F, 3, 12, 4, 0.375F, false));
        } else {
            arm = new ModelRenderer(this);
            arm.setRotationPoint(-2.0F, 12.0F, 0.0F);
            setRotationAngle(arm, 0.0F, 0.0F, 3.1416F);
            arm.addCuboid(new ModelRenderer.ModelBox(40, 16, -3.0F, -11.5F, -2.0F, 3, 12, 4, 0.0F, false));
            arm.addCuboid(new ModelRenderer.ModelBox(arm, 40, 32, -3.0F, -11.5F, -2.0F, 3, 12, 4, 0.375F, false));
        }
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public Consumer<ModelRenderer> andThen(Consumer<? super ModelRenderer> after) {
        return null;
    }

    @Override
    public void render(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        arm.render();
    }
}
