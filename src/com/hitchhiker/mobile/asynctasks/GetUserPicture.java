package com.hitchhiker.mobile.asynctasks;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class GetUserPicture extends AsyncTask<String, Void, Bitmap> {
	ImageView profileImage;
	
	public GetUserPicture(ImageView profileImage) {
		this.profileImage = profileImage;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		String urldisplay = urls[0];
//		String urldisplay = "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/t1.0-1/c12.12.155.155/s100x100/1017407_558446464196372_97465039_a.jpg";
		Log.d("URRRRRLS from GetUserPicture", urldisplay);
	      Bitmap bitmap = null;
	      try {
	    	  URL url = new URL(urldisplay);
	    	  bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
	    	  HttpURLConnection.setFollowRedirects(true);
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	      return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {
			Log.d("Not null", "NOT NULLL");
		}
		profileImage.setImageBitmap(getRoundedCornerBitmap(result, 70));
	}
	
	protected static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint pnt = new Paint();
        pnt.setAntiAlias(true);
        pnt.setShader(bitmapShader);
        
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        

        return output;
    }
	
}