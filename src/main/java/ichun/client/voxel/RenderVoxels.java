package ichun.client.voxel;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import ichun.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class RenderVoxels extends Render
{
    //	public HashMap<ResourceLocation, BufferedImage> oriSkins = new HashMap<ResourceLocation, BufferedImage>();
    public HashMap<ResourceLocation, BufferedImage[]> restitchedSkins = new HashMap<ResourceLocation, BufferedImage[]>();
    public HashMap<ResourceLocation, int[]> restitchedSkinsId = new HashMap<ResourceLocation, int[]>();

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return AbstractClientPlayer.locationStevePng;
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        EntityTrail sd = (EntityTrail)entity;

        if(sd.parent == Minecraft.getMinecraft().renderViewEntity && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
        {
            return;
        }

        GL11.glPushMatrix();
//        GL11.glTranslatef((float)d, (float)d1, (float)d2);

        ArrayList<LocationInfo> loc = iChunUtil.proxy.trailTicker.getPlayerLocationInfo(sd.parent);

        ResourceLocation rl = sd.parent.getLocationSkin();

        if(loc.get(20).txLocation != null)
        {
            rl = loc.get(20).txLocation;
        }

        BufferedImage[] skins = restitchedSkins.get(rl);

        if(skins == null)
        {
            ITextureObject obj = Minecraft.getMinecraft().getTextureManager().getTexture(rl);
            if(obj instanceof ThreadDownloadImageData)
            {
                try
                {
                    ThreadDownloadImageData imgDat = (ThreadDownloadImageData)obj;
                    BufferedImage img = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imgDat, "field_110560_d", "bufferedImage");
                    if(img != null)
                    {
                        int[] imgId = new int[4];
                        skins = new BufferedImage[4];

                        int[] dimsX = new int[] { 4, 4, 8, 8 };
                        int[] dimsZ = new int[] { 4, 4, 4, 8 };
                        int[] dimsY = new int[] { 12, 12, 12, 8 };

                        int[] startX = new int[] { 0, 40, 16, 0 };
                        int[] startY = new int[] { 16, 16, 16, 0 };

                        for(int j = 0; j < dimsX.length; j++)
                        {
                            int[] dim = new int[] { dimsX[j], dimsY[j], dimsZ[j] };
                            int[] rots = new int[] { -90, 180, 0, 0, 90, 0, -90, 180, 				90 };
                            BufferedImage tmp = new BufferedImage(48, 24, 1);

                            Graphics2D gfx = tmp.createGraphics();

                            int[] xSource = new int[] { dim[2], dim[2], dim[2] + dim[0] + dim[2], 0, dim[2] + dim[0], dim[2] + dim[0], dim[2] + dim[0], dim[2] + dim[0], 				dim[2]};
                            int[] ySource = new int[] { 0, 0, dim[2], dim[2], 0, 0, 0, 0,  					0 };

                            int[] xCoord = new int[] { dim[0], dim[0] + dim[2] + dim[0] + dim[2], 0, dim[0] + dim[2] + dim[0] + dim[2] + dim[0], dim[0], dim[0] + dim[2], dim[0] + dim[2] + dim[0], dim[0] + dim[2] + dim[0] + dim[2], 				dim[2] + dim[0] + dim[2] };
                            int[] yCoord = new int[] { 0, 0, dim[2], dim[2], dim[2] + dim[1], dim[2] + dim[1], dim[2] + dim[1], dim[2] + dim[1], 				0 };

                            int[] dimX = new int[] { dim[0], dim[0], dim[0], dim[2], dim[0], dim[0], dim[0], dim[0], 					dim[0] };
                            int[] dimY = new int[] { dim[2], dim[2], dim[1], dim[1], dim[2], dim[2], dim[2], dim[2], 					dim[2] };

                            for(int i = 0; i < rots.length; i++)
                            {
                                if(i == rots.length - 1)
                                {
                                    gfx.drawImage(img, dim[0], 0, dim[0] + dim[2] + dim[0] + dim[2] + dim[0], dim[2] + dim[1], startX[j], startY[j], startX[j] + (2 * dim[0] + 2 * dim[2]), startY[j] + dim[1] + dim[2], null);
                                }

                                BufferedImage temp = img.getSubimage(startX[j] + xSource[i], startY[j] + ySource[i], dimX[i], dimY[i]); //new BufferedImage(img.getWidth(), img.getHeight(), 1);

                                BufferedImage temp1 = new BufferedImage(dimX[i], dimY[i], 1);

                                Graphics2D gfx1 = temp1.createGraphics();
                                gfx1.rotate(Math.toRadians(rots[i]), dimX[i] / 2, dimY[i] / 2);
                                //    			                gfx1.drawImage(temp, 0, 0, (Math.abs(rots[i]) == 90) ? dimY[i] : dimX[i], (Math.abs(rots[i]) == 90) ? dimX[i] : dimY[i], 0, 0, (Math.abs(rots[i]) == 90) ? dimY[i] : dimX[i], (Math.abs(rots[i]) == 90) ? dimX[i] : dimY[i], (ImageObserver)null);
                                gfx1.drawImage(temp, 0, 0, dimX[i], dimY[i], 0, 0, dimX[i], dimY[i], null);
                                gfx1.dispose();

                                gfx.drawImage(temp1, xCoord[i], yCoord[i], xCoord[i] + dimX[i], yCoord[i] + dimY[i], 0, 0, dimX[i], dimY[i], null);
                            }

                            imgId[j] = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), tmp);
                            skins[j] = tmp;
                            //			                try
                            //			                {
                            //			                    ImageIO.write(tmp, "png", new File(Minecraft.getMinecraft().mcDataDir, "test"+j+".png"));
                            //			                }
                            //			                catch (IOException ioexception)
                            //			                {
                            //			                    ioexception.printStackTrace();
                            //			                }
                        }

                        restitchedSkinsId.put(rl, imgId);
                        restitchedSkins.put(rl, skins);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        sd.model.renderPlayer(sd, entity.worldObj.getWorldTime(), entity.hashCode(), loc, d, d1, d2, 0.0625F, f1, restitchedSkinsId.get(rl));

        GL11.glPopMatrix();
    }
}
