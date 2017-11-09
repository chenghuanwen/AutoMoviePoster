package com.hisilicon.videocenter.auto;

import java.util.List;

import com.hisilicon.videocenter.util.Movie;

public interface OnQueryLocalDataFinishedListener {
     void onQueryLocalDataFinish(List<Movie> movies);
}
