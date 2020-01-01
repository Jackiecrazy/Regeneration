package me.swirtzly.regeneration.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

import java.util.function.Consumer;

public class ModelHand extends Model {

    private final ModelRenderer arm;

    public ModelHand(boolean isAlex) {
        super(ModelHand::);
        textureWidth = 64;
        textureHeight = 64;

        if (isAlex) {
            arm = new ModelRenderer(this);
            arm.setRotationPoint(-2.0F, 12.0F, 0.0F);
            setRotationAngle(arm, 0.0F, 0.0F, 3.1416F);
            arm.cubeList.add(new ModelRenderer.ModelBox(arm, 40, 16, -3.0F, -11.5F, 0.0F, 3, 12, 4, 0.0F, false));
            arm.cubeList.add(new ModelRenderer.ModelBox(arm, 40, 32, -3.0F, -11.5F, 0.0F, 3, 12, 4, 0.375F, false));
        } else {
            arm = new ModelRenderer(this);
            arm.setRotationPoint(-2.0F, 12.0F, 0.0F);
            setRotationAngle(arm, 0.0F, 0.0F, 3.1416F);
            arm.cubeList.add(new ModelRenderer.ModelBox(arm, 40, 16, -3.0F, -11.5F, -2.0F, 3, 12, 4, 0.0F, false));
            arm.cubeList.add(new ModelRenderer.ModelBox(arm, 40, 32, -3.0F, -11.5F, -2.0F, 3, 12, 4, 0.375F, false));
        }
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        arm.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3) {
        arm.func_228307_a_();
    }

    @Override
    public Consumer<ModelRenderer> andThen(Consumer<? super ModelRenderer> after) {
        return null;
    }
}
