package co.etornam.familytracker.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import co.etornam.familytracker.helpers.AvatarBitmap;

public class AvatarBitmapWorker extends AsyncTask<String, Void, Bitmap> {
	public String path;
	private WeakReference<ImageView> imageViewWeakReference;
	private Context mContext;
	private int reqWidth, reqHeight;

	public AvatarBitmapWorker(ImageView imageView, Context context) {
		imageViewWeakReference = new WeakReference<>(imageView);
		mContext = context;
		reqWidth = imageView.getMaxWidth();
		reqHeight = imageView.getMaxHeight();

	}

	public static Bitmap decodeSampleBitmapFromResource(String path, int reqWidth, int reqHeight) {

		BitmapFactory.Options options = new BitmapFactory.Options();

//        dont decode / convert to bitmpa yet
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(path, options);

//        Calculate the InSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

//        Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;


		return BitmapFactory.decodeFile(path, options);

	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        Raw Height and Width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;


		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

//            calculate the largest inSampleSize value that is a power of 2 and keeps both
//            height and width larger than the requested height and width;
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		path = params[0];

		new AvatarBitmap(decodeSampleBitmapFromResource(path, this.reqWidth, this.reqHeight));
		return AvatarBitmap.getBitmap();
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {

		if (imageViewWeakReference != null && bitmap != null) {
			ImageView imageView = imageViewWeakReference.get();
			imageView.setImageBitmap(bitmap);
		}

	}
}

