package me.swirtzly.regeneration.client.skinhandling;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class SkinInfo {

	private SkinType skintype = SkinType.ALEX;
	private ResourceLocation textureLocation = DefaultPlayerSkin.getDefaultSkinLegacy();
    private boolean tick = false;

	public SkinType getSkintype() {
		return skintype;
	}

	public SkinInfo setSkintype(SkinType skintype) {
		this.skintype = skintype;
		return this;
	}

	public ResourceLocation getTextureLocation() {
		return textureLocation;
	}

	public SkinInfo setTextureLocation(ResourceLocation textureLocation) {
		this.textureLocation = textureLocation;
		return this;
	}

    public boolean istickRequired() {
        return tick;
    }

    public SkinInfo settickRequired(boolean tick) {
        this.tick = tick;
		return this;
	}

	public enum SkinType {
		ALEX("slim"), STEVE("default");

		private final String type;

		SkinType(String type) {
			this.type = type;
		}

		public String getMojangType() {
			return type;
		}
	}
	
}
