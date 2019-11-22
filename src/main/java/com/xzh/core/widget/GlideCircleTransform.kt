package com.xzh.core.widget

import android.content.res.Resources
import android.graphics.*
import androidx.annotation.ColorInt
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * Created by xzh on 2019/8/21.
 * 圆形
 */
class GlideCircleTransform(
    borderWidth: Int = 0,
    @ColorInt val borderColor: Int = Color.TRANSPARENT
) : BitmapTransformation() {
    private var mBorderWidth: Float = Resources.getSystem().displayMetrics.density * borderWidth
    private var mBorderPaint: Paint

    init {

        mBorderPaint = Paint().apply {
            isDither = true

            color = borderColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = mBorderWidth
        }
    }

    override fun updateDiskCacheKey(md: MessageDigest) {
    }


    override fun transform(pool: BitmapPool, source: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val size = (Math.min(source.width, source.height) - mBorderWidth / 2).toInt()
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        // TODO this could be acquired from the pool too
        val squared = Bitmap.createBitmap(source, x, y, size, size)
        var result: Bitmap? = pool.get(size, size, Bitmap.Config.ARGB_8888)
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result!!)
        val paint = Paint()
        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        paint.isDither = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        val borderRadius = r - mBorderWidth / 2
        canvas.drawCircle(r, r, borderRadius, mBorderPaint)
        return result
    }

}