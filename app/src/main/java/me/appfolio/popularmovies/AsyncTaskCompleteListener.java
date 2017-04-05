package me.appfolio.popularmovies;

interface AsyncTaskCompleteListener<T>
{
    void onTaskComplete(T result);
}