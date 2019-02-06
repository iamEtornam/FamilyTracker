package co.etornam.familytracker.helpers;

import android.graphics.Bitmap;

public class AvatarBitmap {
	public static Bitmap bitmap;

	public AvatarBitmap(Bitmap inBitmap) {
		bitmap = inBitmap;
	}

	public static Bitmap getBitmap() {
		return bitmap;
	}

	public static void setBitmap(Bitmap bitmap) {
		AvatarBitmap.bitmap = bitmap;
	}
}
