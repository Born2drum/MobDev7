package ua.kpi.comsys.iv8224.app_mobiles.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import ua.kpi.comsys.iv8224.app_mobiles.R;
import ua.kpi.comsys.iv8224.app_mobiles.db.App;
import ua.kpi.comsys.iv8224.app_mobiles.db.AppDatabase;


public class MovieInfo {

    private static final String KEY = "7e9fe69e";

    @SuppressLint("StaticFieldLeak")
    private static View popupView;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar detailsProgressBar;

    @SuppressLint("StaticFieldLeak")
    private static ImageView poster;

    private static Movie movie;
    private static AppDatabase appDatabase;


    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    public void showPopupWindow(final View view, Movie movie) {

        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popupView = inflater.inflate(R.layout.popup_movie_info, null);

        detailsProgressBar = popupView.findViewById(R.id.progress_bar_details);
        poster = popupView.findViewById(R.id.movie_info_image);

        MovieInfo.movie = movie;

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        appDatabase = App.getInstance().getDatabase();

        AsyncLoadMovieDetails aTask = new AsyncLoadMovieDetails();
        aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie.getImdbID());
    }

    protected static void setInfoData(){

        if (movie.getPoster() != null) {
            poster.setVisibility(View.INVISIBLE);
            detailsProgressBar.setVisibility(View.VISIBLE);
            new NotificationsFragment.DownloadImageTask(poster, detailsProgressBar,
                    popupView.getContext()).execute(movie.getPoster());
        }
        else {
            poster.setVisibility(View.INVISIBLE);
            detailsProgressBar.setVisibility(View.VISIBLE);
            poster.setImageBitmap(movie.getPosterBitmap());
            detailsProgressBar.setVisibility(View.GONE);
            poster.setVisibility(View.VISIBLE);
        }

        ((TextView) popupView.findViewById(R.id.movie_info_title))      .setText(movie.getTitle());
        ((TextView) popupView.findViewById(R.id.movie_info_year))       .setText(movie.getYear());
        ((TextView) popupView.findViewById(R.id.movie_info_released))   .setText(movie.getReleased());
        ((TextView) popupView.findViewById(R.id.movie_info_runtime))    .setText(movie.getRuntime());
        ((TextView) popupView.findViewById(R.id.movie_info_genre))      .setText(movie.getGenre());
        ((TextView) popupView.findViewById(R.id.movie_info_director))   .setText(movie.getDirector());
        ((TextView) popupView.findViewById(R.id.movie_info_actors))     .setText(movie.getActors());
        ((TextView) popupView.findViewById(R.id.movie_info_plot))       .setText(movie.getPlot());
        ((TextView) popupView.findViewById(R.id.movie_info_language))   .setText(movie.getLanguage());
        ((TextView) popupView.findViewById(R.id.movie_info_country))    .setText(movie.getCountry());
        ((TextView) popupView.findViewById(R.id.movie_info_awards))     .setText(movie.getAwards());
        ((TextView) popupView.findViewById(R.id.movie_info_rating))     .setText(movie.getRating());
        ((TextView) popupView.findViewById(R.id.movie_info_production)) .setText(movie.getProduction());
    }


    public static class AsyncLoadMovieDetailsToDB extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            appDatabase.movieDao().setDetailsByImdbId(movies[0].getImdbID(),
                    movies[0].getRated(),
                    movies[0].getReleased(),
                    movies[0].getRuntime(),
                    movies[0].getGenre(),
                    movies[0].getDirector(),
                    movies[0].getWriter(),
                    movies[0].getActors(),
                    movies[0].getPlot(),
                    movies[0].getLanguage(),
                    movies[0].getCountry(),
                    movies[0].getAwards(),
                    movies[0].getRating(),
                    movies[0].getVotes(),
                    movies[0].getProduction());

            return null;
        }
    }

    private static class AsyncLoadMovieDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MovieInfo.setInfoData();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Void doInBackground(String... strings) {
            search(strings[0]);
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void search(String imdbID) {
            String jsonUrl = String.format("http://www.omdbapi.com/?apikey=%s&i=%s", KEY, imdbID.trim());
            try {
               // parseMovieInfo(getRequest(jsonUrl), movie);
                String req = getRequest(jsonUrl);
                parseMovieInfo(req, movie);
                toDb();
            } catch (MalformedURLException e) {
                System.err.println(String.format("Incorrect URL <%s>!", jsonUrl));
                e.printStackTrace();
            } catch (UnknownHostException e) {
                System.err.println("Request timeout!");
                fromDB(imdbID);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                System.err.println("Some trouble occurred when parsing Json file.");
                e.printStackTrace();
            }
        }

        private void parseMovieInfo(String jsonText, Movie movie) throws ParseException {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);
            movie.addInfo((String) jsonObject.get("Rated"),
                    (String) jsonObject.get("Released"),
                    (String) jsonObject.get("Runtime"),
                    (String) jsonObject.get("Genre"),
                    (String) jsonObject.get("Director"),
                    (String) jsonObject.get("Writer"),
                    (String) jsonObject.get("Actors"),
                    (String) jsonObject.get("Plot"),
                    (String) jsonObject.get("Language"),
                    (String) jsonObject.get("Country"),
                    (String) jsonObject.get("Awards"),
                    (String) jsonObject.get("imdbRating"),
                    (String) jsonObject.get("imdbVotes"),
                    (String) jsonObject.get("Production"));
        }

        private String getRequest(String url) throws IOException {
            StringBuilder result = new StringBuilder();

            URL getReq = new URL(url);
            URLConnection connection = getReq.openConnection();
            //connection.setConnectTimeout(15 * 1000);
            //connection.setReadTimeout(15 * 1000);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                result.append(inputLine).append("\n");

            in.close();


            return result.toString();
        }

        private void fromDB(String imdbId){
            movie = appDatabase.movieDao().getMovieByImdbId(imdbId).createMovieInfo();
        }

        private void toDb(){
            new AsyncLoadMovieDetailsToDB().execute(movie);
        }
    }
}

