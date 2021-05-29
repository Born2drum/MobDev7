package ua.kpi.comsys.iv8224.app_mobiles.ui.gallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

import ua.kpi.comsys.iv8224.app_mobiles.R;
import ua.kpi.comsys.iv8224.app_mobiles.db.App;
import ua.kpi.comsys.iv8224.app_mobiles.db.AppDatabase;
import ua.kpi.comsys.iv8224.app_mobiles.db.Gallery;


public class GalleryFragment extends Fragment {

    private static final String KEY = "19193969-87191e5db266905fe8936d565";
    private static final int COUNT = 30;

    @SuppressLint("StaticFieldLeak")
    private static View root;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout scrollMain;
    private static ArrayList<ImageView> allImages;
    private static ArrayList<ArrayList<Object>> placeholderList;
    private static AppDatabase appDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);

        scrollMain = root.findViewById(R.id.linear_main);

        allImages = new ArrayList<>();
        placeholderList = new ArrayList<>();

        appDatabase = App.getInstance().getDatabase();

        AsyncLoadImages aTask = new AsyncLoadImages();
        aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "fun+party");

        return root;
    }


    protected static void loadImages(ArrayList<String> images){
        if (images != null) {
            for (String img : images) {
                new AsyncLoadBitmapToDB().execute(img);
                addImage(img);
            }
        } else {
            Toast.makeText(root.getContext(),
                    "Unable to load data for some reason (check Internet connection)",
                    Toast.LENGTH_LONG).show();
        }
    }

    private static void addImage(String imageUrl) {
        ProgressBar imageProgressBar = new ProgressBar(root.getContext());
        imageProgressBar.setLayoutParams(
                new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        imageProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(root.getContext(), R.color.purple_500),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        imageProgressBar.setVisibility(View.GONE);
        imageProgressBar.setId(imageProgressBar.hashCode());

        ImageView newImage = new ImageView(root.getContext());

        imageProgressBar.setVisibility(View.VISIBLE);
        new AsyncLoadImageFromDB(newImage, imageProgressBar).execute(imageUrl);
       // new NotificationsFragment.DownloadImageTask(newImage, imageProgressBar, root.getContext()).execute(imageUrl);

        newImage.setBackgroundColor(Color.GRAY);
        ConstraintLayout.LayoutParams imageParams =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        imageParams.dimensionRatio = "1";
        newImage.setLayoutParams(imageParams);
        newImage.setId(newImage.hashCode());
        setImagePlace(newImage, imageProgressBar);

        allImages.add(newImage);
    }

    private static void setImagePlace(ImageView newImage, ProgressBar progressBar) {
        ConstraintLayout tmpLayout = null;
        ConstraintSet tmpSet = null;
        if (allImages.size() > 0) {
            tmpLayout = (ConstraintLayout) getConstraintArrayList(0, placeholderList);
            if (allImages.size() % 10 != 0) {
                tmpLayout.addView(newImage);
                tmpLayout.addView(progressBar);
            }
            tmpSet = (ConstraintSet) getConstraintArrayList(1, placeholderList);

            tmpSet.clone(tmpLayout);

            tmpSet.setMargin(newImage.getId(), ConstraintSet.START, 3);
            tmpSet.setMargin(newImage.getId(), ConstraintSet.TOP, 3);
            tmpSet.setMargin(newImage.getId(), ConstraintSet.END, 3);
            tmpSet.setMargin(newImage.getId(), ConstraintSet.BOTTOM, 3);

            tmpSet.connect(progressBar.getId(), ConstraintSet.START, newImage.getId(), ConstraintSet.START);
            tmpSet.connect(progressBar.getId(), ConstraintSet.TOP, newImage.getId(), ConstraintSet.TOP);
            tmpSet.connect(progressBar.getId(), ConstraintSet.END, newImage.getId(), ConstraintSet.END);
            tmpSet.connect(progressBar.getId(), ConstraintSet.BOTTOM, newImage.getId(), ConstraintSet.BOTTOM);
        }

        switch (allImages.size() % 10){
            case 0:{
                placeholderList.add(new ArrayList<>());

                ConstraintLayout newConstraint = new ConstraintLayout(root.getContext());
                placeholderList.get(placeholderList.size()-1).add(newConstraint);
                newConstraint.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                scrollMain.addView(newConstraint);

                Guideline vertical_25 = makeGuideline(ConstraintLayout.LayoutParams.VERTICAL,
                        0.25f);
                Guideline vertical_50 = makeGuideline(ConstraintLayout.LayoutParams.VERTICAL,
                        0.5f);
                Guideline vertical_75 = makeGuideline(ConstraintLayout.LayoutParams.VERTICAL,
                        0.75f);

                Guideline horizontal_25 = makeGuideline(ConstraintLayout.LayoutParams.HORIZONTAL,
                        1f);
                Guideline horizontal_50 = makeGuideline(ConstraintLayout.LayoutParams.HORIZONTAL,
                        1f);
                Guideline horizontal_75 = makeGuideline(ConstraintLayout.LayoutParams.HORIZONTAL,
                        1f);

                newConstraint.addView(vertical_25, 0);
                newConstraint.addView(vertical_50, 1);
                newConstraint.addView(vertical_75, 2);
                newConstraint.addView(horizontal_25, 3);
                newConstraint.addView(horizontal_50, 4);
                newConstraint.addView(horizontal_75, 5);

                newConstraint.addView(newImage);
                newConstraint.addView(progressBar);

                ConstraintSet newConstraintSet = new ConstraintSet();
                placeholderList.get(placeholderList.size()-1).add(newConstraintSet);
                newConstraintSet.clone(newConstraint);

                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.START, 3);
                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.TOP, 3);
                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.END, 3);
                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.BOTTOM, 3);

                connectInConstraint(newConstraintSet, newImage.getId(),
                        ConstraintSet.PARENT_ID, ConstraintSet.PARENT_ID,
                        vertical_25.getId(), horizontal_25.getId());
                connectInConstraint(newConstraintSet, progressBar.getId(),
                        ConstraintSet.PARENT_ID, ConstraintSet.PARENT_ID,
                        vertical_25.getId(), horizontal_25.getId());

                newConstraintSet.applyTo(newConstraint);
                break;
            }

            case 1: {
                tmpSet.setGuidelinePercent(tmpLayout.getChildAt(3).getId(), 0.5f);

                connectInConstraint(tmpSet, newImage.getId(),
                        tmpLayout.getChildAt(0).getId(), ConstraintSet.PARENT_ID,
                        tmpLayout.getChildAt(2).getId(), tmpLayout.getChildAt(4).getId());

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 2: {
                connectInConstraint(tmpSet, newImage.getId(),
                        tmpLayout.getChildAt(2).getId(), ConstraintSet.PARENT_ID,
                        ConstraintSet.PARENT_ID, tmpLayout.getChildAt(3).getId());

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 3: {
                connectInConstraint(tmpSet, newImage.getId(),
                        ConstraintSet.PARENT_ID, tmpLayout.getChildAt(3).getId(),
                        tmpLayout.getChildAt(0).getId(), tmpLayout.getChildAt(4).getId());

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 4: {
                connectInConstraint(tmpSet, newImage.getId(),
                        tmpLayout.getChildAt(2).getId(), tmpLayout.getChildAt(3).getId(),
                        ConstraintSet.PARENT_ID, tmpLayout.getChildAt(4).getId());

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 5: {
                tmpSet.setGuidelinePercent(tmpLayout.getChildAt(3).getId(), 0.25f);
                tmpSet.setGuidelinePercent(tmpLayout.getChildAt(4).getId(), 0.5f);
                tmpSet.setGuidelinePercent(tmpLayout.getChildAt(5).getId(), 0.75f);

                connectInConstraint(tmpSet, newImage.getId(),
                        ConstraintSet.PARENT_ID, tmpLayout.getChildAt(4).getId(),
                        tmpLayout.getChildAt(1).getId(), ConstraintSet.PARENT_ID);

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 6: {
                connectInConstraint(tmpSet, newImage.getId(),
                        tmpLayout.getChildAt(1).getId(), tmpLayout.getChildAt(4).getId(),
                        tmpLayout.getChildAt(2).getId(), tmpLayout.getChildAt(5).getId());

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 7: {
                connectInConstraint(tmpSet, newImage.getId(),
                        tmpLayout.getChildAt(2).getId(), tmpLayout.getChildAt(4).getId(),
                        ConstraintSet.PARENT_ID, tmpLayout.getChildAt(5).getId());

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 8: {
                connectInConstraint(tmpSet, newImage.getId(),
                        tmpLayout.getChildAt(1).getId(), tmpLayout.getChildAt(5).getId(),
                        tmpLayout.getChildAt(2).getId(), ConstraintSet.PARENT_ID);

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 9: {
                connectInConstraint(tmpSet, newImage.getId(),
                        tmpLayout.getChildAt(2).getId(), tmpLayout.getChildAt(5).getId(),
                        ConstraintSet.PARENT_ID, ConstraintSet.PARENT_ID);

                tmpSet.applyTo(tmpLayout);
                break;
            }
        }
    }

    private static void connectInConstraint(ConstraintSet constraintSet, int mainView,
                                            int startView, int topView,
                                            int endView, int bottomView){
        constraintSet.connect(mainView, ConstraintSet.START,
                startView, startView == ConstraintSet.PARENT_ID ?
                                ConstraintSet.START : ConstraintSet.END);
        constraintSet.connect(mainView, ConstraintSet.TOP,
                topView, topView == ConstraintSet.PARENT_ID ?
                                ConstraintSet.TOP : ConstraintSet.BOTTOM);
        constraintSet.connect(mainView, ConstraintSet.END,
                endView, endView == ConstraintSet.PARENT_ID ?
                                ConstraintSet.END : ConstraintSet.START);
        constraintSet.connect(mainView, ConstraintSet.BOTTOM,
                bottomView, bottomView == ConstraintSet.PARENT_ID ?
                                ConstraintSet.BOTTOM : ConstraintSet.TOP);
    }

    private static Guideline makeGuideline(int orientation, float percent){
        Guideline guideline = new Guideline(root.getContext());
        guideline.setId(guideline.hashCode());

        ConstraintLayout.LayoutParams guideline_Params =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        guideline_Params.orientation = orientation;

        guideline.setLayoutParams(guideline_Params);

        guideline.setGuidelinePercent(percent);

        return guideline;
    }

    private static Object getConstraintArrayList(int index, ArrayList<ArrayList<Object>> list){
        return list.get(list.size()-1).get(index);
    }


    private static class AsyncLoadImageFromDB extends AsyncTask<String, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        ImageView imageView;
        @SuppressLint("StaticFieldLeak")
        ProgressBar progressBar;
        Bitmap bitmap;

        public AsyncLoadImageFromDB(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                bitmap = appDatabase.galleryDao().getByUrl(urls[0]).getBitmapImage();
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private static class AsyncLoadBitmapToDB extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            Bitmap mIcon11;
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                if (appDatabase.galleryDao().getByUrl(urls[0]) != null &&
                        appDatabase.galleryDao().getByUrl(urls[0]).imageData != null)
                    mIcon11 = appDatabase.galleryDao().getByUrl(urls[0]).getBitmapImage();
                else
                    mIcon11 = BitmapFactory.decodeResource(root.getContext().getResources(),
                            android.R.drawable.ic_media_play);
            }
            if (mIcon11 != null)
                appDatabase.galleryDao().setImageBitmapByUrl(urls[0],
                        Gallery.getBitmapAsByteArray(mIcon11));
            return null;
        }
    }

    private static class AsyncLoadImages extends AsyncTask<String, Void, ArrayList<String>> {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            return search(strings[0]);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(ArrayList<String> images) {
            super.onPostExecute(images);

            if (images == null || images.size() == 0) {
                Toast.makeText(root.getContext(), "Unable to load images for some reason",
                        Toast.LENGTH_SHORT).show();
            }

            GalleryFragment.loadImages(images);
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        private ArrayList<String> search(String request){
            String jsonResponse = String.format("https://pixabay.com/api/?key=%s&q=%s&image_type=photo&per_page=%d",
                    KEY, request, COUNT);
            try {
                return parseImages(getRequest(jsonResponse));
            } catch (UnknownHostException e) {
                System.err.println(String.format("Request timeout!", jsonResponse));
                return (ArrayList<String>) appDatabase.galleryDao().getAllUrls();
            } catch (MalformedURLException e) {
                System.err.println(String.format("Incorrect URL <%s>!", jsonResponse));
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                System.err.println("Some trouble occurred when parsing Json file.");
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<String> parseImages(String jsonText) throws ParseException {
            ArrayList<String> result = new ArrayList<>();

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

            JSONArray images = (JSONArray) jsonObject.get("hits");
            for (Object img : images) {
                JSONObject tmp = (JSONObject) img;

                String url = (String) tmp.get("webformatURL");

                Gallery gallery = new Gallery(
                        (Long) tmp.get("id"), url, null);
                appDatabase.galleryDao().insert(gallery);

                result.add(url);
            }

            return result;
        }

        private String getRequest(String url) throws IOException {
            StringBuilder result = new StringBuilder();

            URL getReq = new URL(url);
            URLConnection connection = getReq.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                result.append(inputLine).append("\n");

            in.close();

            return result.toString();
        }
    }
}

