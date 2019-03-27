package gregtech.common.multipart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TileMultipart;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.common.pipelike.cable.Insulation;
import gregtech.common.pipelike.cable.WireProperties;
import gregtech.common.pipelike.cable.tile.CableEnergyContainer;
import gregtech.common.render.CableRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CableMultiPart extends PipeMultiPart<Insulation, WireProperties> {

    private CableEnergyContainer energyContainer;

    CableMultiPart() {
    }

    public CableMultiPart(IPipeTile<Insulation, WireProperties> sourceTile) {
        super(sourceTile);
    }

    @Override
    public ResourceLocation getType() {
        return GTMultipartFactory.CABLE_PART_KEY;
    }

    @Override
    protected PipeMultiPart<Insulation, WireProperties> toTickablePart() {
        return new CableMultiPartTickable(this);
    }

    @Override
    public boolean supportsTicking() {
        return false;
    }

    public CableEnergyContainer getEnergyContainer() {
        if (energyContainer == null) {
            this.energyContainer = new CableEnergyContainer(this);
        }
        return energyContainer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapabilityInternal(Capability<T> capability, EnumFacing facing) {
        if (capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER) {
            return (T) getEnergyContainer();
        }
        return super.getCapabilityInternal(capability, facing);
    }

    @SideOnly(Side.CLIENT)
    public boolean renderStatic(Vector3 pos, BlockRenderLayer layer, CCRenderState ccrs) {
        if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
            TileMultipart tileMultipart = tile();
            int brightness = tileMultipart.getWorld().getBlockState(tileMultipart.getPos()).getPackedLightmapCoords(tileMultipart.getWorld(), tileMultipart.getPos());
            ccrs.brightness = brightness;
            CableRenderer.INSTANCE.renderCableBlock(getPipeMaterial(), getPipeType(), getInsulationColor(), ccrs,
                new IVertexOperation[]{new Translation(pos)},
                activeConnections & ~getBlockedConnections());
            ccrs.brightness = brightness;
            getCoverableImplementation().renderCovers(ccrs, new Matrix4().translate(pos), new IVertexOperation[0]);
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getParticleTexture() {
        return CableRenderer.INSTANCE.getParticleTexture(this);
    }
}
