package pw.haze.genetics

import com.sun.javafx.geom.Vec2f
import com.sun.javafx.geom.Vec4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage
import org.lwjgl.util.IOUtils
import java.io.File
import java.nio.ByteBuffer

/**
 * |> Author: haze
 * |> Since: 4/15/16
 */
class Image(val path: File) {

    var image: ByteBuffer? = null
    var w: Int = -1
    var h: Int = -1
    var comp: Int = -1

    fun bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GL11.glGenTextures())
    }

    fun preLoop() {
        if ( comp == 3 ) {
            if ( (w and 3) != 0 )
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2 - (w and 1));
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
        } else {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    }

    fun draw(pos: Vec2f, scale: Float) {
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.x, pos.y, 1F)
        GL11.glScalef(scale, scale, 1f);
        GL11.glColor4f(1F, 1F, 1F, 1F)
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(0.0f, 0.0f);

        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(w.toFloat(), 0.0f);

        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(w.toFloat(), h.toFloat());

        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(0.0f, h.toFloat());
        GL11.glEnd();

        GL11.glPopMatrix();
    }

    fun load() {
        try {
            val imageBuff = IOUtils.ioResourceToByteBuffer(path.absolutePath, 8192)
            val w = BufferUtils.createIntBuffer(1)
            val h = BufferUtils.createIntBuffer(1)
            val comp = BufferUtils.createIntBuffer(1)
            image = STBImage.stbi_load_from_memory(imageBuff, w, h, comp, 0) ?: throw RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason())
            this.w = w.get(0)
            this.h = h.get(0)
            this.comp = comp.get(0)

        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}