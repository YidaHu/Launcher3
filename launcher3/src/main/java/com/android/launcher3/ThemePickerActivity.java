package com.android.launcher3;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.widget.AdapterView;
import android.app.WallpaperManager;
import android.graphics.BitmapFactory;

public class ThemePickerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.theme_picker);
		removeSharedPreference();
		getThemeBitmaps("mnt/sdcard/theme_thumbs");
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			                        int position, long id) {
				System.out.println("mThemesNames.size() -- " + mThemesNames.size());
				WallpaperManager wallpaperManager = WallpaperManager.getInstance(ThemePickerActivity.this);
				if (position == mThemesNames.size()) {
					getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
							Context.MODE_PRIVATE).edit().putString("theme_key", "default").commit();
					try {
						wallpaperManager.setResource(android.content.res.Resources.getSystem().getIdentifier("default_wallpaper", "drawable", "android"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					android.os.Process.killProcess(android.os.Process.myPid());
				} else if (isFileEffect("mnt/sdcard/theme/" + mThemesNames.get(position))) {
					getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
							Context.MODE_PRIVATE).edit().putString("theme_key", mThemesNames.get(position)).commit();
					try {
//						removeSharedPreference();
						setSharedPreference(mThemesNames.get(position).toString());
						Log.i("SharedhemesNames", mThemesNames.get(position).toString());
						wallpaperManager.setBitmap(BitmapFactory.decodeFile("mnt/sdcard/theme/" + mThemesNames.get(position) + "/" + mThemesNames.get(position) + "_wallpaper.jpg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					android.os.Process.killProcess(android.os.Process.myPid());
				} else {
//down load this theme
				}
			}
		});
	}

	private List<String> mThemesNames = null;
	private List<Bitmap> mThemesBitmaps = null;

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mThemesNames.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position == mThemesNames.size())
				return BitmapFactory.decodeResource(ThemePickerActivity.this.getResources(), R.drawable.theme_img_1);
			else
				return mThemesBitmaps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(280, 478));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}
			if (position == mThemesNames.size())
				imageView.setImageBitmap(BitmapFactory.decodeResource(ThemePickerActivity.this.getResources(), R.drawable.theme_img_1));
			else
				imageView.setImageBitmap(mThemesBitmaps.get(position));
			return imageView;
		}


	}

	private void getThemeBitmaps(String path) {
		mThemesNames = new ArrayList<String>();
		mThemesBitmaps = new ArrayList<Bitmap>();
		List<String> themetitles = new ArrayList<String>();
		File pathFile = new File(path);
		if (pathFile.exists() && pathFile.isDirectory()) {
			String[] fileNames = pathFile.list();
			if (fileNames.length <= 0)
				return;
			for (String filename : fileNames) {
				File subFile = new File(path + "/" + filename);
				if (subFile.exists() && (!subFile.isDirectory()) && subFile.getName().endsWith(".png")) {
					Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(path + "/" + filename);
					String namekey = trimExtension(filename);
					if (bitmap != null && (namekey != null)) {
						mThemesNames.add(namekey);
						mThemesBitmaps.add(bitmap);
					}
				}
			}
		}
	}

	private String trimExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length()))) {
				return filename.substring(0, i);
			}
		}
		return null;
	}

	private boolean isFileEffect(String name) {
		File file = new File(name);
		if (file.exists() && file.isDirectory() && (file.list().length > 0))
			return true;
		else
			return false;

	}

	public void setSharedPreference(String str) {
		sharedPreferences = getSharedPreferences("ThemeName", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("name", str);
		editor.commit();// 提交修改
	}

	// 清除sharedpreferences的数据
	public void removeSharedPreference() {
		sharedPreferences = getSharedPreferences("ThemeName", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove("name");
		editor.commit();// 提交修改
	}

	SharedPreferences sharedPreferences;


}