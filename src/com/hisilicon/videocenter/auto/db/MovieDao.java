package com.hisilicon.videocenter.auto.db;

import java.util.List;

import com.hisilicon.videocenter.auto.OnQueryLocalDataFinishedListener;
import com.hisilicon.videocenter.util.Movie;

public interface MovieDao {
  void queryLocalMovies(OnQueryLocalDataFinishedListener listenere);
  void addMovies(List<Movie> movies);
  void deleteMovies(int names);
}
