package ua.kpi.comsys.iv8224.app_mobiles.ui.notifications;

import android.graphics.Bitmap;

public class Movie {
    private String title;
    private String year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String language;
    private String country;
    private String awards;
    private String rating;
    private String votes;
    private String production;
    private String imdbID;
    private String type;
    private String poster;
    private Bitmap posterBitmap;


    public Movie(String title, String year, String imdbID, String type, String poster){
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.type = type;
        this.poster = poster;
    }

    public Movie(String title, String year, String imdbID, String type, Bitmap posterBitmap) {
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.type = type;
        this.posterBitmap = posterBitmap;
    }

    public void addInfo(String rated, String released, String runtime,
                        String genre, String director, String writer,
                        String actors, String plot, String language,
                        String country, String awards, String rating,
                        String votes, String production){
        this.rated = rated;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.plot = plot;
        this.language = language;
        this.country = country;
        this.awards = awards;
        this.rating = rating;
        this.votes = votes;
        this.production = production;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getPoster() {
        return poster;
    }

    public String getType() {
        return type;
    }

    public String getRating() {
        return rating;
    }

    public String getActors() {
        return actors;
    }

    public String getAwards() {
        return awards;
    }

    public String getCountry() {
        return country;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public String getPlot() {
        return plot;
    }

    public String getProduction() {
        return production;
    }

    public String getRated() {
        return rated;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getVotes() {
        return votes;
    }

    public String getWriter() {
        return writer;
    }

    public Bitmap getPosterBitmap() {
        return posterBitmap;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setPosterBitmap(Bitmap posterBitmap) {
        this.posterBitmap = posterBitmap;
    }
}
